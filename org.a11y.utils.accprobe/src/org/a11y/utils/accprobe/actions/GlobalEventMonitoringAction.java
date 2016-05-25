package org.a11y.utils.accprobe.actions;

import org.a11y.utils.accprobe.views.EventMonitorView;
import org.eclipse.jface.action.IAction;

public class GlobalEventMonitoringAction extends AbstractAction {

	public GlobalEventMonitoringAction() {
		super();
	}
	public void run(IAction action) {
		if (view instanceof EventMonitorView) {
			EventMonitorView ev = (EventMonitorView) view;
			if (EventMonitorView.isGlobalMonitoringEnabled()) {
				ev.disableGlobalMonitoring();
			}else {
				ev.enableGlobalMonitoring();
			}
			ev.removeEventListener();
			ev.registerEventListener();
		}
	}

}
