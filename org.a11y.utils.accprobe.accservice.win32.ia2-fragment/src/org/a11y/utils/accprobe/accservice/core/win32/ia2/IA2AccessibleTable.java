/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core.win32.ia2;

import org.a11y.utils.accprobe.accservice.core.IAccessibleTableElement;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;


/**
 * implementation of <code>IAccessibleTableElement</code> for GUI controls that implement IAccessible2/IBM interfaces.
*
 * <p>This class is a wrapper for an IAccessible2 pointer, a pointer that Provides
 * access to a native Windows object that provides assistive technologies (ATs) with properties of GUI components 
 * that allow the AT to offer an alternative interface to the control. This class relies upon JCAccessible.dll
 * for most of its implementation. The documentation for the Microsoft COM
 * library and, in particular, for IAccessible2/IBM will be helpful.
*
 * @author Mike Smith
 */
public class IA2AccessibleTable extends IA2AccessibleElement implements IAccessibleTableElement
{

	// This reference is used to ensure that the IA2Accessible parent
	// does not go out of scope while this class exists.  When IA2Accessible
	// goes out of scope the accRef will be disposed
	private IA2Accessible _parent = null;

	private int _accRef;

	/**
	 * Constructor used to create an accessible table object
	 * @param ref reference pointer to the IA2Accessible table object
	 * @param parent IA2Accessible parent of this object
	 */
	public IA2AccessibleTable (int ref, IA2Accessible parent) {
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

	/** {@inheritDoc} */
	public Object getCaption () throws InvalidComponentException {
		checkIsValid();
		int summ = internalGetTableCaption();
		String summr = null;
		IA2Accessible ia2 = null;
		if(summ!=0){
			ia2 = new IA2Accessible(summ);
		}
		return ia2;
	}

	protected native int internalGetTableCaption ();

	/** {@inheritDoc} */
	public Object getSummary () throws InvalidComponentException {
		checkIsValid();
		int summ = internalGetTableSummary();
		String summr = null;
		IA2Accessible ia2 = null;
		if(summ!=0){
			ia2 = new IA2Accessible(summ);
		}
		return ia2;
	}

	protected native int internalGetTableSummary ();

	/** {@inheritDoc} */
	public long getColumnCount () throws InvalidComponentException {
		checkIsValid();
		return internalGetTableColumnCount();
	}

	protected native long internalGetTableColumnCount ();

	/** {@inheritDoc} */
	public long getRowCount () throws InvalidComponentException {
		checkIsValid();
		return internalGetTableRowCount();
	}

	protected native long internalGetTableRowCount ();

	/** {@inheritDoc} */
	public String getColumnDescription (long column)
		throws InvalidComponentException {
		checkIsValid();
		return internalGetColumnDescription(column);
	}

	protected native String internalGetColumnDescription (long column);

	/** {@inheritDoc} */
	public String getRowDescription (long row)
		throws InvalidComponentException {
		checkIsValid();
		return internalGetRowDescription(row);
	}

	protected native String internalGetRowDescription (long row);

	/** {@inheritDoc} */
	public int[] getSelectedColumns ()
		throws InvalidComponentException {
		checkIsValid();
		int[] res=  internalGetSelectedAccessibleColumns();
		if(res!=null && res.length >0){
			return res;
		}
		return null;
	}

	protected native int[] internalGetSelectedAccessibleColumns ();

	/** {@inheritDoc} */
	public int[] getSelectedRows () throws InvalidComponentException {
		checkIsValid();
		int[] res= internalGetSelectedAccessibleRows();
		if(res!=null && res.length >0){
			return res;
		}
		return null;
	}

	protected native int[] internalGetSelectedAccessibleRows ();

	public IA2Accessible getAccessibleAt(int row, int column) throws InvalidComponentException {
		checkIsValid();
		int ref = internalGetCell(row, column);
		IA2Accessible iacc2 = null;
		iacc2 = new IA2Accessible(ref);
		return iacc2;
	}
	protected native int internalGetCell(int row, int column);

	public int getChildIndex(int rowIndex, int colIndex) throws InvalidComponentException {
		checkIsValid();
		return  internalGetIndex(rowIndex, colIndex);
	}
	protected native int internalGetIndex(int rowIndex, int colIndex);

	public int getColumnExtentAt(int row, int column) throws InvalidComponentException {
		checkIsValid();
		return internalGetColumnExtentAt( row,  column);
	}
	protected native int internalGetColumnExtentAt(int row, int column);

	public IA2AccessibleTable getColumnHeaders()
	throws InvalidComponentException {
		checkIsValid();
		int ref = internalGetColumnHeaders();
		if(ref != 0){
			return new IA2AccessibleTable(ref, _parent);
		}
		return null;
	}
	protected native int internalGetColumnHeaders();
	public int getColumnIndex(int childIndex) throws InvalidComponentException {
		checkIsValid();
		return internalGetColumnIndex( childIndex);
	}

	protected native int internalGetColumnIndex(int childIndex);

	public int getSelectedAccessibleColumnCount() throws InvalidComponentException {
		checkIsValid();
		return internalGetSelectedColumnCount();
	}
	protected native int internalGetSelectedColumnCount();

	public int getSelectedAccessibleRowCount() throws InvalidComponentException {
		checkIsValid();
		return internalGetSelectedRowCount();
	}
	protected native int internalGetSelectedRowCount();

	public int getRowExtentAt(int row, int column) throws InvalidComponentException {
		checkIsValid();
		return internalGetRowExtentAt( row,  column);
	}
	protected native int internalGetRowExtentAt(int row, int column);

	public IA2AccessibleTable getRowHeaders()
	throws InvalidComponentException {
		checkIsValid();
		int ref = internalGetRowHeaders();
		if(ref != 0){
			return new IA2AccessibleTable(ref, _parent);
		}
		return null;
	}
	protected native int internalGetRowHeaders();

	public int getRowIndex(int childIndex) throws InvalidComponentException {
		checkIsValid();
		return internalGetRowIndex( childIndex);
	}

	protected native int internalGetRowIndex(int childIndex);

	public boolean isColumnSelected(int column) throws InvalidComponentException {
		checkIsValid();
		return internalIsColumnSelected(column);
	}
	protected native boolean  internalIsColumnSelected(int column);

	public boolean isRowSelected(int row) throws InvalidComponentException {
		checkIsValid();
		return internalIsRowSelected(row);
	}
	protected native boolean  internalIsRowSelected(int row);

	public boolean isAccessibleSelected(int row, int column) throws InvalidComponentException {
		checkIsValid();
		return internalIsAccessibleSelected(row, column);
	}
	protected native boolean  internalIsAccessibleSelected(int row,int column);

	public boolean selectColumn(int column)throws InvalidComponentException {
		checkIsValid();
		return internalSelectAccessibleColumn(column);
	}
	protected native boolean  internalSelectAccessibleColumn(int column);

	public boolean selectRow(int row) throws InvalidComponentException {
		checkIsValid();
		return internalSelectAccessibleRow(row);
	}
	protected native boolean  internalSelectAccessibleRow(int row);

	public boolean unselectColumn(int column) throws InvalidComponentException {
		checkIsValid();
		return internalUnselectAccessibleColumn(column);
	}
	protected native boolean  internalUnselectAccessibleColumn(int column);

	public boolean unselectRow(int row) throws InvalidComponentException {
		checkIsValid();
		return internalUnselectAccessibleRow(row);
	}
	protected native boolean  internalUnselectAccessibleRow(int row);

	public IA2RowColumnExtents getRowColumnExtentsAtIndex(int index)throws InvalidComponentException {
		checkIsValid();
		int[] rcExts = internalGetRowColumnExtentsAtIndex(index);
		if(rcExts!=null && rcExts.length==5){
			boolean isSel = (rcExts[4]==1)? true: false;
			return new IA2RowColumnExtents(rcExts[0],rcExts[1],rcExts[2],rcExts[3], isSel);
		}
		return null;
	}
	protected native int[] internalGetRowColumnExtentsAtIndex(int index);
	
	public IA2TableModelChange getTableModelChange() throws InvalidComponentException {
		checkIsValid();
		int[] modChg = internalGetModelChange();
		if(modChg!=null && modChg.length==5){
			return new IA2TableModelChange(modChg[0], modChg[1],modChg[2],modChg[3],modChg[4]);
		}
		return null;
	}
	protected native int[] internalGetModelChange();

	public int getSelectedChildCount() throws InvalidComponentException {
		return internalGetSelectedChildCount();
	}
	protected native int internalGetSelectedChildCount();
	
	public int[] getSelectedChildren() throws InvalidComponentException {
		checkIsValid();
		int[] res=  internalGetSelectedChildren();
		if(res!=null && res.length >0){
			return res;
		}
		return null;
	}
	protected native int[] internalGetSelectedChildren();
}
