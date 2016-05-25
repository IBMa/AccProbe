/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core.win32.ia2;

/**
 * @author kteegala
 *
 */

public class IA2TextSegment {
	
	public String text;
	public int start;
	public int end; 

	public IA2TextSegment() {
	}
	
	public IA2TextSegment(String text, int start, int end) {
		this.text = text;
		this.start = start;
		this.end = end;
	}

	public int getEnd() {
		return end;
	}

	public int getStart() {
		return start;
	}

	public String getText() {
		return text;
	}
	
	public String toString(){
		return getClass().getSimpleName() + "[text=" +text + ",start=" + start + ",end=" + end + "]";
	}

}
