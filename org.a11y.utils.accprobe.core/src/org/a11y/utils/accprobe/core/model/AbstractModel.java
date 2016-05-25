/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation.
*
*
*
 *
 *
 * Contributors:
 *  IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.a11y.utils.accprobe.core.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.a11y.utils.accprobe.core.config.ConfigurationException;
import org.a11y.utils.accprobe.core.config.IConfiguration;
import org.a11y.utils.accprobe.core.logging.LoggingUtil;
import org.a11y.utils.accprobe.core.model.events.IModelChangeListener;
import org.a11y.utils.accprobe.core.model.events.ModelChangeEvent;
import org.a11y.utils.accprobe.core.model.locate.INodeLocator;
import org.a11y.utils.accprobe.core.model.traverse.INodeWalker;
import org.a11y.utils.accprobe.core.model.traverse.ITreeNodeWalker;
import org.a11y.utils.accprobe.core.model.traverse.filters.INodeFilter;
import org.a11y.utils.accprobe.core.model.traverse.filters.NodeIdFilter;
import org.a11y.utils.accprobe.core.model.traverse.filters.NodeNameFilter;
import org.a11y.utils.accprobe.core.model.traverse.filters.NodeTypeFilter;
import org.a11y.utils.accprobe.core.resources.ClassLoaderCache;
import org.a11y.utils.accprobe.core.runtime.IRuntimeContext;
import org.a11y.utils.accprobe.core.runtime.RuntimeContextFactory;

/**
 * provides basic services for any model instance. Note that the
 * IRenderableModel interface must be implemented as well by clients that wish
 * to validate structures via validation documents using the A11Y Engine.
*
 * @see org.a11y.utils.accprobe.core.model.IRenderableModel
 * @author Mike Squillace
 */
public abstract class AbstractModel implements IModel {

	protected IConfiguration configuration;
	protected IRuntimeContext runtimeContext = RuntimeContextFactory
			.getInstance().getRuntimeContext();
	protected INodeLocator locator;
	protected ITreeNodeWalker treeNodeWalker;
	protected String baseType;
	protected Properties nodeToTypeNameMap = new Properties();

	private List changeListenerList = new LinkedList();
	private String _type;
	protected Logger logger = Logger.getLogger(LoggingUtil.A11Y_CORE_LOGGER_NAME);

	/**
	 * create a new model
	 * 
	 * @param type
	 *            - type of model
	 */
	public AbstractModel(String type) {
		_type = type;
		try {
			configuration = runtimeContext.getConfiguration();
		} catch (ConfigurationException e) {
			logger.log(Level.WARNING,
					"Trouble instantiating model of type " + type
					+ " - no configuration object", e);
		}
	}

	/** {@inheritDoc} */
	public String getName() {
		return _type;
	}

	protected void setModelType(String type) {
		_type = type;
	}

	/** {@inheritDoc} */
	public void setNodeID(Object comp, String id) {
	}

	/**
	 * default implementation returns the hexadecimal representation of
	 * <code>System.identityHashCode</code> of the given element.
	 * 
	 * @see java.lang.System#identityHashCode(Object)
	 * 
	 */
	public String getNodeId(Object element) {
		int id = System.identityHashCode(element);
		String xid = Integer.toHexString(id).toUpperCase();
		return "00000000".substring(0, 8 - xid.length()) + xid;
	}

	/**
	 * {@inheritDoc} returns the qualified class name (i.e. without the package
	 * name) of the given element
	 */
	public String getNodeName(Object element) {
		String typeName = element.getClass().getName();
		String nodeName = typeName.substring(typeName.lastIndexOf('.') + 1);
		nodeToTypeNameMap.setProperty(nodeName, typeName);
		return nodeName;
	}

	/**
	 * {@inheritDoc} returns the original class name from which this node name
	 * was derived or, if unsuccessful, tries to form the correct class based on
	 * the package names associated with this implementation
	 */
	public String getTypeName(String nodeName) {
		String typeName = nodeToTypeNameMap.getProperty(nodeName);
		if (typeName == null || typeName.length() == 0) {
			for (String packName : getPackageNames()) {
				Class<?> c = ClassLoaderCache.getDefault().classForName(
						packName + "." + nodeName);
				if (c != null) {
					typeName = c.getName();
					break;
				}
			}
		}
		return typeName;
	}

