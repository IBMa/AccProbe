/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core.win32.ia2;

import org.a11y.utils.accprobe.core.model.InvalidComponentException;

/**
 * implementation of <code>IA2AccessibleValue</code>
*
 /**
 * The IA2AccessibleAction interface gives access to 
 * actions that can be executed for accessible objects.
 * Every accessible object that can be manipulated beyond
 * its methods exported over the accessibility API 
 * should support this interface to expose all actions that
 * it can perform. Each action can be performed or queried
 * for a name, description or associated key bindings. 
 * Actions are needed more for ATs that assist the 
 * mobility impaired. By providing actions directly,
 * the AT can present them to the user without the 
 * user having to perform the extra steps to navigate 
 *   a context menu. 

 * @see IA2Accessible
*
 * @author Kavitha Teegala
 */
public class IA2AccessibleAction extends IA2AccessibleElement
{

	// This reference is used to ensure that the IA2Accessible parent
	// does not go out of scope while this class exists.  When IA2Accessible
	// goes out of scope the accRef will be disposed
	private IA2Accessible _parent = null;

	private int _accRef;

	/**
	 * Constructor used to create an accessible action object
	 * @param val
	 * @param parent IA2Accessible parent of this object
	 */
	public IA2AccessibleAction(int val, IA2Accessible parent) {
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
	 * Returns the number of accessible actions available in 
	 * this object.If there are more than one, the first one 
	 * is considered the "default" action of the object. 
	 * @return int -number of actions or zero if there are no actions.  

	 */
    public int getnActions() {

       return internalGetActionCount();
     }
    protected native int internalGetActionCount();
    
    
    /**
     * Performs the specified Action on the object. 
     * 
     * @param actionIndex - This index specifies the action to perform. If it lies outside 
     * 						the valid range no action is performed. 
     * @return boolean
     */
    public boolean doAction(int actionIndex) {

        return  internalDoAccessibleAction(actionIndex) ;
    }
    protected native boolean internalDoAccessibleAction(int actionIndex) ;

    
    /**
     * Returns a description of the specified action of the object
     * @return - The returned value is a localized string of the specified action. 
     */
    public String[] getDescription() {
    	int actionCount = getnActions();
    	if(actionCount==0){
    		return null;
    	}
    	String[] desc = new String[actionCount];
    	for(int i=0; i< desc.length; i++){
         desc[i]= internalGetActionDescription(i) ;
    	}
        
        return desc;
    }
    protected native String internalGetActionDescription(int actionIndex) ;
   
    /**
     * Returns an array of strings describing one or more key 
     * bindings, if there are any, associated with the 
     * specified action. 
     * @param actionIndex
     * @param nMaxBinding
     * @return String[]
     */
    public String[] getKeyBinding(int actionIndex, int nMaxBinding) {
    
        return internalGetActionKeyBinding(actionIndex, nMaxBinding) ;
    }
    protected native String[] internalGetActionKeyBinding(int actionIndex, int nMaxBinding) ;

    /**
     * Returns the non-localized name of specified action. 
     * @return Stringp[]
     */
    public String[] getName() {
    	int actionCount = getnActions();
    	if(actionCount==0){
    		return null;
    	}
    	String[] names = new String[actionCount];
    	for(int i=0; i< names.length; i++){
         names[i]= internalGetActionName(i) ;
         if(names[i]==null){
        	 names[i] ="";
         }
    	}
        
        return names;
    }
    protected native String internalGetActionName(int actionIndex);

    /**
     * Returns the localized name of specified action
     * @return String[]
     */
    public String[] getLocalizedName() {
    	int actionCount = getnActions();
    	if(actionCount==0){
    		return null;
    	}
    	String[] names = new String[actionCount];
    	for(int i=0; i< names.length; i++){
         names[i]= internalGetLocalizedAccessibleActionName(i) ;
         if(names[i]==null){
        	 names[i] ="";
         }
    	}
        
        return names;
    }
    
    protected native String internalGetLocalizedAccessibleActionName(int actionIndex);

}
