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
 * @author Kavitha Teegala
 */
public interface IAccessibleEditableTextElement 
{	
     /**
     * Copies the text range into the clipboard. 
     * 
     * @param startIndex the starting index in the text
     * @param endIndex the ending index in the text
     * @throws InvalidComponentException 
     */
    public void copyText(int startIndex, int endIndex) throws InvalidComponentException;

    /**
     * Deletes the text between two indices
     *
     * @param startIndex the starting index in the text
     * @param endIndex the ending index in the text
     * @throws InvalidComponentException 
     */
    public void deleteText(int startIndex, int endIndex) throws InvalidComponentException;

    /**
     * Inserts the specified string at the given index/
     *
     * @param index the index in the text where the string will 
     * be inserted
     * @param s the string to insert in the text
     * @throws InvalidComponentException 
     */
    public void insertText(int index, String s) throws InvalidComponentException;

    /**
     * Deletes a range of text and copies it to the clipboard. 
     *
     * @param startIndex the starting index in the text
     * @param endIndex the ending index in the text
     * @throws InvalidComponentException 
     */
    public void cutText(int startIndex, int endIndex) throws InvalidComponentException;

    /**
     * Pastes the text from the system clipboard into the text
     * starting at the specified index.
     *
     * @param startIndex the starting index in the text
     * @throws InvalidComponentException 
     */
    public void pasteText(int startIndex) throws InvalidComponentException;

    /**
     * Replaces the text between two indices with the specified
     * string.
     *
     * @param startIndex the starting index in the text
     * @param endIndex the ending index in the text
     * @param s the string to replace the text between two indices
     * @throws InvalidComponentException 
     */
    public void replaceText(int startIndex, int endIndex, String s) throws InvalidComponentException;

    /**
     * Sets attributes for the text between two indices.
     *
     * @param startIndex the starting index in the text
     * @param endIndex the ending index in the text
     * @param as -atrributes in a String array
     * @throws InvalidComponentException 
     */
    public void setAttributes(int startIndex, int endIndex, String as) throws InvalidComponentException;

}
