/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core;

import org.a11y.utils.accprobe.core.model.InvalidComponentException;

/**
 * Interface for exposing accessibility-related properties of tables to the validation engine.
 *
 * @see IAccessibleElement2
 * @author Mike Smith
 */
public interface IAccessibleTableElement
{

	/**
	 * get the caption for the table
	 * 
	 * @return caption or an empty string if no caption is provided
	 * @throws InvalidComponentException
	 */
	public Object getCaption () throws InvalidComponentException;

	/**
	 * get the summary for the table
	 * 
	 * @return summary or an empty string if no summary is provided
	 * @throws InvalidComponentException
	 */
	public Object getSummary () throws InvalidComponentException;

	/**
	 * get the number of columns for the table
	 * 
	 * @return column count
	 * @throws InvalidComponentException
	 */
	public long getColumnCount () throws InvalidComponentException;

	/**
	 * get the number of rows for the table
	 * 
	 * @return row count
	 * @throws InvalidComponentException
	 */
	public long getRowCount () throws InvalidComponentException;

	/**
	 * get the description for the specified column
	 * 
	 * @param column zero-based index of column
	 * @return row description or an empty string if column index is out of range
	 * @throws InvalidComponentException
	 */
	public String getColumnDescription (long column) throws InvalidComponentException;

	/**
	 * get the description for the specified row
	 *
	 * @param row zero-based index of row
	 * @return row description or an empty string if row index is out of range
	 * @throws InvalidComponentException
	 */
	public String getRowDescription (long row) throws InvalidComponentException;

	/**
	 * get the indecies of the selected columns
	 * 
	 * @return an array of selected column indecies
	 * @throws InvalidComponentException
	 */
	public int[] getSelectedColumns () throws InvalidComponentException;

	/**
	 * get the indecies of the selected rows
	 * 
	 * @return an array of selected row indecies
	 * @throws InvalidComponentException
	 */
	public int[] getSelectedRows () throws InvalidComponentException;
	
	/**
	 * get the number of selected chidren for the table
	 * 
	 * @return selected children count
	 * @throws InvalidComponentException
	 */
	public int getSelectedChildCount() throws InvalidComponentException;
	
	/**
	 * get the indices of the selected Children
	 * 
	 * @return an array of selected children indecies
	 * @throws InvalidComponentException
	 */
	public int[] getSelectedChildren () throws InvalidComponentException;
	
}
