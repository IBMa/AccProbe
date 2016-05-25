/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 
package org.a11y.utils.accprobe.actions;

import org.a11y.utils.accprobe.dialogs.PropertiesFilterDialog;
import org.a11y.utils.accprobe.views.PropertiesView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;


public class PropertiesFilterAction extends AbstractAction {
	
	
	public PropertiesFilterAction() {
		super();
	}

	public void run(IAction action) {
		if (view instanceof PropertiesView) {
			PropertiesView pv = (PropertiesView) view;
			Shell shell = pv.getSite().getShell();
			if (pv.getNode() == null) {
				MessageDialog.openError(
						shell, PropertiesView.TITLE,
						"No properties to filter.\n"
								+ "Please select an item in the Explorer View.");
			} else {
				PropertiesFilterDialog dialog = new PropertiesFilterDialog(shell, pv.getNode(), pv);
				if (dialog.open() == IDialogConstants.OK_ID) {
					pv.updateView();
				}
			}
		}

	}

}
