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
import org.a11y.utils.accprobe.views.AbstractViewPart;
import org.a11y.utils.accprobe.views.ExplorerView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchWindow;


public class CaretPositionAction extends Action
{
	
	public static final String ID = "org.a11y.utils.accprobe.actions.KeyboardFocusAction";
	
	private IWorkbenchWindow window;
	private IAccessibilityService accService = null;

	public CaretPositionAction (IWorkbenchWindow window, String label) {
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
		ExplorerView view = (ExplorerView) AbstractViewPart.findView(ExplorerView.ID);
		if (accService != null && accService instanceof MsaaAccessibilityService) {
			if (isChecked()) {
				if(getText().equals("&Caret position")){
					if(!view.registerCaretEventListener()){
						setChecked(false);
					}
				}
			}else {
				if(getText().equals("&Caret position")){
					view.removeCaretEventListener();
				}
			}
		}
		
	}

}
