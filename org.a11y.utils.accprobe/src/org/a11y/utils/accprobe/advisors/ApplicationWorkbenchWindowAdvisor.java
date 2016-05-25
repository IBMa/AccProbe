/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.advisors;

import org.a11y.utils.accprobe.accservice.IAccessibilityService;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;


public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor
{

	public static final String TITLE = "Accessibility Probe";

	private int hwnd;

	private IAccessibilityService accService = null;

	private IWorkbenchWindowConfigurer windowConfigurer = null;

	public ApplicationWorkbenchWindowAdvisor (IWorkbenchWindowConfigurer configurer) {
		super(configurer);
		configurer.setShowProgressIndicator(true);
		windowConfigurer = configurer;
	}

	public ActionBarAdvisor createActionBarAdvisor (IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen () {
		windowConfigurer.setInitialSize(new Point(800, 600));
		windowConfigurer.setShowCoolBar(true);
		windowConfigurer.setShowStatusLine(false);
		windowConfigurer.setTitle(TITLE);
	}

	public void dispose () {
//		if (accService != null) {
//			accService.getWindowService().setWindowNoTop(hwnd);
//		}
		super.dispose();
	}

	public int getHwnd () {
		return hwnd;
	}

	public void setHwnd (int hwnd) {
		this.hwnd = hwnd;
	}
}
