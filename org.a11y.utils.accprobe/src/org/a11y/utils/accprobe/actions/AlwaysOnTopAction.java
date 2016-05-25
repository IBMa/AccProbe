/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 
package org.a11y.utils.accprobe.actions;

import org.a11y.utils.accprobe.accservice.AccessibilityServiceException;
import org.a11y.utils.accprobe.accservice.AccessibilityServiceManager;
import org.a11y.utils.accprobe.accservice.IAccessibilityService;
import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaAccessibilityService;
import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaWindowService;
import org.a11y.utils.accprobe.views.AbstractViewPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;


public class AlwaysOnTopAction extends Action
{
	
	public static final String ID = "org.a11y.utils.accprobe.actions.AlwaysOnTopAction";

	private final IWorkbenchWindow window;

	private IAccessibilityService accService = null;

	public AlwaysOnTopAction (IWorkbenchWindow window, String label) {
		super(label, IAction.AS_CHECK_BOX);
		this.window = window;
		try {
			accService = AccessibilityServiceManager.getInstance().getAccessibilityService(
				MsaaAccessibilityService.MSAA_ACCSERVICE_NAME);
		}catch (AccessibilityServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// The id is used to refer to the action in a menu or toolbar
		setId(ID);
		//setChecked(true); //default value is true;
	}

	public void run () {
		if(AbstractViewPart.isActionSuspended()){			
			setChecked(!isChecked());
			MessageDialog.openError(
					window.getShell(),"Always on Top",
					"Please resume all actions before changing this option");
			return;
		}
		if (accService != null && accService instanceof MsaaAccessibilityService) {
			MsaaWindowService msaaService = (MsaaWindowService) accService.getWindowService();
			if (isChecked()) {
				msaaService.setWindowOnTop(window.getShell().handle);
			}else {
				msaaService.setWindowNoTop(window.getShell().handle);
			}
		}
	}

}
