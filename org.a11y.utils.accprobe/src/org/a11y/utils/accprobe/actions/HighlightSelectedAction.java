/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 
package org.a11y.utils.accprobe.actions;

import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaAccessible;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchWindow;


public class HighlightSelectedAction extends Action
{
	
	public static final String ID = "org.a11y.utils.accprobe.actions.HighlightSelectionAction";


	public HighlightSelectedAction (IWorkbenchWindow window, String label) {
		super(label, IAction.AS_CHECK_BOX);
		// The id is used to refer to the action in a menu or toolbar
		setId(ID);
		//setChecked(true); //default value is true;
	}

	public void run () {
		if (isChecked()) {
			MsaaAccessible.setHighlightEnabled(true);
		}else {
			MsaaAccessible.setHighlightEnabled(false);
			MsaaAccessible.eraseDesktop();
		}
	}
}
