/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 
package org.a11y.utils.accprobe.providers;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class EventPreferencesLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	public static final int COLUMN_NAMES_COL = 0;

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int index) {
		String result = "";
		if (element instanceof String) {
			if (index == COLUMN_NAMES_COL) {
				result = element.toString();
			}
		}
		return result;
	}

	public String getText(Object element) {
		return getColumnText(element, COLUMN_NAMES_COL);
	}

}
