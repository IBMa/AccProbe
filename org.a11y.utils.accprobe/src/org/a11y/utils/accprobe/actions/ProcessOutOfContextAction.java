/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.actions;

import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaAccessibilityEventService;
import org.a11y.utils.accprobe.views.EventMonitorView;
import org.eclipse.jface.action.IAction;


public class ProcessOutOfContextAction extends AbstractAction
{

	public static final String ID = "org.a11y.utils.accprobe.actions.ProcessOutOfContextAction";

	public static final int CONTEXT_FLAG = MsaaAccessibilityEventService.WINEVENT_OUTOFCONTEXT
			| MsaaAccessibilityEventService.WINEVENT_SKIPOWNPROCESS;

	public ProcessOutOfContextAction () {
		super();
	}

	public void run (IAction action) {
		if (view instanceof EventMonitorView) {
			EventMonitorView ev = (EventMonitorView) view;
			if (action.isChecked()) {
				toggleMenuAction(ev, ProcessInContextAction.ID, false);
				ev.removeEventListener();
				ev.setContextFlag(MsaaAccessibilityEventService.WINEVENT_OUTOFCONTEXT);
				ev.registerEventListener();
			}
		}
	}
}
