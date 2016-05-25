/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core.win32.ia2;

import java.awt.Point;
import java.awt.Rectangle;

import org.a11y.utils.accprobe.accservice.core.IAccessibleTextElement;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;


/**
 * implementation of <code>IAccessibleTextElement</code> for GUI controls that implement IAccessible2/IBM interfaces.
*
 * <p>This class is a wrapper for an IAccessible2 pointer, a pointer that Provides
 * access to a native Windows object that provides assistive technologies (ATs) with properties of GUI components 
 * that allow the AT to offer an alternative interface to the control. This class relies upon JCAccessible.dll
 * for most of its implementation. The documentation for the Microsoft COM
 * library and, in particular, for IAccessible2/IBM will be helpful.
*
 * @author Mike Smith, Kavitha Teegala
 */
public class IA2AccessibleText extends IA2AccessibleElement implements IAccessibleTextElement
{

	// This reference is used to ensure that the IA2Accessible parent
	// does not go out of scope while this class exists.  When IA2Accessible
	// goes out of scope the accRef will be disposed
	private IA2Accessible _parent = null;

	private int _accRef;

	/**
	 * Constructor used to create an accessible text object
	 * @param accRef reference pointer to the IA2Accessible text object
	 * @param parent IA2Accessible parent of this object
	 */
	public IA2AccessibleText (int accRef, IA2Accessible parent) {
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

	/**
	 * used before each public method call to confirm that
	 * this object is valid and can return correct information
	 * @throws InvalidComponentException
	 */
	private void checkIsValid () throws InvalidComponentException {
		if (_accRef == 0 || _parent == null) { throw new InvalidComponentException("Invalid accessible text"); }
	}

	/** {@inheritDoc} */
	public String getText (int startOffset, int endOffset) throws InvalidComponentException {
		checkIsValid();
		return internalGetText(startOffset, endOffset);
	}

	protected native String internalGetText (int startOffset, int endOffset);

	/** {@inheritDoc} */
	public long getCaretOffset () throws InvalidComponentException {
		checkIsValid();
		return internalGetCaretOffset();
	}

	protected native long internalGetCaretOffset ();

	/** {@inheritDoc} */
	public String getAttributes (long offset) throws InvalidComponentException {
		
		checkIsValid();
		IA2TextSegment seg = internalGetAttributes(offset);
		if(seg!=null){
			return "start=" + seg.start + ", end=" + seg.end +", text=" +seg.text;
		}
		return null;
		
	}

	protected native IA2TextSegment internalGetAttributes (long offset);

	/** {@inheritDoc} */
	public long getnSelections () throws InvalidComponentException {
		checkIsValid();
		return internalGetSelectionCount();
	}

	protected native long internalGetSelectionCount ();

	/** {@inheritDoc} */
	public String getSelection (long index) throws InvalidComponentException {
		checkIsValid();
		IA2TextSegment sel = internalGetSelectedText(index);
		if(sel!=null){
			return  "[start=" + sel.getStart() + "; end=" + sel.getEnd() + "]";
		}
		return null;
	}

	protected native IA2TextSegment internalGetSelectedText (long index);

	/**
	 * Adds a text selection. 
 	 * @param startOffset -Starting offset ( 0-based). 
	 * @param endOffset - Offset of first character after new selection (0-based).  
	 * @return boolean
	 * @throws InvalidComponentException
	 */
	public boolean addSelection(int startOffset, int endOffset)
	throws InvalidComponentException {
		checkIsValid();
		return internalAddSelection(startOffset,endOffset );
	}
	
	protected native boolean internalAddSelection(int startOffset, int endOffset);
	

    /**
     * Returns the bounding box of the specified position.
     * The virtual character after the last character of
     * the represented text, i.e. the one at position 
     * length is a special case. It represents the 
     * current input position and will therefore
     * typically be queried by AT more often than 
     * other positions. Because it does not represent 
     * an existing character its bounding box is 
     * defined in relation to preceding characters. 
     * It should be roughly equivalent to the bounding box 
     * of some character when inserted at the end of 
     * the text. Its height typically being the maximal
     * height of all the characters in the text or the 
     * height of the preceding character, its width being at 
     * least one pixel so that the bounding box is not 
     * degenerate.
     * @param offset -Index of the character for which to return its bounding box. The valid range is 0..length. 
     * @param coordType -Specifies if the coordinates are relative to the screen or to the parent window. 
     * @return Rectangle -the bounding box of the specified position.
     */
	
    public Rectangle getCharacterExtents(int offset, int coordType)  
	throws InvalidComponentException {
		checkIsValid();
    	int[] cRect = internalGetCharacterBounds( offset, coordType);
    	if(cRect!=null && cRect.length==4){
    		return new Rectangle(cRect[0], cRect[1], cRect[2], cRect[3]);
    	}
    	return null;
    }
    protected  native int[] internalGetCharacterBounds(int offset, int coordType);
    
    public int getOffsetAtPoint(int x,int y, int coordType) throws InvalidComponentException {
		checkIsValid();
    	return internalGetOffsetAtPoint(x, y, coordType);
    }
    protected native int internalGetOffsetAtPoint(int x, int y, int coordType);
    
     public IA2TextSegment getTextBeforeOffset(int offset, int boundaryType) throws InvalidComponentException {
		checkIsValid();
		return internalGetTextBeforeOffset( offset,  boundaryType);
	}
   protected native IA2TextSegment internalGetTextBeforeOffset(int offset, int boundaryType);
   
   public IA2TextSegment getTextAfterOffset(int offset, int boundaryType) throws InvalidComponentException {
		checkIsValid();
		return internalGetTextAfterOffset(offset, boundaryType);
   }
   protected native IA2TextSegment internalGetTextAfterOffset(int offset, int boundaryType);

   public IA2TextSegment getTextAtOffset(int offset, int boundaryType) throws InvalidComponentException {
		checkIsValid();
	   return internalGetTextAtOffset(offset, boundaryType);
   }
   protected native IA2TextSegment internalGetTextAtOffset(int offset, int boundaryType);
   
   public boolean removeSelection(int selIndex)throws InvalidComponentException {
		checkIsValid();
	   return internalRemoveSelection(selIndex);
   }
   protected native boolean internalRemoveSelection(int selIndex);
   
   public boolean setCaretOffset(int offset)throws InvalidComponentException {
		checkIsValid();
	   return internalSetCaretOffset(offset);
   }
   protected native boolean internalSetCaretOffset(int offset);

   public boolean setSelection(int selIndex, int startOffset, int endOffset)throws InvalidComponentException {
		checkIsValid();
	   return internalSetSelection(selIndex, startOffset, endOffset);
   }
   protected native boolean internalSetSelection(int sel, int startOffset, int endOffset);
   
   public int getnCharacters() throws InvalidComponentException {
		checkIsValid();
	   return internalGetCharacterCount();
   }
   protected native int internalGetCharacterCount();
   
   public boolean scrollSubstringTo(int startOffset, int endOffset, int scrollType)throws InvalidComponentException {
	   checkIsValid();
	   return internalScrollToSubstring(startOffset, endOffset, scrollType);
   }
   protected native boolean internalScrollToSubstring(int startOffset, int endOffset, int scrollType);
   
   
   public boolean scrollSubstringToPoint(int startOffset, int endOffset, int scrollType, int x, int y)throws InvalidComponentException {
		checkIsValid();
	   return internalScrollSubstringToPoint(startOffset, endOffset, scrollType, x, y);
  }
  protected native boolean internalScrollSubstringToPoint(int startOffset, int endOffset, int scrollType, int x, int y);
 
   public IA2TextSegment getNewText() throws InvalidComponentException {
	   checkIsValid();
	   return internalGetNewText();
   }
   protected native IA2TextSegment internalGetNewText();
  
   public IA2TextSegment getOldText() throws InvalidComponentException {
		checkIsValid();
		return internalGetOldText();
   }
   protected native IA2TextSegment internalGetOldText();

  protected String getText() throws InvalidComponentException {
	return getText(0, getnCharacters());
}
}