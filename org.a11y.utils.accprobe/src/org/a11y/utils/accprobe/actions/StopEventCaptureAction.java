/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.actions;

import org.a11y.utils.accprobe.views.EventMonitorView;
import org.eclipse.jface.action.IAction;


public class StopEventCaptureAction extends AbstractAction
{

	public static final String ID = "org.a11y.utils.accprobe.actions.StopEventCaptureAction";

	public StopEventCaptureAction () {
		super();
	}

	public void run (IAction action) {
		if (view instanceof EventMonitorView) {
			EventMonitorView ev = (EventMonitorView) view;
			ev.enableToolbarButton(StartEventCaptureAction.ID, true);
			ev.enableToolbarButton(ID, false);
			ev.enableToolbarButton(PauseEventDisplayAction.ID, false);
			ev.enableToolbarButton(ResumeEventDisplayAction.ID, false);
			ev.removeEventListener();
			ev.setCaptureEvents(false);
		}
	}
}
