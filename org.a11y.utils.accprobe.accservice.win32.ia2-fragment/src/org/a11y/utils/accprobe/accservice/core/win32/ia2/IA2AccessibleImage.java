/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core.win32.ia2;

import java.awt.Point;

import org.a11y.utils.accprobe.accservice.core.IAccessibleImageElement;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;


/**
 * implementation of <code>IAccessibleImageElement</code> for GUI controls that implement IAccessible2/IBM interfaces.
*
 * <p>This class is a wrapper for an IAccessible2 pointer, a pointer that Provides
 * access to a native Windows object that provides assistive technologies (ATs) with properties of GUI components 
 * that allow the AT to offer an alternative interface to the control. This class relies upon JCAccessible.dll
 * for most of its implementation. The documentation for the Microsoft COM
 * library and, in particular, for IAccessible2/IBM will be helpful.
*
 * @author Mike Smith
 */
public class IA2AccessibleImage extends IA2AccessibleElement implements IAccessibleImageElement 
{

	// This reference is used to ensure that the IA2Accessible parent
	// does not go out of scope while this class exists.  When IA2Accessible
	// goes out of scope the accRef will be disposed
	private IA2Accessible _parent = null;

	private int _accRef;

	/**
	 * Constructor used to create an accessible image object
	 * @param image reference pointer to the IA2Accessible image object
	 * @param parent IA2Accessible parent of this object
	 */
	public IA2AccessibleImage (int image, IA2Accessible parent) {
		_accRef = image;
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

	/** {@inheritDoc} */
	public String getDescription () throws InvalidComponentException {
		checkIsValid();
		return internalGetImageDescription();
	}

	protected native String internalGetImageDescription ();

	/** {@inheritDoc} */
	public int getHeight () throws InvalidComponentException {
		checkIsValid();
		return internalGetImageHeight();
	}

	protected native int internalGetImageHeight ();

	/** {@inheritDoc} */
	public int getWidth () throws InvalidComponentException {
		checkIsValid();
		return internalGetImageWidth();
	}

	protected native int internalGetImageWidth ();
	
	public Point getImagePosition() throws InvalidComponentException {
		checkIsValid();
		return internalGetImagePosition(0);
	}
	
	protected native Point internalGetImagePosition(int type);

}
