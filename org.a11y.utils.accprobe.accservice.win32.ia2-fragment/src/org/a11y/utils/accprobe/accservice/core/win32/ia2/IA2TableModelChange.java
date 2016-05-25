/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 
package org.a11y.utils.accprobe.accservice.core.win32.ia2;

public class IA2TableModelChange {
	public int type;
	public int firstRow;
	public int lastRow;
	public int firstColumn;
	public int lastColumn;

	public IA2TableModelChange(int type, int firstRow, int lastRow, int firstColumn, int lastColumn) {
		this.type = type;
		this.firstRow = firstRow;
		this.lastRow = lastRow;
		this.firstColumn = firstColumn;
		this.lastColumn = lastColumn;
	}
	
	public String toString(){
		return getClass().getName() + "[type=" + type +",frow=" +
		firstRow + ",fcol="+ 
		firstColumn + ",lrow=" +lastRow
		+",lcol=" + lastColumn;

	}
	
}
