/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation.
*
*
*
 *
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.a11y.utils.accprobe.accservice.core.win32.msaa;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.a11y.utils.accprobe.accservice.AccessibilityServiceException;
import org.a11y.utils.accprobe.accservice.AccessibilityServiceManager;
import org.a11y.utils.accprobe.accservice.IAccessibilityEventService;
import org.a11y.utils.accprobe.accservice.IAccessibilityService;
import org.a11y.utils.accprobe.accservice.adapt.win32.msaa.MsaaAccessibilityAdaptor;
import org.a11y.utils.accprobe.accservice.core.AccessibleConstants;
import org.a11y.utils.accprobe.accservice.core.AccessibleElementTreeWalker;
import org.a11y.utils.accprobe.accservice.core.IAccessibleElement;
import org.a11y.utils.accprobe.accservice.locate.AccessibleNodeLocator;
import org.a11y.utils.accprobe.core.adapt.DefaultAdaptorFactory;
import org.a11y.utils.accprobe.core.model.AbstractRenderableModel;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;
import org.a11y.utils.accprobe.core.model.events.IModelEventListener;
import org.a11y.utils.accprobe.core.model.events.ModelEventType;
import org.a11y.utils.accprobe.core.model.locate.INodeLocator;
import org.a11y.utils.accprobe.core.model.traverse.INodeWalker;


/**
 * a model implementation for a hierarchy of IAccessible components used by the
 * Microsoft Active Accessibility (MSAA) model
*
 * @see <a
 *      href="http://msdn2.microsoft.com/en-us/library/ms696165.aspx">IAccessible
 *      interface</a>
 * @author <a href="mailto:masquill@us.ibm.com>Mike Squillace</a>
*
 */
public class MsaaGuiModel extends AbstractRenderableModel {

	public static final String MSAA_MODEL = "msaa";

	@SuppressWarnings("unused")
	private ModelEventType[] changeEventTypes = null;
	
	/**
	 * create a new MsaaModel
	 */
	public MsaaGuiModel() {
		super(MSAA_MODEL);
		DefaultAdaptorFactory.getInstance().registerAdaptor(MsaaAccessible.class, new MsaaAccessibilityAdaptor());
	}

	// This method should only be used by classes that extend this class
	protected MsaaGuiModel(String extendedAttrib) {
		super(extendedAttrib);
	}

	/** {@inheritDoc} */
	public INodeWalker getNodeWalker () {
		if ( treeNodeWalker == null ) {
			treeNodeWalker = new AccessibleElementTreeWalker();
			setFilters();
		}
		return treeNodeWalker;
	}

	/**
	 * {@inheritDoc} returns the accessibleRole of this element if it is of type
	 * <code>MsaaAccessible</code>
	 */
	public String getNodeName(Object element) {
		String accRole = null;
		if (element instanceof MsaaAccessible) {
			try {
				accRole = ((MsaaAccessible) element).getAccessibleRole();
			} catch (InvalidComponentException e) {
			}
		}
		return accRole;
	}

	/**
	 * {@inheritDoc} always returns <code>org.a11y.utils.accprobe.accservice.AccessibleElement</code>
	 */
	public String getTypeName (String nodeName) {
		return IAccessibleElement.class.getName();
	}

	protected Class<?> getAccessibleElementType() {
		return MsaaAccessible.class;
	}

	protected Class<?> getAccessibilityEventServiceType() {
		return MsaaAccessibilityEventService.class;
	}

	protected void initEventIdMap() {
		initEventIdMap(getAccessibleElementType(), getAccessibilityEventServiceType(), "EVENT_");
	}

	/** {@inheritDoc} */
	public ModelEventType[] getModelEventTypes(Class<?> sourceType) {
		return sourceType == null || sourceType.equals(getAccessibleElementType())
			? (ModelEventType[]) eventIdMap.values().toArray(new ModelEventType[eventIdMap.size()])
			: new ModelEventType[0];
	}

