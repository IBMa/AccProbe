/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 


package org.a11y.utils.accprobe.actions;

import java.util.Map.Entry;

import org.a11y.utils.accprobe.dialogs.MethodInvocationDialog;
import org.a11y.utils.accprobe.views.PropertiesView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;


public class MethodInvocationAction implements IObjectActionDelegate
{

	public static final String METHODINVOCATION_ACTION_ID = "org.a11y.utils.accprobe.actions.methodInvocationAction";
	
	private PropertiesView _propertiesView;
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		if (targetPart instanceof PropertiesView) {
			_propertiesView = (PropertiesView) targetPart;
		}
	}

	public void run(IAction action) {
		if (_propertiesView != null && _propertiesView.getTreeViewer().getInput() != null) {
			IStructuredSelection selection = (IStructuredSelection) _propertiesView.getTreeViewer().getSelection();
			Shell shell = _propertiesView.getSite().getShell();
			Object target = ((Entry) selection.getFirstElement()).getValue();
			new MethodInvocationDialog(shell, target, null).open();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
