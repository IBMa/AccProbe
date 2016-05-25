/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;

public class ResetViewsAction extends Action
{

	public static final String ID = "org.a11y.utils.accprobe.actions.ResetViewsAction";

	private final IWorkbenchWindow window;

	public ResetViewsAction (IWorkbenchWindow window, String label) {
		super(label);
		this.window = window;
		// The id is used to refer to the action in a menu or toolbar
		setId(ID);
	}

	public void run () {
		window.getActivePage().resetPerspective();
	}
}
