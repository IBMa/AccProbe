/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 
package org.a11y.utils.accprobe.actions;

import org.a11y.utils.accprobe.dialogs.EventFilterDialog;
import org.a11y.utils.accprobe.views.EventMonitorView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;


public class EventFilterAction extends AbstractAction {

	
	public static final String ID = "org.a11y.utils.accprobe.actions.EventFilterAction";
	
	public EventFilterAction() {
		super();
	}

	public void run(IAction action) {
		if (view instanceof EventMonitorView) {
			EventMonitorView ev = (EventMonitorView) view;
			Shell shell = ev.getSite().getShell(); 
			if (ev.getNode() == null  && !EventMonitorView.isGlobalMonitoringEnabled()) {
				MessageDialog.openError(
						shell, EventMonitorView.TITLE,
						"Events to watch are determined by the target selected.\n Please select an item in the Explorer View.");
			} else {
				EventFilterDialog dialog = new EventFilterDialog(shell, ev.getNode());
				if (dialog.open() == IDialogConstants.OK_ID) {
					ev.updateView();
				}
			}
			
		}

	}

}
