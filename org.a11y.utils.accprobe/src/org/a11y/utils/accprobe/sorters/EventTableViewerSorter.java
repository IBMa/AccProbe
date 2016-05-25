/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.sorters;

import java.text.Collator;

import org.a11y.utils.accprobe.providers.EventRecord;
import org.eclipse.jface.viewers.Viewer;


public class EventTableViewerSorter extends AbstractViewerSorter
{

	public static final int ROLE = 0;
	
	public static final int NAME = 1;
	
	public static final int TYPE = 2;

	public static final int TIMESTAMP = 3;
	
	public static final int STATE = 4;
	
	public static final int MISC = 5;


	public EventTableViewerSorter(Collator collator) {
		super(collator);
		setSortingField(TIMESTAMP, false);
	}


	public EventTableViewerSorter () {
		super();
		setSortingField(TIMESTAMP, false);
	}


	public int compare (Viewer viewer, Object e1, Object e2) {
		String s1 = null, s2 = null;
		long l1, l2;
		EventRecord r1 = (EventRecord) e1;
		EventRecord r2 = (EventRecord) e2;
		switch (sortingField) {
		case NAME:
			s1 = r1.getName();
			s2 = r2.getName();
			return performStringCompare(s1, s2);
		case ROLE:
			s1 = r1.getRole();
			s2 = r2.getRole();
			return performStringCompare(s1, s2);
		case STATE:
			s1 = r1.getState();
			s2 = r2.getState();
			return performStringCompare(s1, s2);
		case TYPE:
			s1 = r1.getType();
			s2 = r2.getType();
			return performStringCompare(s1, s2);
		case TIMESTAMP:
			l1 = r1.getTimestamp();
			l2 = r2.getTimestamp();
			return performLongCompare(l1, l2);
		case MISC:
			s1 = r1.getMiscData();
			s2 = r2.getMiscData();
			return performStringCompare(s1, s2);
		}
		return 0;
	}

}
