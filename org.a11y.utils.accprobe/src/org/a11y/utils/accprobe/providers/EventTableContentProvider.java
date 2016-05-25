/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.providers;

import org.a11y.utils.accprobe.views.EventMonitorView;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class EventTableContentProvider extends ArrayContentProvider
{


	private EventMonitorView ev = null;

	public EventTableContentProvider (EventMonitorView view) {
		ev = view;
	}

	public Object[] getElements (Object element) {
		return ev.getEvents().toArray();
	}

	/*
	 * input is assumed to be a ExplorerViewNode (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ArrayContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	public void inputChanged (Viewer viewer, Object oldInput, Object newInput) {
		if (oldInput != null) {
			ev.removeEventListener(oldInput);
		}
		if (newInput != null) {
			ev.registerEventListener(newInput);
		}
	}
}
