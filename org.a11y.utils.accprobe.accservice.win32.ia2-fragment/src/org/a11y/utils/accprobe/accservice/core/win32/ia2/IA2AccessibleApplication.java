/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core.win32.ia2;

import org.a11y.utils.accprobe.core.model.InvalidComponentException;


/**
 * implementation of <code>IA2AccessibleApplication</code>
*
 /**
 * This interface gives access to the application's name 
 * and version information.This interface provides the AT
 * with the information it needs to differentiate this 
 * application from other applications, from other versions 
 * of this application, or from other versions of this application
 * running on different versions of an accessibility bridge or
 * accessbility toolkit. 

 * @see IA2Accessible
*
 * @author Kavitha Teegala
 */
public class IA2AccessibleApplication extends IA2AccessibleElement
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
	public IA2AccessibleApplication(int val, IA2Accessible parent) {
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
	 * Return the application name
	 * @return accessible application name
	 * @throws InvalidComponentException
	 */
	public String getApplicationName ()
		throws InvalidComponentException {
		checkIsValid();
		return internalGetApplicationName();
	}

	protected native String internalGetApplicationName ();

	/**
	 * Return the accessible application version
	 * @return accessible application version
	 * @throws InvalidComponentException
	 */
	public String getApplicationVersion ()
		throws InvalidComponentException {
		checkIsValid();
		return internalGetApplicationVersion();
	}

	protected native String internalGetApplicationVersion ();
	
	/**
	 * Return the toolkit name
	 * @return accessible toolkit name
	 * @throws InvalidComponentException
	 */
	public String getToolkitName ()
		throws InvalidComponentException {
		checkIsValid();
		return internalGetToolkitName();
	}

	protected native String internalGetToolkitName ();

	/**
	 * Return the accessible toolkit version
	 * @return accessible toolkit version
	 * @throws InvalidComponentException
	 */
	public String getToolkitVersion ()
		throws InvalidComponentException {
		checkIsValid();
		return internalGetToolkitVersion();
	}

	protected native String internalGetToolkitVersion ();
}
