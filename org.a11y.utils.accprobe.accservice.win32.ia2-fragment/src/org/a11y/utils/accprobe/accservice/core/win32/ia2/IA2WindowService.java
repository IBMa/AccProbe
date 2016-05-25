/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/ 


package org.a11y.utils.accprobe.accservice.core.win32.ia2;

import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaAccessible;
import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaWindowService;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;


/**
 * @author <a href="mailto:masquill@us.ibm.com>Mike Squillace</a>
 *
 */
public class IA2WindowService extends MsaaWindowService
{

	private static final long serialVersionUID = -4191226662163560401L;

	/**
	 * not to be used by clients; made public so that native code can reference
	 * 
	 * @param hwnd
	 * @throws InvalidComponentException
	 */
	protected void windowCallback (int hwnd) throws InvalidComponentException {
		IA2Accessible ca = new IA2Accessible(hwnd, MsaaAccessible.childId_self);
		fireTopLevelWindowEvent(ca);
	}

}
