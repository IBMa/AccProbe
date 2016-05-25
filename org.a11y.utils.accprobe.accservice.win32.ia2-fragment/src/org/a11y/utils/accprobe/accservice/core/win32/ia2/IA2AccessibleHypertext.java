/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core.win32.ia2;


/**
 * implementation of <code>IA2AccessibleHypertext</code>
*
 /**
 * The IA2AccessibleHyperText interface exposes information about 
 * hypertext in a document. The IAccessibleHypertext interface is 
 * the main interface to expose hyperlinks in a document, typically 
 * a text document, that are used to reference other documents. A 
 * typical implementation is to implement this interface the 
 * smallest text object such as a paragraph of text. 
 * @see IA2Accessible
 * @see IA2Accessible#getAccessibleHypertext
*
 * @author Kavitha Teegala
 */
public class IA2AccessibleHypertext extends IA2AccessibleText
{

	// This reference is used to ensure that the IA2Accessible parent
	// does not go out of scope while this class exists.  When IA2Accessible
	// goes out of scope the accRef will be disposed
	private IA2Accessible _parent = null;
	//private int _accRef =0;

	/**
	 * Constructor used to create an accessible image object
	 * @param val
	 * @param parent IA2Accessible parent of this object
	 */
	public IA2AccessibleHypertext(int val, IA2Accessible parent) {
			super( val, parent);
			//_accRef  = super.internalRef();
	}

	/**
	 * used by native code only. Clients should not call directly.
	 * @return ptr address for native object
	 */
	/*public int internalRef () {
		return _accRef;
	}*/

	/**
	 * used before each public method call to confirm that
	 * this object is valid and can return correct information
	 * @throws InvalidComponentException
	 */
//	private void checkIsValid () throws InvalidComponentException {
//		if (_accRef == 0 || _parent == null) { throw new InvalidComponentException("Invalid accessible image"); }
//	}

	/**
	 * Returns the number of Hyperlinks and link groups 
	 * contained within this hypertext paragraph.
	 *  
	 * @return - number of links
	 */
    public int getnHyperlinks() {
        return  internalGetHyperlinkCount();
    }
    protected native int internalGetHyperlinkCount();
    /**
     * Returns the specified link. The returned 
     * IAccessibleHyperlink object encapsulates the hyperlink 
     * and provides several kinds of information describing it.
     * @param index
     * @return IA2AccessibleHyperlink
     */
    public IA2AccessibleHyperlink getHyperlink(int index) {
    	int hlRef = internalGetHyperlink(index);
    	if(hlRef!=0)
    	{
    		return new IA2AccessibleHyperlink(hlRef, _parent);
    	}
        return null;
    }
    
     protected native int internalGetHyperlink(int index) ;
    /**
     * Returns the index of the hyperlink that is 
     * associated with this character index.This is the 
     * case when a link spans the given character index. 
     * @param charIndex
     * @return int
     */
    public int getHyperlinkIndex(int charIndex) {

        return internalGetHyperlinkIndex(charIndex);
    }
    protected native int internalGetHyperlinkIndex(int charIndex);
}
