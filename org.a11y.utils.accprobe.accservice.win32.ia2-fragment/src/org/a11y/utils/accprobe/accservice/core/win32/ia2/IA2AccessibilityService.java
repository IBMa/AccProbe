/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core.win32.ia2;

import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaAccessibilityService;
import org.a11y.utils.accprobe.accservice.AccessibilityServiceException;
import org.a11y.utils.accprobe.accservice.IAccessibilityEventService;
import org.a11y.utils.accprobe.accservice.IWindowService;
import org.a11y.utils.accprobe.accservice.core.IAccessibleElement;
import org.a11y.utils.accprobe.core.logging.LoggingUtil;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;


public class IA2AccessibilityService extends MsaaAccessibilityService
{


	private static final long serialVersionUID = -3956586839108596041L;

	/**
	 * name of dynamic module for IA2 access; value is 'a11y-ia2'
	 */
	public static final String A11Y_IA2_LIBRARY_NAME = "a11y-ia2";
	
	/**
	 * accessibility service name for this service; value is 'ia2'
	 */
	public static final String IA2_ACCSERVICE_NAME = "ia2";
	
	private IWindowService _windowService = new IA2WindowService();
	private IAccessibilityEventService _accEventService = new IA2AccessibilityEventService(_windowService);
	
	/** {@inheritDoc} */
	public IAccessibleElement createAccessibleElement(Object obj, Object[] params)
			throws InvalidComponentException {
		IA2AccessibilityService.internalCoInitialize();
		IA2Accessible ia2Acc = null;
		if (obj instanceof Point) {
			ia2Acc = new IA2Accessible((Point) obj);
		} else if (obj instanceof Integer) {
			int hwnd = ((Integer) obj).intValue();
			int childId = -1;
			if( params !=null){
				int length = params.length;

				if (length > 0 && (params[0] instanceof Integer)) {
					childId = ((Integer) params[0]).intValue();
				}
			}
			ia2Acc = new IA2Accessible(hwnd, childId);
		}

		return ia2Acc;
	}

	/** {@inheritDoc} */
	public IAccessibilityEventService getAccessibilityEventService() {
		return _accEventService;
	}

	/** {@inheritDoc} */
	public IWindowService getWindowService() {
		return _windowService;
	}

	public void initialize() throws AccessibilityServiceException {
		super.initialize();
		try {
			System.loadLibrary(A11Y_IA2_LIBRARY_NAME);
			}catch (Throwable e) {
			Logger.getLogger(LoggingUtil.ACCSERVICE_LOGGER_NAME).log(Level.WARNING, "unable to load library " + A11Y_IA2_LIBRARY_NAME, e);
		}
	}
	protected static native boolean internalCoInitialize();
	protected static native boolean internalCoUnInitialize();

}
