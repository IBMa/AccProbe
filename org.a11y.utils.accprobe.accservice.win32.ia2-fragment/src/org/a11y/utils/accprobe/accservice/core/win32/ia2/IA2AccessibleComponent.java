/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core.win32.ia2;

import java.awt.Point;

import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaAccessible;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;


/**
 *
 * @see IA2Accessible
 * @see IA2Accessible#getAccessibleComponent
*
 * @author Kavitha Teegala
 */
public class IA2AccessibleComponent extends IA2AccessibleElement
{

	// This reference is used to ensure that the IA2Accessible parent
	// does not go out of scope while this class exists.  When IA2Accessible
	// goes out of scope the accRef will be disposed
	private IA2Accessible _parent = null;

	private int _accRef;

	/**
	 * Constructor used to create an accessible image object
	 * @param val
	 * @param parent IA2Accessible parent of this object
	 */
	public IA2AccessibleComponent(int val, IA2Accessible parent) {
		_accRef = val;
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
		if (_accRef == 0 || _parent == null) { throw new InvalidComponentException("Invalid accessible image"); }
	}

    public Point getLocationInParent() {
    	Point P = null;
    	int[] pArray = internalGetLocation();
    	if(pArray!=null && pArray.length>0){
    		P = new Point(pArray[0], pArray[1]);
    	}
        return P;
    }
	protected native int[] internalGetLocation();
	
    public Object getForeground() {
    	int res =  internalGetForeground();
    	return MsaaAccessible.getHex(res);
    }
	protected native int internalGetForeground();
	
    public Object getBackground() {
    	int res = internalGetBackground();
    	return MsaaAccessible.getHex(res);
    }
	protected native int internalGetBackground();
}
