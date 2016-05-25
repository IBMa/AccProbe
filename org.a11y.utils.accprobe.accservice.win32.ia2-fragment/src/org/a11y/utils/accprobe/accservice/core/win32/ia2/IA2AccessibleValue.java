/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core.win32.ia2;

import org.a11y.utils.accprobe.accservice.core.IAccessibleValueElement;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;


/**
 * implementation of <code>IA2AccessibleValue</code>
*
 /**
 * The IA2AccessibleValue interface should be supported by any object 
 * that supports a numerical value (e.g., a scroll bar).  This interface 
 * provides the standard mechanism for an assistive technology to determine 
 * and set the numerical value as well as get the minimum and maximum values.
 *
 * @see IA2Accessible
 * @see IA2Accessible#getAccessibleValue
*
 * @author Kavitha Teegala
 */
public class IA2AccessibleValue extends IA2AccessibleElement implements IAccessibleValueElement
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
	public IA2AccessibleValue(int val, IA2Accessible parent) {
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

	 /**
     * Get the value of this object as a Number.  If the value has not been
     * set, the return value will be null.
     *
     * @return value of the object
	 * @throws InvalidComponentException 
     */
    public Object getCurrentValue() throws InvalidComponentException{
    	checkIsValid();
    	return internalGetCurrentValue();
    }

	protected native Object internalGetCurrentValue();

    /**
     * Set the value of this object as a Number.
     *
     * @throws InvalidComponentException 
     */
    public void setCurrentValue(String vt_type, long value) throws InvalidComponentException{
    	checkIsValid();
   		internalSetCurrentAccessibleValue(value, vt_type);
      }

	protected native boolean internalSetCurrentAccessibleValue(long o, String type);

//    /**
//     * Get the description of the value of this object.
//     *
//     * @return description of the value of the object
//     */
//    public String getValueDescription();

	/**
	 * get the minimum accessible value (e.g. for a slider control)
	 * 
	 * @return minimum value or <code>null</code> if no value is provided
	 * @throws InvalidComponentException 
	 * @throws InvalidComponentException
	 */
	public Object getMinimumValue () throws InvalidComponentException{
		checkIsValid();
		return internalGetValueMin();
	}

	protected native Object internalGetValueMax ();

	/**
	 * get the maximum accessible value (e.g. for a slider control)
	 * 
	 * @return maximum value or <code>null</code> if no value is provided
	 * @throws InvalidComponentException 
	 * @throws InvalidComponentException
	 */
	public Object getMaximumValue () throws InvalidComponentException{
		checkIsValid();
		return internalGetValueMax();
	}
	
	protected native Object internalGetValueMin();

}
