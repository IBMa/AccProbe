/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 
package org.a11y.utils.accprobe.providers;

import org.a11y.utils.accprobe.core.model.events.ModelEventType;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


public class EventFilterLabelProvider extends LabelProvider
	implements ITableLabelProvider
{
	
	public static final int EVENT_COL = 0;

	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getColumnText(Object element, int index) {
		String result = "";
		if (element instanceof ModelEventType) {			
			if (index == EVENT_COL) {
				result = ((ModelEventType) element).getEventName();
			}
		}
		return result;
	}
	
	public String getText (Object element) {
		return getColumnText(element, EVENT_COL);
	}

}
