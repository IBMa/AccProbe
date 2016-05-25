/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.actions;

import java.io.File;

import org.a11y.utils.accprobe.views.EventMonitorView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;


public class SaveToFileAction extends AbstractAction
{

	public static final String ID = "org.a11y.utils.accprobe.actions.SaveToFileAction";

	public SaveToFileAction () {
		super();
	}

	public void run (IAction action) {
		if (view instanceof EventMonitorView) {
			EventMonitorView ev = (EventMonitorView) view;
			if (action.isChecked()) {
				Shell shell = ev.getSite().getShell();
				FileDialog dialog = new FileDialog(shell, SWT.SAVE);
				dialog.setFileName("accProbeLog");
				dialog.setFilterNames(new String[] {"Text files (*.txt)"});
				dialog.setFilterExtensions(new String[] {"*.txt"});
				String filename = dialog.open();
				if (filename != null && filename.length() > 0) {
					File f = new File(filename);
					if (f.exists()) {
						boolean ok = MessageDialog.openConfirm(
							shell, "Confirm overwrite",
							"This file will be overwritten.\n Are you sure?");
						if (ok) {
							ev.setLogEvents(filename, true);
						} else {
							ev.setLogEvents(null, false);
							action.setChecked(false);
						}
					}else {
						ev.setLogEvents(filename, true);
					}
				}
			}else {
				ev.setLogEvents(null, false);
			}
		}
	}
}
