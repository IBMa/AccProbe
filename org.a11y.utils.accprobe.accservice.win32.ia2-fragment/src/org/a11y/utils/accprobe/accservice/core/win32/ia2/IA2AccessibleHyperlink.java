/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core.win32.ia2;


/**
  * 
  * This interface represents hyperlinks. 
  * 
  * This interface represents a hyperlink associated with a 
  * single substring of text or single non-text object. 
  * Non-text objects can have either a single link or a 
  * collection of links such as when the non-text object is 
  * an image map.
  * 
  * Linked objects and anchors are implementation dependent. 
  * This interface reflects all methods found in 
  * <a href="http://accessibility.freestandards.org/a11yspecs/ia2/docs/html/">IAccessibleAction</a>. 
  * IAccessibleAction::nActions indicates the maximum value for 
  * the indices used with the methods of this interface.
  * 
  * Furthermore, the object that implements this interface has 
  * to be connected implicitly or explicitly with an object 
  * that implements IAccessibleText. 
  * IAccessibleHyperlink::startIndex and 
  * IAccessibleHyperlink::endIndex are indices with respect to 
  * the text exposed by IAccessibleText. 
 * @see IA2Accessible
*
 * @author Kavitha Teegala
 */
public class IA2AccessibleHyperlink extends IA2AccessibleAction
{

	// This reference is used to ensure that the IA2Accessible parent
	// does not go out of scope while this class exists.  When IA2Accessible
	// goes out of scope the accRef will be disposed
	private IA2Accessible _parent = null;

	//private int _accRef;

	/**
	 * Constructor used to create an accessible image object
	 * @param val
	 * @param parent IA2Accessible parent of this object
	 */
	public IA2AccessibleHyperlink(int val, IA2Accessible parent) {
			super(val, parent);				
	}

	/**
	 * used by native code only. Clients should not call directly.
	 * @return ptr address for native object
	 */
	/*public int internalRef () {
		return _accRef;
	}
*/
	/**
	 * used before each public method call to confirm that
	 * this object is valid and can return correct information
	 * @throws InvalidComponentException
	 */
	/*private void checkIsValid () throws InvalidComponentException {
		if (_accRef == 0 || _parent == null) { throw new InvalidComponentException("Invalid accessible image"); }
	}*/
	
	/**
	 * Returns an object that represents the link anchor, 
	 * as appropriate for the link at the specified index. 
	 * @param index - A 0 based index identifies the anchor when,
	 *  			as in the case of an image map, there is more than one 
	 * 				link represented by this object. The valid maximal 
	 *  			index is indicated by IAccessibleAction::nActions. 
	 * @return- This is an implementation dependent value.
	 */
	   public Object getAnchor(int index) {

	        return internalGetAnchor(index);
	    }
	   protected native Object internalGetAnchor(int index);

	   /**
		 * Returns an object that represents the target of the link, 
		 * as appropriate for the link at the specified index. 
		 * @param index - A 0 based index identifies the anchor when,
		 *  			as in the case of an image map, there is more than one 
		 * 				link represented by this object. The valid maximal 
		 *  			index is indicated by IAccessibleAction::nActions. 
		 * @return- This is an implementation dependent value.
		 */
	    public Object getAnchorTarget(int index) {

	        return internalGetAnchorTarget(index);
	    }
	    protected native Object internalGetAnchorTarget(int index);
	    
	    /**
	     * Returns the index at which the textual rerpesentation of 
	     * the hyperlink starts. 
	     * 
	     * @return - The returned value is related to the 
	     * 			IAccessibleText interface of the object that 
	     * 			owns this hyperlink
	     */
	    public int getStartIndex() {

	        return  internalGetStartIndex();
	    }
	    protected native int internalGetStartIndex();
	    
	    /**
	     * Returns the index at which the textual rerpesentation of 
	     * the hyperlink ends. 
	     * 
	     * @return - The returned value is related to the 
	     * 			IAccessibleText interface of the object that 
	     * 			owns this hyperlink. The character at the index 
	     * 			is not part of the hypertext. 
	     */
	  
	    public int getEndIndex() {

	        return  internalGetEndIndex();
	    }
	    protected native int internalGetEndIndex();
	    
	    /**
	     * Returns whether the target object referenced by this 
	     * link is still valid. 
	     * 
	     * This is a volatile state that may change without 
	     * sending an appropriate event. Returns TRUE if the 
	     * referenced target is still valid and FALSE otherwise.

	     * @return boolean
	     */
	    public boolean isValid() {

	        return internalIsValid();
	    }
	    
	    protected native boolean internalIsValid();
}
