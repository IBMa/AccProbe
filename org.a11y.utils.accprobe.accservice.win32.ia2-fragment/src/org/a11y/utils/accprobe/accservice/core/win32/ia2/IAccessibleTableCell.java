/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core.win32.ia2;

import org.a11y.utils.accprobe.core.model.InvalidComponentException;

public class IAccessibleTableCell extends IA2AccessibleElement {
	// This reference is used to ensure that the IA2Accessible parent
	// does not go out of scope while this class exists.  When IA2Accessible
	// goes out of scope the accRef will be disposed
	private IA2Accessible _parent = null;

	private int _accRef;

	/**
	 * Constructor used to create an accessible table cell object
	 * @param ref reference pointer to the table cell object
	 * @param parent IA2Accessible parent of this object
	 */
	public IAccessibleTableCell (int ref, IA2Accessible parent) {
		_accRef = ref;
		_parent = parent;
	}

	/**
	 * used by native code only. Clients should not call directly.
	 * @return ptr address for native object
	 */
	public int internalRef () {
		return _accRef;
	}

	/**
	 * used before each public method call to confirm that
	 * this object is valid and can return correct information
	 * @throws InvalidComponentException
	 */
	private void checkIsValid () throws InvalidComponentException {
		if (_accRef == 0 || _parent == null) { throw new InvalidComponentException("Invalid accessible table"); }
	}

	/**
	 * Returns the number of columns occupied by this cell accessible. 
	 **/
	public int getColumnExtent() throws InvalidComponentException {
		checkIsValid();
		return internalGetColumnExtent();
	}
	protected native int internalGetColumnExtent();

	/**
	 * Returns the column headers as an array of cell accessibles. 
	 * @return
	 * @throws InvalidComponentException
	 */
	public IA2Accessible[] getColumnHeaderCells()
	throws InvalidComponentException {
		checkIsValid();
		int[] ref = internalGetColumnHeaderCells();
		if(ref != null && ref.length>0){
			IA2Accessible[] res = new IA2Accessible[ref.length];
			for(int i=0;i<ref.length; i++){
				res[i] = new IA2Accessible(ref[i]);
			}
			return res;
		}
		return null;
	}
	protected native int[] internalGetColumnHeaderCells();

	/**
	 * Translates this cell accessible into the corresponding column index. 
	 * @return
	 * @throws InvalidComponentException
	 */
	public int getColumnIndex() throws InvalidComponentException {
		checkIsValid();
		return internalGetColumnIndex();
	}

	protected native int internalGetColumnIndex();

	/**
	 * Returns the number of rows occupied by this cell accessible. 
	 * @param row
	 * @param column
	 * @return
	 * @throws InvalidComponentException
	 */
	public int getRowExtent() throws InvalidComponentException {
		checkIsValid();
		return internalGetRowExtent();
	}
	protected native int internalGetRowExtent();

	/**
	 * 
	 * @return
	 * @throws InvalidComponentException
	 */

	public IA2Accessible[] getRowHeaderCells()throws InvalidComponentException {
		checkIsValid();
		int[] ref = internalGetRowHeaderCells();
		if(ref != null && ref.length>0){
			IA2Accessible[] res = new IA2Accessible[ref.length];
			for(int i=0;i<ref.length; i++){
				res[i] = new IA2Accessible(ref[i]);
			}
			return res;
		}
		return null;
	}
	protected native int[] internalGetRowHeaderCells();

	/**
	 * 
	 * @param childIndex
	 * @return
	 * @throws InvalidComponentException
	 */


	public int getRowIndex() throws InvalidComponentException {
		checkIsValid();
		return internalGetRowIndex();
	}

	protected native int internalGetRowIndex();

	/**
	 * 
	 * @return
	 * @throws InvalidComponentException
	 */
	public boolean isSelected() throws InvalidComponentException {
		checkIsValid();
		return internalIsSelected();
	}
	protected native boolean internalIsSelected();

	/**
	 * 
	 * @return
	 * @throws InvalidComponentException
	 */
	public String getRowColumnExtents()throws InvalidComponentException {
		checkIsValid();
		int[] rcExts = internalGetRowColumnExtents();
		if(rcExts!=null && rcExts.length==5){
			boolean isSel = (rcExts[4]==1)? true: false;
			IA2RowColumnExtents rce = new IA2RowColumnExtents(rcExts[0],rcExts[1],rcExts[2],rcExts[3], isSel);
			return rce.toString();
		}
		return null;
	}
	protected native int[] internalGetRowColumnExtents();

	/**
	 * 
	 * @return
	 * @throws InvalidComponentException
	 */
	public IAccessibleTable2 getTable() throws InvalidComponentException {
		checkIsValid();
		IAccessibleTable2 res = null;
		int ref = internalGetTable();
		if (ref != 0) {
			res = new IAccessibleTable2(ref, _parent);
		}
		return res;
	}
	protected native int internalGetTable();

}
