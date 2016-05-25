/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.actions;

import org.a11y.utils.accprobe.views.EventMonitorView;
import org.eclipse.jface.action.IAction;


public class PauseEventDisplayAction extends AbstractAction
{

	public static final String ID = "org.a11y.utils.accprobe.actions.PauseEventDisplayAction";

	public PauseEventDisplayAction () {
		super();
	}

	public void run (IAction action) {
		if (view instanceof EventMonitorView) {
			EventMonitorView ev = (EventMonitorView) view;
			ev.enableToolbarButton(ResumeEventDisplayAction.ID, true);
			ev.enableToolbarButton(ID, false);
			ev.setDisplayEvents(false);
		}
	}
}
