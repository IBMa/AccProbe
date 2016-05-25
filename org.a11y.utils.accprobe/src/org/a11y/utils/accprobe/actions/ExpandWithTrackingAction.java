package org.a11y.utils.accprobe.actions;

import org.a11y.utils.accprobe.views.ExplorerView;
import org.eclipse.jface.action.IAction;

public class ExpandWithTrackingAction extends AbstractAction {

	public ExpandWithTrackingAction() {
		super();
	}
	public void run(IAction action) {
		if (view instanceof ExplorerView) {
			ExplorerView ex = (ExplorerView) view;
			if (ExplorerView.isExpansionEnabled()) {
				ex.disableExpansion();
			}else {
				ex.enableExpansion();
			}
		}
	}

	
}
