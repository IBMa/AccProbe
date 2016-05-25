/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 
package org.a11y.utils.accprobe.actions;

import org.a11y.utils.accprobe.views.PropertiesView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;



public class RefreshPropertyAction implements IObjectActionDelegate
{

	
	
	public static final String ID = "org.a11y.utils.accprobe.actions.RefreshPropertyAction";
	
	private PropertiesView view;
	
	

	public void setActivePart (IAction action, IWorkbenchPart targetPart) {
		if (targetPart instanceof PropertiesView) {
			view = (PropertiesView) targetPart;
		}
		
	}

	public void run (IAction action) {
		if (view != null && view.getTreeViewer().getInput() != null) {
			view.updateView();
		}
		
	}

	public void selectionChanged (IAction action, ISelection selection) {
		
	}
}
