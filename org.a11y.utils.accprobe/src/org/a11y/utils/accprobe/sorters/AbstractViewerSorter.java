/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.sorters;

import java.text.Collator;

import org.eclipse.jface.viewers.ViewerSorter;

public class AbstractViewerSorter extends ViewerSorter
{

	
	
	protected int sortingField;
	
	private boolean ascending;

	public AbstractViewerSorter () {
		super();
	}
	public AbstractViewerSorter (Collator collator) {
		super(collator);
	}

	protected int performIntCompare (int i1, int i2) {
		if (ascending) {
			return i1 - i2;
		}else {
			return i2 - i1;
		}
	}

	protected int performLongCompare (long l1, long l2) {
		if (ascending) {
			return (int) (l1 - l2);
		}else {
			return (int) (l2 - l1);
		}
	}

	protected int performStringCompare (String s1, String s2) {
		if (ascending) {
			return s1.compareTo(s2);
		}else {
			return s2.compareTo(s1);
		}
	}

	/**
	 * @param sortingField The sortingField to set.
	 */
	public void setSortingField (int sortingField) {
		if (this.sortingField == sortingField) {
			ascending = !ascending;
		}else {
			ascending = true;
		}
		this.sortingField = sortingField;
	}

	public void setSortingField (int sortBy, boolean direction) {
		this.sortingField = sortBy;
		ascending = direction;
	}

	public boolean isSorterProperty (Object element, String property) {
		return true;
	}

	
	public boolean isAscending () {
		return ascending;
	}

	
	public void setAscending (boolean ascending) {
		this.ascending = ascending;
	}
}