/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.core.model.traverse;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.a11y.utils.accprobe.core.config.ConfigurationException;
import org.a11y.utils.accprobe.core.config.IConfiguration;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;
import org.a11y.utils.accprobe.core.model.traverse.filters.INodeFilter;
import org.a11y.utils.accprobe.core.resources.ClassLoaderCache;
import org.a11y.utils.accprobe.core.runtime.IRuntimeContext;
import org.a11y.utils.accprobe.core.runtime.RuntimeContextFactory;


/**
 * base implementation for any tree walker
*
 * @author Mike Squillace
 */
public abstract class AbstractTreeNodeWalker extends AbstractNodeWalker
	implements ITreeNodeWalker
{

	protected ClassLoaderCache clCache = ClassLoaderCache.getDefault();
	protected IConfiguration configuration;
	protected Map componentBridgeMap;

	public AbstractTreeNodeWalker () {
		try {
			IRuntimeContext context = RuntimeContextFactory.getInstance().getRuntimeContext();
			configuration = context.getConfiguration();
		}catch (ConfigurationException e) {
		}
	}

	/** {@inheritDoc} */
	public void setComponentBridgeMap (Map bridgeMap) {
		componentBridgeMap = bridgeMap;
	}

	protected Object getBridgedChild (Object parent) {
		return componentBridgeMap != null ? componentBridgeMap.get(parent) : null;
	}

	protected Object getBridgedParent (Object child) {
		Object parent = null;
		if (componentBridgeMap != null) {
			for (Iterator iter = componentBridgeMap.keySet().iterator(); iter.hasNext() & parent == null;) {
				Object p = iter.next();
				if (componentBridgeMap.get(p).equals(child)) {
					parent = p;
				}
			}
		}
		return parent;
	}

	/** {@inheritDoc} */
	public boolean hasChildren (Object element) throws InvalidComponentException {
		return getChildren(element).length > 0
				|| getBridgedChild(element) != null;
	}

	/** {@inheritDoc} */
	public Object[] getSuccessorNodes (Object node) throws InvalidComponentException {
		return getChildren(node);
	}
	
	/**
	 * default implementation returns the child at the specified index in the array returned by 
	 * a call to <code>getChildren</code>. This method should be overwritten by clients (if possible) 
	 * for the sake of performance.
	 * 
	 * @param index
	 * @return child at specified index in array of children returned by <code>getChildren</code>
	 * @throws InvalidComponentException
	 */
	public Object getChild (Object element, int index) throws InvalidComponentException {
		Object[] children = getChildren(element);
		Object child = null;
		if (index < children.length) {
			child = children[index];
		}
		return child;
	}
	
	/** {@inheritDoc} */
	public Object[] getFilteredSuccessorNodes (Object node) throws InvalidComponentException {
		return getFilteredChildren(node);
	}
	
	/** {@inheritDoc} */
	public Object[] getFilteredChildren (Object parent) throws InvalidComponentException {
		Object[] children = getChildren(parent);
		LinkedList filtered = new LinkedList();

		if(!nodeFilters.isEmpty()){
			if (children != null && children.length > 0) {
				// admit each child only if it passes all filters
				for (int c = 0; c < children.length; ++c) {
					Object child = children[c];
					boolean pass = true;
					for (Iterator iter = nodeFilters.iterator(); pass && iter.hasNext(); ) {
						pass = ((INodeFilter) iter.next()).pass(child);
					}
					if (pass) {
						filtered.add(child);
					}
				}
			}
		}
		
		return !nodeFilters.isEmpty() ? filtered.toArray() : children;
	}
	
	/** {@inheritDoc} */
	public Object[] getPredecessorNodes (Object node) throws InvalidComponentException {
		Object parent = getParent(node);
		return parent != null ? new Object[] {parent} : new Object[0];
	}
	
} // AbstractTreeWalker
