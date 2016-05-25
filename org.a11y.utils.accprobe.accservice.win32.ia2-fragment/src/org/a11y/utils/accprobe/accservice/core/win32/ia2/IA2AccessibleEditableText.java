/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core.win32.ia2;

import org.a11y.utils.accprobe.accservice.core.IAccessibleEditableTextElement;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;


/**
 * implementation of <code>IAccessibleEditableTextElement</code> for GUI controls that implement IAccessible2/IBM interfaces.
 * This interface gives write access to a text representation.
 * This interface is typically used in conjunction with the
 * AccessibleText interface and complements that interface 
 * with the additional ability to modify text.
*
 * The substrings used with this interface are specified as 
 * follows: If startOffset is less than endOffset, the 
 * substring starts with the character at startOffset and 
 * ends with the character just before endOffset. If endOffset 
 * is lower than startOffset, the result is the same as a call 
 * with the two arguments exchanged. The whole text can be 
 * defined by passing the indices zero and 
 * IAccessibleText::nCharacters. If both indices have the 
 * same value, an empty string is defined.
*
 * @author Kavitha Teegala
 */
public class IA2AccessibleEditableText extends IA2AccessibleElement implements IAccessibleEditableTextElement 
{

	private int _accRef;
	private IA2Accessible _parent = null;

	/**
	 * Constructor used to create an accessible text object
	 * @param accRef reference pointer to the IA2Accessible text object
	 * @param parent IA2Accessible parent of this object
	 */
	public IA2AccessibleEditableText (int accRef, IA2Accessible parent) {
		_accRef = accRef;
		_parent = parent;
	}

	/**
	 * used by native code only. Clients should not call directly.
	 * @return ptr address for native object
	 */
	public int internalRef () {
		return _accRef;
	}

	private void checkIsValid () throws InvalidComponentException {
		if (_accRef == 0 ) { throw new InvalidComponentException("Invalid accessible image"); }
	}

	public void copyText(int startIndex, int endIndex) throws InvalidComponentException {
		checkIsValid();
		boolean res = internalCopyText(startIndex, endIndex);
	}
	protected native boolean internalCopyText(int startIndex, int endIndex);
	
	public void cutText(int startIndex, int endIndex) throws InvalidComponentException {
		checkIsValid();
		boolean cres = internalCutText(startIndex, endIndex);
	}
	protected native boolean internalCutText(int startIndex, int endIndex);
	
	public void deleteText(int startIndex, int endIndex) throws InvalidComponentException {
		checkIsValid();
		boolean cres = internalDeleteText(startIndex, endIndex);
		
	}
	protected native boolean internalDeleteText(int startIndex, int endIndex);
	
	public void insertText(int index, String s) throws InvalidComponentException {
		checkIsValid();
		if( s!=null){
			boolean res = internalInsertText(index, s);
		}
	}
	protected native boolean internalInsertText(int index, String s);
	
	public void pasteText(int startIndex) throws InvalidComponentException {
		checkIsValid();
		boolean res = internalPasteText(startIndex);
		
	}
	protected native boolean internalPasteText(int startIndex);
	
	public void replaceText(int startIndex, int endIndex, String s) throws InvalidComponentException {
		checkIsValid();
		if(s!=null){
			boolean res = internalReplaceText(startIndex, endIndex, s);
		}
	}
	protected native boolean internalReplaceText(int startIndex, int endIndex, String s);
		
	public void setAttributes(int startIndex, int endIndex, String as) throws InvalidComponentException {
		checkIsValid();
		boolean res = internalSetAttributes(startIndex, endIndex, as);
		
		
	}
	protected native boolean internalSetAttributes(int startIndex, int endIndex, String as);

}