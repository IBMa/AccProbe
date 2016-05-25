/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.views;

import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

/**
 * @author barryf
 *
 */
public class ViewerControlAdapter extends ControlAdapter
{

	private Control control;

	public ViewerControlAdapter (Control c) {
		control = c;
	}

	public void controlResized (ControlEvent e) {
		Point s = ((Control) e.getSource()).getSize();
		control.setSize(s);
	}
}
