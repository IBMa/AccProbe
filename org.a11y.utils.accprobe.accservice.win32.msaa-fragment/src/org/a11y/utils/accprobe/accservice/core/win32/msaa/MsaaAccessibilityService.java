/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core.win32.msaa;

import java.awt.Point;

import org.a11y.utils.accprobe.accservice.AccessibilityServiceException;
import org.a11y.utils.accprobe.accservice.IAccessibilityEventService;
import org.a11y.utils.accprobe.accservice.IAccessibilityService;
import org.a11y.utils.accprobe.accservice.IWindowService;
import org.a11y.utils.accprobe.accservice.core.IAccessibleElement;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;


public class MsaaAccessibilityService implements IAccessibilityService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5517183072097924795L;

	/**
	 * name of dynamic module for MSAA access; value is 'a11y-msaa'
	 */
	public static final String A11Y_MSAA_LIBRARY_NAME = "a11y-msaa";

	/**
	 * accessibility service name for this service; value is 'msaa'
	 */
	public static final String MSAA_ACCSERVICE_NAME = "msaa";

	private IWindowService _windowService = new MsaaWindowService();
	private IAccessibilityEventService _accEventService = new MsaaAccessibilityEventService(_windowService);
	
	public IAccessibleElement createAccessibleElement(Object obj, Object[] params)
			throws InvalidComponentException {
		MsaaAccessibilityService.internalCoInitialize();
		MsaaAccessible mAcc = null;

		if (obj instanceof Point) {
			mAcc = new MsaaAccessible((Point) obj);
		} else if (obj instanceof Integer) {
			int hwnd = ((Integer) obj).intValue();
			int childId = -1;
			if (params != null) {
				int length = params.length;

				if (length == 1 && (params[0] instanceof Integer)) {
					childId = ((Integer) params[0]).intValue();
				}
			}
			mAcc = new MsaaAccessible(hwnd, childId);
		} else {
			mAcc = new MsaaAccessible(obj);
		}

		return mAcc;
	}

	public IAccessibilityEventService getAccessibilityEventService() {
		return _accEventService;
	}

	public IWindowService getWindowService() {
		return _windowService;
	}

	public void initialize() throws AccessibilityServiceException {
		try {
			System.loadLibrary(A11Y_MSAA_LIBRARY_NAME);
		} catch (Throwable e) {
				throw new AccessibilityServiceException("Unable to load library " + A11Y_MSAA_LIBRARY_NAME, e);
		}
	}
	
	protected static native boolean internalCoInitialize();
	protected static native boolean internalCoUnInitialize();


}