	/** {@inheritDoc}
	 * 
	 * The only optional parameter is an int that specifies flags for MSAA event-processing. If such a flag (or 
	 * combination of flags is not present, <code>DEFAULT_CONTEXT_FLAG</code> will be used.
	 */
	public void registerModelEventListener(IModelEventListener listener, ModelEventType[] eventTypes, Object[] params) {
		registerModelEventListener(listener, eventTypes, params, MsaaAccessibilityService.MSAA_ACCSERVICE_NAME);
	}

	protected void registerModelEventListener(IModelEventListener listener, ModelEventType[] eventTypes,
											  Object[] params, String accServiceName) {
		IAccessibilityService service = null;
		try {
			service = AccessibilityServiceManager.getInstance().getAccessibilityService(accServiceName);
		} catch (AccessibilityServiceException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
		
		if (service != null) {
			IAccessibilityEventService eventService = service.getAccessibilityEventService();
			if (eventService != null) {
				List<Object> actualIds = new ArrayList<Object>();
				for (int i = 0; i < eventTypes.length; ++i) {
					Object eventId = eventTypes[i].getEventId();
					if (eventIdMap.containsKey(eventId)) {
						actualIds.add(eventId);
					}
				}
				eventService.addAccessibilityEventListener(listener, actualIds.toArray(), params);
			}
		}
	}

	/** {@inheritDoc} */
	public void unregisterModelEventListener(IModelEventListener listener, ModelEventType[] eventTypes) {
		unregisterModelEventListener(listener, eventTypes, MsaaAccessibilityService.MSAA_ACCSERVICE_NAME);
	}
	
	protected void unregisterModelEventListener(IModelEventListener listener, ModelEventType[] eventTypes, String accServiceName) {
		IAccessibilityService service = null;
		try {
			service = AccessibilityServiceManager.getInstance().getAccessibilityService(accServiceName);
		} catch (AccessibilityServiceException e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
		
		if (service != null) {
			IAccessibilityEventService eventService = service.getAccessibilityEventService();
			if (eventService != null) {
				List<Object> actualIds = new ArrayList<Object>();
				for (int i = 0; i < eventTypes.length; ++i) {
					Object eventId = eventTypes[i].getEventId();
					if (eventIdMap.containsKey(eventId)) {
						actualIds.add(eventId);
					}
				}
				eventService.removeAccessibilityEventListener(listener, actualIds.toArray());
			}
		}
	}

	/** {@inheritDoc} */
	public boolean isVisible(Object comp) {
		boolean result = false;
		try {
			Set<String> state = ((MsaaAccessible) comp).getAccessibleState();
			if (state != null) {
				result = state.contains(AccessibleConstants.STATE_VISIBLE);
			}
		} catch (InvalidComponentException e) {
		}
		return result;
	}

	/** {@inheritDoc} */
	public boolean isValid(Object comp) {
		boolean result = false;
		if (comp != null && comp instanceof MsaaAccessible) {
			try {
				((MsaaAccessible) comp).checkIsValid();
				result = true;
			} catch (InvalidComponentException e) {
				result = false;
			}
		}
		return result;
	}

	/** {@inheritDoc} */
	public boolean requestFocusFor(Object comp) {
		return true;
	}

	/**
	 * will be invoked on the current thread
	 */
	public void asyncInvokeOnUIThread(Runnable runnable) {
		runnable.run();
	}

	/**
	 * will be invoked on the current thread
	 */
	public void invokeOnUIThread(Runnable runnable) {
		runnable.run();
	}

	public boolean isUIThread() {
		return true;
	}

	public boolean isTopDown() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.a11y.utils.accprobe.core.model.AbstractModel#getNodeLocator()
	 */
	@Override
	public INodeLocator getNodeLocator() {
		if (locator == null){
			locator = new AccessibleNodeLocator();
		}
		return locator;
	}

	public String[] getPackageNames() {
		return new String[0];
	}

}
