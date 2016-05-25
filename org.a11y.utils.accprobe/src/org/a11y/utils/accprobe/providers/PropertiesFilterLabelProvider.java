/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 
package org.a11y.utils.accprobe.providers;

import java.util.Map.Entry;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class PropertiesFilterLabelProvider extends LabelProvider implements
		IBaseLabelProvider {
	
	
	public static final int PROPERTY_COL = 0;


	public PropertiesFilterLabelProvider() {
		super();
	}

	public Image getColumnImage(Object element, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getText(Object element) {
		String result = "";
		if (element instanceof Entry) {
			result = (String) ((Entry)element).getKey();
		}
		return result;
	}

}
