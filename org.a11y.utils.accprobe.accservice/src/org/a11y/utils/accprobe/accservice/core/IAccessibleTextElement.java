/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core;

import org.a11y.utils.accprobe.core.model.InvalidComponentException;

/**
 * Interface for exposing accessibility-related properties of text components to the validation engine.
 *
 * @see IAccessibleElement2
 * @author Mike Smith
 */
public interface IAccessibleTextElement
{

	/**
	 * get the offset of the caret for the accessible text object
	 * 
	 * @return caret offset
	 * @throws InvalidComponentException
	 */
	public long getCaretOffset () throws InvalidComponentException;

	/**
	 * get the attributes at the specified offset of the text
	 * 
	 * @param offset zero index of caret offset
	 * @return attributes or an empty string if no description is provided
	 * @throws InvalidComponentException
	 */
	public Object getAttributes (long offset) throws InvalidComponentException;

	/**
	 * get number of active non-contiguous selections 
	 * 
	 * @return the number of selections
	 * @throws InvalidComponentException
	 */
	public long getnSelections () throws InvalidComponentException;

	/**
	 * get the text of the selected object
	 * 
	 * @param index zero index of selected text objects
	 * @return the number of selections or empty string if index is out of range
	 * @throws InvalidComponentException
	 */
	public Object getSelection (long index) throws InvalidComponentException;
}
