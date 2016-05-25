/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 
package org.a11y.utils.accprobe.actions;

import org.a11y.utils.accprobe.Activator;
import org.a11y.utils.accprobe.dialogs.EventViewPreferencesDialog;
import org.a11y.utils.accprobe.views.EventMonitorView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;


public class EventViewPreferencesAction extends AbstractAction implements
		IViewActionDelegate {

	public EventViewPreferencesAction() {
		super();
	}

	public void run(IAction action) {
		if (view instanceof EventMonitorView) {
			EventMonitorView ev = (EventMonitorView) view;
			Shell shell = Activator.getDefault().getWorkbench()
					.getActiveWorkbenchWindow().getShell();
			EventViewPreferencesDialog dialog = new EventViewPreferencesDialog(
					shell);
			if (dialog.open() == IDialogConstants.OK_ID) {
				ev.viewPreferencesChanged();
			}
		}

	}

}
