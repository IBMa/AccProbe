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

package org.a11y.utils.accprobe.accservice.core.win32.ia2;

import java.util.Set;

import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaGuiModel;
import org.a11y.utils.accprobe.accservice.adapt.win32.ia2.IA2AccessibilityAdaptor;
import org.a11y.utils.accprobe.accservice.core.AccessibleConstants;
import org.a11y.utils.accprobe.accservice.core.AccessibleElementTreeWalker;
import org.a11y.utils.accprobe.core.adapt.DefaultAdaptorFactory;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;
import org.a11y.utils.accprobe.core.model.events.IModelEventListener;
import org.a11y.utils.accprobe.core.model.events.ModelEventType;


public class IA2GuiModel extends MsaaGuiModel {

	/**
	 * model type for IA2; value is 'ia2'
	 */
	public static final String IA2_MODEL = "ia2";

	/**
	 * create a new IA2GuiModel
	 */
	public IA2GuiModel() {
		super(IA2_MODEL);
		treeNodeWalker = new AccessibleElementTreeWalker();
		DefaultAdaptorFactory.getInstance().registerAdaptor(IA2Accessible.class, new IA2AccessibilityAdaptor());
	}

	protected Class<?> getAccessibleElementType() {
		return IA2Accessible.class;
	}

	protected Class<?> getAccessibilityEventServiceType() {
		return IA2AccessibilityEventService.class;
	}

	protected void initEventIdMap() {
		super.initEventIdMap();
		initEventIdMap(getAccessibleElementType(),
				getAccessibilityEventServiceType(), "IA2_EVENT_");
	}

	/** {@inheritDoc} */
	public boolean isVisible(Object comp) {
		boolean result = false;
		try {
			Set state = ((IA2Accessible) comp).getAccessibleState();
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
		if (comp != null && comp instanceof IA2Accessible) {
			try {
				((IA2Accessible) comp).checkIsValid();
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

	public boolean isTopDown() {
		return false;
	}

	/** {@inheritDoc} */
	public void registerModelEventListener(IModelEventListener listener, ModelEventType[] eventTypes, Object[] params) {
		registerModelEventListener(listener, eventTypes, params, IA2AccessibilityService.IA2_ACCSERVICE_NAME);
	}

	/** {@inheritDoc} */
	public void unregisterModelEventListener(IModelEventListener listener, ModelEventType[] eventTypes) {
		unregisterModelEventListener(listener, eventTypes, IA2AccessibilityService.IA2_ACCSERVICE_NAME);
	}
	
}
