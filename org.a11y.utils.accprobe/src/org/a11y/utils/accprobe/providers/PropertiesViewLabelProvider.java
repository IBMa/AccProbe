/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.providers;

import java.util.Map.Entry;

import org.a11y.utils.accprobe.GuiUtils;
import org.a11y.utils.accprobe.accservice.core.IAccessibleElement;
import org.a11y.utils.accprobe.filters.PropertyFilter;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


public class PropertiesViewLabelProvider extends LabelProvider
implements ITableLabelProvider {

	public static final int NAME_COLUMN = 0;

	public static final int VALUE_COLUMN = 1;

	public PropertiesViewLabelProvider() {
		super();
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		String result = "<null>";
		
		if (element instanceof Entry) {
			Entry entry = (Entry) element;
			String key =  (String) entry.getKey();
			switch (columnIndex) {
			case NAME_COLUMN:
				result = (String) entry.getKey();
				break;
			case VALUE_COLUMN:
				Object val = entry.getValue();
				if( PropertyFilter.isPropertyGroup(entry)){
					result = null;
				}else if (val != null) {
					Class<?> type = val.getClass();
					if (type.isArray()&& !type.getComponentType().isPrimitive()) {
						String typeName = type.getComponentType().getName();
						String shortName = typeName.substring(typeName.lastIndexOf('.') + 1);
						result = shortName + "[" + ((Object[]) val).length + "]";
					} else if (GuiUtils.isPrimitive(type) || GuiUtils.hasAcceptableToString(type)) {
						result = val.toString();
					} else {
						String typeName = type.getName();
						result = "class " + typeName.substring(typeName.lastIndexOf('.') + 1);
						if(val instanceof IAccessibleElement){
							result = result + "="+ val.toString();
						}
					}
				}
				break;
			}
		}

		return result;
	}

}
