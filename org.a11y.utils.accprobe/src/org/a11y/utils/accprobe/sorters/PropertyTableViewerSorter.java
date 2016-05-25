/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.sorters;

import java.util.Map.Entry;

import org.a11y.utils.accprobe.Activator;
import org.a11y.utils.accprobe.dialogs.PropertiesSortDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.Viewer;

public class PropertyTableViewerSorter extends AbstractViewerSorter {
	public static final int PROPERTY_ALPHA = 0;
	public static final int PROPERTY_NUMERIC = 1;

	public PropertyTableViewerSorter() {
		super();
		IDialogSettings settings = Activator.getDefault().getDialogSettings().getSection(PropertiesSortDialog.ID);
		if (settings == null) {
			setSortingField(PROPERTY_ALPHA, true);
		} else {
			setSortingField(PROPERTY_NUMERIC, true);
		}
	}

	public int compare(Viewer viewer, Object e1, Object e2) {
		// input is a HashMap entry
		String s1 = null, s2 = null;
		int i1 = Integer.MAX_VALUE, i2 = Integer.MAX_VALUE;
		Entry he1 = (Entry) e1;
		Entry he2 = (Entry) e2;
		int result = 0;

		switch (sortingField) {
			case PROPERTY_ALPHA:
				s1 = he1.getKey().toString();
				s2 = he2.getKey().toString();
				result = performStringCompare(s1, s2);
				break;
			case PROPERTY_NUMERIC:
				IDialogSettings settings = Activator.getDefault().getDialogSettings().getSection(PropertiesSortDialog.ID);
				if (settings != null) {
					String prop1 = (String) he1.getKey();
					String prop2 = (String) he2.getKey();
					if (prop1 != null && prop2 != null) {
						String[] sortedProps = settings.getArray(PropertiesSortDialog.SORT_ORDER_KEY);
						int index1 = -1, index2 = -1;
						for (int i = 0; index1 == -1 && index2 == -1 && i < sortedProps.length; ++i) {
							if (sortedProps[i].indexOf(prop1) >= 0) {
								index1 = i;
							} else if (sortedProps[i].indexOf(prop2) >= 0) {
								index2 = i;
							}
						}
						
						if (index1 >= 0) {
							int indexDelim = sortedProps[index1].indexOf(PropertiesSortDialog.INDEX_DELIMITER);
							i1 = Integer.parseInt(sortedProps[index1].substring(indexDelim + PropertiesSortDialog.INDEX_DELIMITER.length()));
						}
						if (index2 >= 0) {
							int indexDelim = sortedProps[index2].indexOf(PropertiesSortDialog.INDEX_DELIMITER);
							i2 = Integer.parseInt(sortedProps[index2].substring(indexDelim + PropertiesSortDialog.INDEX_DELIMITER.length()));
						}
						result = performIntCompare(i1, i2);
					}
				}
				break;
		}

		return result;
	}

	public void updateSortOrder() {
		setSortingField(PROPERTY_NUMERIC, true);
	}

}
