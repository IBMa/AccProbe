/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core;

import org.a11y.utils.accprobe.core.model.InvalidComponentException;

/**
 * Interface for exposing accessibility-related properties of images to the validation engine.
 *
 * @see IAccessibleElement2
 * @author Mike Smith
 */
public interface IAccessibleImageElement
{

	/**
	 * get the description for the accessible image
	 * 
	 * @return image description or an empty string if no description is provided
	 * @throws InvalidComponentException
	 */
	public String getDescription () throws InvalidComponentException;

	/**
	 * get the height for the accessible image
	 * 
	 * @return image height or zero if no height is provided
	 * @throws InvalidComponentException
	 */
	public int getHeight () throws InvalidComponentException;

	/**
	 * get the width for the accessible image
	 * 
	 * @return image width or zerog if no width is provided
	 * @throws InvalidComponentException
	 */
	public int getWidth () throws InvalidComponentException;
}
