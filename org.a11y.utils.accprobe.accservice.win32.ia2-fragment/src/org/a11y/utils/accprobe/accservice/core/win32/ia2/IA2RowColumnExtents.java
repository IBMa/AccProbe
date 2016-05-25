/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 
package org.a11y.utils.accprobe.accservice.core.win32.ia2;

public class IA2RowColumnExtents {

	public int row;  
	public int column;  
	public int rowExtents;  
	public int columnExtents;  
	public boolean isSelected; 

	public IA2RowColumnExtents(int row, int column, int rowExtents, int columnExtents, boolean isSelected) {
		this.row = row;
		this.column = column;
		this.rowExtents = rowExtents;
		this.columnExtents = columnExtents;
		this.isSelected = isSelected;
	}
	
	public String toString(){
		return getClass().getSimpleName() + "[row=" + row + ",col="+ 
					column + ",rowExtents=" +rowExtents
				+",colExtents=" + columnExtents
				+",isSel=" + isSelected;
	}
}
