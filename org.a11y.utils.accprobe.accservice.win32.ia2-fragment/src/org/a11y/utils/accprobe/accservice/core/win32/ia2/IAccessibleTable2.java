/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core.win32.ia2;

import org.a11y.utils.accprobe.core.model.InvalidComponentException;

public class IAccessibleTable2 extends IA2AccessibleElement{

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
	public IAccessibleTable2 (int ref, IA2Accessible parent) {
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
		int summ = internalGetCaption();
		String summr = null;
		IA2Accessible ia2 = null;
		if(summ!=0){
			ia2 = new IA2Accessible(summ);
		}
		return ia2;
	}

	protected native int internalGetCaption ();

	/** {@inheritDoc} */
	public Object getSummary () throws InvalidComponentException {
		checkIsValid();
		int summ = internalGetSummary();
		String summr = null;
		IA2Accessible ia2 = null;
		if(summ!=0){
			ia2 = new IA2Accessible(summ);
		}
		return ia2;
	}

	protected native int internalGetSummary ();

	/** {@inheritDoc} */
	public long getnColumns () throws InvalidComponentException {
		
		return getColumnCount();
	}

	protected native long internalGetNColumns ();

	/** {@inheritDoc} */
	public long getnRows () throws InvalidComponentException {
		return getRowCount();
	}

	protected native long internalGetNRows();


	private long getColumnCount() throws InvalidComponentException {
		checkIsValid();
		return internalGetNColumns();
	}

	private long getRowCount() throws InvalidComponentException {
		checkIsValid();
		return internalGetNRows();
	}

	private int getSelectedChildCount() throws InvalidComponentException {
		return internalGetNSelectedCells();
	}

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
	public Object[] getSelectedColumns ()
		throws InvalidComponentException {
		checkIsValid();
		int[] res=  internalGetSelectedColumns();
		if(res!=null && res.length >0){
			Integer[] cols = new Integer[res.length];
			for(int i=0;i<res.length;i++){
				cols[i] = res[i];
			}
			return cols;
		}
		return null;
	}

	protected native int[] internalGetSelectedColumns ();

	/** {@inheritDoc} */
	public Object[] getSelectedRows () throws InvalidComponentException {
		checkIsValid();
		int[] res= internalGetSelectedRows();
		if(res!=null && res.length >0){
			Integer[] rows = new Integer[res.length];
			for(int i=0;i<res.length;i++){
				rows[i] = res[i];
			}
			return rows;
		}
		return null;
	}

	protected native int[] internalGetSelectedRows ();

	public IA2Accessible getAccessibleAt(int row, int column) throws InvalidComponentException {
		checkIsValid();
		int ref = internalGetCellAt(row, column);
		IA2Accessible iacc2 = null;
		if(ref != 0){
			iacc2 = new IA2Accessible(ref);
		}
		return iacc2;
	}
	protected native int internalGetCellAt(int row, int column);

	public int getnSelectedColumns() throws InvalidComponentException {
		checkIsValid();
		return internalGetNSelectedColumns();
	}
	protected native int internalGetNSelectedColumns();

	public int getnSelectedRows() throws InvalidComponentException {
		checkIsValid();
		return internalGetNSelectedRows();
	}
	protected native int internalGetNSelectedRows();

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

	public boolean selectColumn(int column)throws InvalidComponentException {
		checkIsValid();
		return internalSelectColumn(column);
	}
	protected native boolean  internalSelectColumn(int column);

	public boolean selectRow(int row) throws InvalidComponentException {
		checkIsValid();
		return internalSelectRow(row);
	}
	protected native boolean  internalSelectRow(int row);

	public boolean unselectColumn(int column) throws InvalidComponentException {
		checkIsValid();
		return internalUnselectColumn(column);
	}
	protected native boolean  internalUnselectColumn(int column);

	public boolean unselectRow(int row) throws InvalidComponentException {
		checkIsValid();
		return internalUnselectRow(row);
	}
	protected native boolean  internalUnselectRow(int row);

	public IA2TableModelChange getModelChange() throws InvalidComponentException {
		checkIsValid();
		int[] modChg = internalGetModelChange();
		if(modChg!=null && modChg.length==5){
			return new IA2TableModelChange(modChg[0], modChg[1],modChg[2],modChg[3],modChg[4]);
		}
		return null;
	}
	protected native int[] internalGetModelChange();

	public int getnSelectedCells() throws InvalidComponentException {
		return getSelectedChildCount();
	}
	protected native int internalGetNSelectedCells();
	
	public IA2Accessible[] getSelectedCells() throws InvalidComponentException {
		checkIsValid();
		int[] refs=  internalGetSelectedCells();
		if(refs!=null && refs.length >0){
			IA2Accessible[] res = new IA2Accessible[refs.length];
			for(int i=0; i< refs.length; i++){
				res[i] = new IA2Accessible(refs[i]);
			}
			return res;
		}
		return null;
	}
	protected native int[] internalGetSelectedCells();

}
