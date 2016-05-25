/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.actions;

import org.a11y.utils.accprobe.views.EventMonitorView;
import org.eclipse.jface.action.IAction;


public class ResumeEventDisplayAction extends AbstractAction
{

	public static final String ID = "org.a11y.utils.accprobe.actions.ResumeEventDisplayAction";

	public ResumeEventDisplayAction () {
		super();
	}

	public void run (IAction action) {
		if (view instanceof EventMonitorView) {
			EventMonitorView ev = (EventMonitorView) view;
			ev.enableToolbarButton(ID, false);
			ev.enableToolbarButton(PauseEventDisplayAction.ID, true);
			ev.setDisplayEvents(true);
		}
	}
}