	/** {@inheritDoc} */
	public String getDefaultAliasPrefix() {
		return "";
	}

	/** {@inheritDoc} */
	public INodeWalker getNodeWalker() {
		setFilters();
		return treeNodeWalker;
	}

	/** {@inheritDoc} */
	public INodeLocator getNodeLocator() {
		return locator;
	}

	/** {@inheritDoc} */
	public void addModelChangeListener(IModelChangeListener listener) {
		if (listener != null) {
			changeListenerList.add(listener);
		}
	}

	/** {@inheritDoc} */
	public void removeModelChangeListener(IModelChangeListener listener) {
		if (listener != null) {
			changeListenerList.remove(listener);
		}
	}

	/**
	 * fire a ModelChangeEvent notification for this model
	 * 
	 * @param mce
	 *            the {@link ModelChangeEvent} The method triggered will vary
	 *            depending on the event type: NODE_INSERTED, NODE_REMOVED,
	 *            NODE_MODIFIED
	 */
	protected void fireModelChangeEvent(ModelChangeEvent mce) {
		int eventType = mce.getEventType();
		switch (eventType) {
		case ModelChangeEvent.NODE_INSERTED: {
			for (Iterator iter = changeListenerList.iterator(); iter.hasNext();) {
				((IModelChangeListener) iter.next()).nodeInserted(mce);
			}
			break;
		}
		case ModelChangeEvent.NODE_REMOVED: {
			for (Iterator iter = changeListenerList.iterator(); iter.hasNext();) {
				((IModelChangeListener) iter.next()).nodeRemoved(mce);
			}
			break;
		}
		case ModelChangeEvent.NODE_MODIFIED: {
			for (Iterator iter = changeListenerList.iterator(); iter.hasNext();) {
				((IModelChangeListener) iter.next()).nodeModified(mce);
			}
			break;
		}
		}
	}

	protected void setFilters() {
		//TODO Kavitha - if msaa and ia2 are not loaded under Webelo,
		//we get an exception here because the symbol pool for FILTER_ID has not yet been created.
		//We need to create it here, or even earlier.
		configuration.setSymbolPool(IConfiguration.FILTER_ID);
		HashMap filterMap = (HashMap) configuration.getParameter(_type);
		if (filterMap != null) {
			String filterClass = (String) filterMap
					.get(IConfiguration.FILTER_CLASSNAME_ATTRIBUTE);

			if (filterClass != null) {
				INodeFilter filter = null;
				try {
					filter = (INodeFilter) ClassLoaderCache.getDefault()
							.classForName(filterClass).newInstance();
				} catch (Exception e) {
					Logger.getLogger(LoggingUtil.A11Y_CORE_LOGGER_NAME)
						.log(Level.WARNING, e.getMessage(), e);
				}
				if (filter != null) {
					treeNodeWalker.addNodeFilter(filter);
				}
			} else {
				// NodeNameFilters
				String names = (String) filterMap
						.get(IConfiguration.FILTER_NODENAMES_ATTRIBUTE);
				if (names != null && names.length() > 0) {
					NodeNameFilter nameFilter = new NodeNameFilter(this, names);
					treeNodeWalker.addNodeFilter(nameFilter);
				}
				// NodeIdFilters
				String ids = (String) filterMap
						.get(IConfiguration.FILTER_NODEIDS_ATTRIBUTE);
				if (ids != null && ids.length() > 0) {
					NodeIdFilter idFilter = new NodeIdFilter(this, ids);
					treeNodeWalker.addNodeFilter(idFilter);
				}
				// NodeTypeFilters

				String types = (String) filterMap
						.get(IConfiguration.FILTER_NODETYPES_ATTRIBUTE);
				if (types != null && types.length() > 0) {
					NodeTypeFilter typeFilter = new NodeTypeFilter(this, types);
					treeNodeWalker.addNodeFilter(typeFilter);
				}

			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.a11y.utils.accprobe.core.model.IModel#getBaseType()
	 */
	public String getBaseType() {
		if (baseType == null) {
			configuration.setSymbolPool(IConfiguration.MODEL_ID);
			HashMap modelMap = (HashMap) configuration.getParameter(_type);
			baseType = (String) modelMap.get(IConfiguration.MODEL_BASE_TYPE);
		}
		return baseType;
	}

	// These next methods are stubs.
	public int getOrder(Object head) {
		return 1;
	}

} // AbstractModel
