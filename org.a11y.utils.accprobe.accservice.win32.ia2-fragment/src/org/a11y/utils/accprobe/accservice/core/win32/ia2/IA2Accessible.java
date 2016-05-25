/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core.win32.ia2;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.a11y.utils.accprobe.accservice.core.win32.msaa.Msaa;
import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaAccessible;
import org.a11y.utils.accprobe.accservice.core.AccessibleConstants;
import org.a11y.utils.accprobe.accservice.core.IAccessibleEditableTextElement;
import org.a11y.utils.accprobe.accservice.core.IAccessibleElement;
import org.a11y.utils.accprobe.accservice.core.IAccessibleElement2;
import org.a11y.utils.accprobe.accservice.core.IAccessibleImageElement;
//import org.a11y.utils.accprobe.accservice.core.IAccessibleTableElement;
import org.a11y.utils.accprobe.accservice.core.IAccessibleTextElement;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;


/**
 * implementation of <code>IAccessibleElement</code> for GUI controls that implement IAccessible2/IBM interfaces.
 * 
 * <p>This class is a wrapper for an IAccessible2 pointer, a pointer that Provides
 * access to a native Windows object that provides assistive technologies (ATs) with properties of GUI components 
 * that allow the AT to offer an alternative interface to the control. This class relies upon JCAccessible.dll
 * for most of its implementation. The documentation for the Microsoft COM
 * library and, in particular, for IAccessible2/IBM will be helpful.
 * 
 * @author Mike Smith, Kavitha Teegala
 */

public class IA2Accessible extends MsaaAccessible implements IAccessibleElement2
{

	/**
	 * construct an IA2Accessible from a window handle. To 
	 * create a MsaaAccessible from a handle for an SWT control, use a child id of
	 * <code>ACC.CHILDID_SELF</code>.
	 * 
	 * @param hwnd -
	 *            window handle for an SWT control
	 * @param childID -
	 *            child ID (if any)
	 */
	public IA2Accessible (int hwnd, int childID) {
		super(hwnd, childID);
	}

	/**
	 * create an IA2Accessible element from the given control. Currently, supported
	 * controls include:
	 * 
	 *  <p><ul>
	 *  <li>SWT controls
	 *  <li>any component implementing the Windows <code>IHTMLElement</code> interface
	 *  </ul>
	 *  
	 * @param control
	 */
	public IA2Accessible (final Object control) {
		// TODO: eventually this will need to be 
		// written to implement this functionality for IA2
		super(control);
	}

	protected void initFromHwnd (int hwnd, int childID) {
		accRef = internalInitFromHwnd(hwnd, childID);
		if (accRef != 0) {
			this.hwnd = hwnd ;
			this.childId = childID;
		}
	}

	protected void initFromPoint (int x, int y) {
		accRef = internalInitFromPoint(x, y);
		if (accRef > 0) {
			this.hwnd = internalGetWindowHandle();
			this.childId = internalGetChildId();
		}
	}

	protected void initFromHtmlElement (int htmlElemRef) {
		accRef = internalInitFromHtmlElement(htmlElemRef);
		if (accRef > 0) {
			this.hwnd = internalGetWindowHandle();
			this.childId = internalGetChildId();
		}
	}

//	protected native int internalGetWindowHandle ();
	protected native int internalGetChildId ();
	protected native static int internalInitFromHwnd (int hwnd, int childID);
	protected native static int internalInitFromHtmlElement (int htmlElemRef);
	protected native static int internalInitFromPoint (int x, int y);

	/**
	 * create an IA2Accessible element by utilizing the MSAA function <code>AccessibleObjectFromPoint</code>.
	 *  
	 * @param location - any location on the current display device
	 */
	public IA2Accessible (Point location) {
		super(location);
	}

	/**
	 * create an IA2Accessible element based on a reference pointer
	 *  
	 * @param ref - reference point
	 */
	public IA2Accessible (int ref) {
		super(ref);
		IA2AccessibilityService.internalCoInitialize();
	}

	/** {@inheritDoc} */
	
	public IAccessibleElement getAccessibleParent () throws InvalidComponentException {
		checkIsValid();
		IAccessibleElement parent = super.getAccessibleParent();
		
		// if parent of this has role 'window' and has the
		// NODE_CHILD_OF relation set, then we'l use that relation target instead
		//In certain cases when the actual parent is indeed a window (such as frame), 
		//node_child_of relation target will not have a valid window handle. In that
		//in that case we leave the parent as is otherwise the target becomes the parent.
		if(parent!=null && parent.getAccessibleRole().equals(AccessibleConstants.ROLE_WINDOW)){
			Map<String, IAccessibleElement[]> relations = getAccessibleRelations();
			if (relations!=null && relations.containsKey(AccessibleConstants.RELATION_NODE_CHILD_OF)) {
				IAccessibleElement acc = relations.get(AccessibleConstants.RELATION_NODE_CHILD_OF)[0];
				if (((MsaaAccessible) acc).getWindowHandle()!=0){
					parent= acc;
				}
			}
		}
		return parent;
	}
	//protected native int internalGetAccParent ();
	/*
	public IAccessibleElement[] getAccessibleChildren () throws InvalidComponentException {
	checkIsValid();
	ArrayList children = new ArrayList();
	IAccessibleElement[] result = null;
	int childCount = getAccessibleChildCount();
	if(childCount > MAX_CHILDREN){
		return null;
	}
	if(childCount >0){
		int[] childRefs = internalGetIA2AccessibleChildren ();
		if (childRefs!=null && childRefs.length > 0) {
			for (int i = 0; i < childRefs.length; i++) {
				IA2Accessible acc = new IA2Accessible(childRefs[i]);
				if (acc != null) {
					acc.indexInParent = i;
					children.add(acc);
				}
			}
			result = (IAccessibleElement[]) children.toArray(new IAccessibleElement[children.size()]);
		} else {
			result = super.getAccessibleChildren();
		}
	}
	return result;
}
	//protected native int[] internalGetIA2AccessibleChildren ();
	*/
	/**
	 * return the child with the given ID. This id is not an index into the
	 * children of a IA2ccessible object, though children are typically numbered
	 * as such. Also, note that the returned IA2Accessible need not correspond to
	 * an SWT control.
	 * 
	 * @param childID ID of desired child
	 * @return child of this IA2Accessible with ID
	 * @throws InvalidComponentException
	 */
	public IAccessibleElement getAccessibleChild (int childID) throws InvalidComponentException {
		checkIsValid();
		int accRef = internalGetIA2AccessibleChild(childID);
		IAccessibleElement acc = null;
		if (accRef != 0) {
			acc = new IA2Accessible(accRef);
		} else {
			acc = super.getAccessibleChild(childID);
		}
		return acc;
	}
	protected native int internalGetIA2AccessibleChild (int childID);

	/** {@inheritDoc} */
	public Object getAccessibleValue2() throws InvalidComponentException {
		checkIsValid();
		Object res = null;
		int ref = internalGetAccessibleValue2();
		if (ref != 0) {
			res = new IA2AccessibleValue(ref, this);
		}
		return res;
	}
	protected native int internalGetAccessibleValue2();

	public IA2AccessibleApplication getAccessibleApplication() throws InvalidComponentException {
		checkIsValid();
		IA2AccessibleApplication res = null;
		int ref = internalGetAccessibleApplication();
		if (ref != 0) {
			res = new IA2AccessibleApplication(ref, this);
		}
		return res;
	}
	protected native int internalGetAccessibleApplication();
	
	public Object getAccessibleAction2() throws InvalidComponentException {
		checkIsValid();
		Object res = null;
		int ref = internalGetAccessibleAction2();
		if (ref != 0) {
			res = new IA2AccessibleAction(ref, this);
		}
		return res;
	}
	protected native int internalGetAccessibleAction2();
	
	public IA2AccessibleComponent getAccessibleComponent() throws InvalidComponentException {
		checkIsValid();
		IA2AccessibleComponent res = null;
		int ref = internalGetAccessibleComponent();
		if (ref != 0) {
			res = new IA2AccessibleComponent(ref, this);	
		}
		return res;
	}
	protected native int internalGetAccessibleComponent();
	
	public IA2AccessibleHypertext getAccessibleHypertext() throws InvalidComponentException {
		checkIsValid();
		IA2AccessibleHypertext res = null;
		int ref = internalGetAccessibleHyperText();
		if (ref != 0) {
			res = new IA2AccessibleHypertext(ref, this);	
		}
		return res;
	}
	protected native int internalGetAccessibleHyperText();
	
	public IA2AccessibleHyperlink getAccessibleHyperlink() throws InvalidComponentException {
		checkIsValid();
		IA2AccessibleHyperlink res = null;
		int ref = internalGetAccessibleHyperLink();
		if (ref != 0) {
			res = new IA2AccessibleHyperlink(ref, this);	
		}
		return res;
	}
	protected native int internalGetAccessibleHyperLink();
	
	/** {@inheritDoc} */
	public IA2AccessibleTable getAccessibleTable () throws InvalidComponentException {
		checkIsValid();
		IA2AccessibleTable res = null;
		int ref = internalGetAccessibleTable();
		if (ref != 0) {
			res = new IA2AccessibleTable(ref, this);
		}
		return res;
	}
	protected native int internalGetAccessibleTable ();
	
	public IAccessibleTable2 getAccessibleTable2 () throws InvalidComponentException {
		checkIsValid();
		IAccessibleTable2 res = null;
		int ref = internalGetAccessibleTable2();
		if (ref != 0) {
			res = new IAccessibleTable2(ref, this);
		}
		return res;
	}
	protected native int internalGetAccessibleTable2 ();

	public IAccessibleTableCell getAccessibleTableCell () throws InvalidComponentException {
		checkIsValid();
		IAccessibleTableCell res = null;
		int ref = internalGetAccessibleTableCell();
		if (ref != 0) {
			res = new IAccessibleTableCell(ref, this);
		}
		return res;
	}
	protected native int internalGetAccessibleTableCell ();

	/** {@inheritDoc} */
	public IAccessibleTextElement getAccessibleText () throws InvalidComponentException {
		checkIsValid();
		IA2AccessibleText res = null;
		int ref = internalGetAccessibleText();
		if (ref != 0) {
			res = new IA2AccessibleText(ref, this);
		}
		return res;
	}
	protected native int internalGetAccessibleText ();

	/** {@inheritDoc} */
	public IAccessibleImageElement[] getAccessibleImage () throws InvalidComponentException {
		checkIsValid();
		IAccessibleImageElement[] res = null;
		int ref[] = internalGetAccessibleImage();
		if (ref != null) {
			res = new IAccessibleImageElement[ref.length];
			for (int i = 0; i < ref.length; i++) {
				res[i] = new IA2AccessibleImage(ref[i], this);
			}
		}
		return res;
	}
	protected native int[] internalGetAccessibleImage ();

	/** {@inheritDoc} */
	public String getAccessibleRole () throws InvalidComponentException {
		checkIsValid();
		long role = internalGetAccessibleRoleAsLong();
		//first, try to get IA2 role as long
		String res = IA2.getIA2A11yRoleName(role);
		if (res == null || res.length() < 1) {
			res = Msaa.getMsaaA11yRoleName(role);
		}
		//then, try to see if role is returned as string
		if (res == null || res.length() < 1) {
			res = internalGetAccessibleRole();
		}
		//if above both are null, get MSAA role
		if (res == null || res.length() < 1) {
			res = super.getAccessibleRole();
		}
		return res;
	}
	protected native long internalGetAccessibleRoleAsLong ();
	protected native String internalGetAccessibleRole();

	/** {@inheritDoc} */
	public Set getAccessibleState () throws InvalidComponentException {
		checkIsValid();
		HashSet res = new HashSet();
		long state = internalGetStates();
		Set msaaState = super.getAccessibleMsaaState();
		// load all the msaa states into the result
		if (msaaState != null) {
			res.addAll(msaaState);
		}
		Set ia2States = ia2States(state);
		res.addAll(ia2States);
		
		return res;
	}

	private Set<String> ia2States(long state) {
		HashSet<String> res = new HashSet<String>();
		if ((state & IA2.IA2_STATE_ACTIVE) != 0) {
			res.add(AccessibleConstants.STATE_ACTIVE);
		}
		if ((state & IA2.IA2_STATE_ARMED) != 0) {
			res.add(AccessibleConstants.STATE_ARMED);
		}
		if ((state & IA2.IA2_STATE_DEFUNCT) != 0) {
			res.add(AccessibleConstants.STATE_DEFUNCT);
		}
		if ((state & IA2.IA2_STATE_EDITABLE) != 0) {
			res.add(AccessibleConstants.STATE_EDITABLE);
		}
		if ((state & IA2.IA2_STATE_HORIZONTAL) != 0) {
			res.add(AccessibleConstants.STATE_HORIZONTAL);
		}
		if ((state & IA2.IA2_STATE_ICONIFIED) != 0) {
			res.add(AccessibleConstants.STATE_ICONIFIED);
		}
		if ((state & IA2.IA2_STATE_INVALID_ENTRY) != 0) {
			res.add(AccessibleConstants.STATE_INVALID_ENTRY);
		}
		if ((state & IA2.IA2_STATE_MANAGES_DESCENDANTS) != 0) {
			res.add(AccessibleConstants.STATE_MANAGES_DESCENDENTS);
		}
		if ((state & IA2.IA2_STATE_MODAL) != 0) {
			res.add(AccessibleConstants.STATE_MODAL);
		}
		if ((state & IA2.IA2_STATE_MULTI_LINE) != 0) {
			res.add(AccessibleConstants.STATE_MULTI_LINE);
		}
		if ((state & IA2.IA2_STATE_OPAQUE) != 0) {
			res.add(AccessibleConstants.STATE_OPAQUE);
		}
		if ((state & IA2.IA2_STATE_REQUIRED) != 0) {
			res.add(AccessibleConstants.STATE_REQUIRED);
		}
		if ((state & IA2.IA2_STATE_SELECTABLE_TEXT) != 0) {
			res.add(AccessibleConstants.STATE_SELECTABLE_TEXT);
		}
		if ((state & IA2.IA2_STATE_SINGLE_LINE) != 0) {
			res.add(AccessibleConstants.STATE_SINGLE_LINE);
		}
		if ((state & IA2.IA2_STATE_STALE) != 0) {
			res.add(AccessibleConstants.STATE_STALE);
		}
		if ((state & IA2.IA2_STATE_SUPPORTS_AUTOCOMPLETION) != 0) {
			res.add(AccessibleConstants.STATE_SUPPORT_AUTOCOMPLETION);
		}
		if ((state & IA2.IA2_STATE_TRANSIENT) != 0) {
			res.add(AccessibleConstants.STATE_TRANSIENT);
		}
		if ((state & IA2.IA2_STATE_VERTICAL) != 0) {
			res.add(AccessibleConstants.STATE_VERTICAL);
		}
		if ((state & IA2.IA2_STATE_CHECKABLE) != 0) {
			res.add(AccessibleConstants.STATE_CHECKABLE);
		}
		if ((state & IA2.IA2_STATE_PINNED) != 0) {
			res.add(AccessibleConstants.STATE_PINNED);
		}
		
		return res;
	}
	protected native long internalGetAccessibleStateAsLong ();

	public static boolean isIA2Accessible (IAccessibleElement accElem) {
		boolean res = false;
		
		if (MsaaAccessible.class.isAssignableFrom(accElem.getClass())) {
			MsaaAccessible acc = (MsaaAccessible) accElem;
			res = IA2Accessible.internalIsIA2Accessible(acc.getAccessibleAddress());
		}
		
		return res;
	}
	
	public static int  IA2FromIAcc (IAccessibleElement accElem) {
		int ia2Acc =0;
		
		if (MsaaAccessible.class.isAssignableFrom(accElem.getClass())) {
			MsaaAccessible acc = (MsaaAccessible) accElem;
			ia2Acc = IA2Accessible.internalGetIA2fromIAcc(acc.getRef());
		}
		
		return ia2Acc;
	}
	protected native int internalGetAddress();
	protected native static boolean internalIsIA2Accessible (int iacc);
	protected native static int internalGetIA2fromIAcc (int iacc);

	/////////////////////////////////////////////new release code///
	
	public IAccessibleEditableTextElement getAccessibleEditableText() throws InvalidComponentException {
		checkIsValid();
		IAccessibleEditableTextElement res = null;
		int ref = internalGetAccessibleEditableText();
		if (ref != 0) {
			res = new IA2AccessibleEditableText(ref, this);
		}
		return res;
	}
	 protected native int internalGetAccessibleEditableText();
    
	public int getAccessibleRelationCount()  throws InvalidComponentException {
	      return internalGetNRelations();
	    }
	 protected native int internalGetNRelations();
	 
	
	public Map<String, IAccessibleElement[]> getRelation(int relationIndex ) throws InvalidComponentException { 
		Map<String, IAccessibleElement[]> res = getAccessibleRelations();
		if(res==null || res.isEmpty()){
			return null;
		}
		HashMap<String, IAccessibleElement[]> newRel = new HashMap<String, IAccessibleElement[]>();
		String[] keyArray = res.keySet().toArray(new String[0]);
		IAccessibleElement[][] vals = res.values().toArray(new IAccessibleElement[0][]);
		
		newRel.put(keyArray[relationIndex], vals[relationIndex]);
	
		return newRel;
	 }
	 protected native String internalGetAccessibleRelation(int relationIndex);
	 
  
  	/** {@inheritDoc} */
	public Map<String, IAccessibleElement[]> getAccessibleRelations () throws InvalidComponentException {
		checkIsValid();
		int maxRelations =getAccessibleRelationCount();
		String[] relArray = internalGetAccessibleRelations(maxRelations);
		
		//the string array returned by the native code contains the keys 
		//and values in the alternate order(Eg: ["memberof","23652376","labelled by","23289665:23232322"]).
		//The key is the relationType such as 'memberof', 'labelledby' etc.
		//and the value is the IA2 pointer to the target object of the relation. In some cases , 
		//there more than one targets. In such cases, the value string
		//is a combination of IA2 pointers concatenated by ":".
		if ( relArray!=null && relArray.length >0){
			HashMap<String, IAccessibleElement[]> res = new HashMap<String, IAccessibleElement[]>();
			for (int i =0; i< relArray.length; i++){
				if(i%2 == 1){
					String[] ia2Refs = relArray[i].split(":");
					ArrayList<IA2Accessible> targetList = new ArrayList<IA2Accessible>();
					for (int r = 0; r <  ia2Refs.length; ++r) {
						int ia2Ref = Integer.parseInt(ia2Refs[r]);
						IA2Accessible target = new IA2Accessible(ia2Ref);
						if (target != null) {
							targetList.add(target);
						}
					}
					res.put(new String(relArray[i-1]),targetList.toArray(new IAccessibleElement[targetList.size()]));
				}
			}
			
			return res;
		}
		
		return null;
	}
	protected native String[] internalGetAccessibleRelations(int maxRelations);
	
    public boolean scrollTo(int topLeft) throws InvalidComponentException {
    	return internalScrollTo(topLeft);
    }
    protected native boolean internalScrollTo(int topLeft);
    
    public boolean scrollToPoint(int coordinateType, int x, int y) throws InvalidComponentException {
   	return internalScrollToPoint( coordinateType,  x, y);
   }
   protected native boolean internalScrollToPoint(int coordinateType, int x, int y);
   
    public Object getAccessibleGroupPosition() throws InvalidComponentException {
    	int[] gp = internalGetGroupPosition();
    	String[] group = {"groupLevel","similarItemsInGroup","positionInGroup"};
    	if(gp != null && gp.length > 0) {
    		ArrayList<String> sr =  new ArrayList<String>();
    		for (int i=0; i< gp.length; i++){
    			sr.add(group[i]+ "="+ gp[i]);
    		}
    		return sr.toString();
    	}
        return null;
    }
    protected native int[] internalGetGroupPosition();
   
    public Set<String> getAccessibleStates() throws InvalidComponentException {
    	return ia2States(internalGetStates());
    }
    protected native int internalGetStates();
    
//    public Set getAccessibleLocalizedStateNames() 
//	throws InvalidComponentException {
//    	int maxLocalizedStateNames =10;
//    	HashSet res = null;
//    	String[] strArray = internalGetLocalizedStateNames(maxLocalizedStateNames);
//      
//    	String strObject;
//    	int i;
//    	if(strArray !=null && strArray.length >0)
//    	{
//    		res  = new HashSet();
//	    	/* Populate the Java collection */
//	    	for (i=0; i < strArray.length; i++)
//	    	   {
//	    	   strObject = new String(strArray[i]);
//	    	   res.add(strObject);
//	    	   }
//    	}
//   	
//    	return res;
//    }
//    protected native String[] internalGetLocalizedStateNames(int maxLocalizedStateNames);
//    
    public String getAccessibleExtendedRole() 
	throws InvalidComponentException {
        return internalGetExtendedRole();
    }
    protected native String internalGetExtendedRole();

    public String getAccessibleLocalizedExtendedRole() throws InvalidComponentException {
       checkIsValid();
       return internalGetLocalizedExtendedRole();
    }
    protected native String internalGetLocalizedExtendedRole();
  
    public int getAccessibleExtendedStateCount()  throws InvalidComponentException {
        checkIsValid();
        return internalGetExtendedStateCount();
    }
	protected native int internalGetExtendedStateCount();
	
    public Set<String> getAccessibleExtendedStates()  throws InvalidComponentException {
    	checkIsValid();
    	int maxExtendedStates = getAccessibleExtendedStateCount();
    	HashSet<String> res = null;
    	String[] strArray = internalGetExtendedStates( maxExtendedStates);
      
    	String strObject;
    	int i;
    	if(strArray !=null && strArray.length >0)
    	{
    		res = new HashSet<String>();
	    	/* Populate the Java collection */
	    	for (i=0; i < strArray.length; i++)	{
	    	   strObject = new String(strArray[i]);
	    	   res.add(strObject);
	    	}
    	}
    	return res;
    }
	protected native String[] internalGetExtendedStates(int maxExtendedStates);
	
    public Set<String> getAccessibleLocalizedExtendedStates()  throws InvalidComponentException {
    	checkIsValid();
    	int maxLocalizedExtendedStates =getAccessibleExtendedStateCount();
    	HashSet<String> res = null;
    	String[] strArray = internalGetLocalizedExtendedStates(maxLocalizedExtendedStates);
      
    	String strObject;
    	int i;
    	if(strArray != null && strArray.length > 0) {
    		res = new HashSet<String>();
	    	/* Populate the Java collection */
	    	for (i=0; i < strArray.length; i++) {
	    	   strObject = new String(strArray[i]);
	    	   res.add(strObject);
	    	}
    	}
    	return res;
    }
	protected native String[] internalGetLocalizedExtendedStates(int maxLocalizedExtendedStates);
	
    public int getUniqueID()  throws InvalidComponentException {
    	checkIsValid();
    	return internalGetUniqueID();
    }
	protected native int internalGetUniqueID();
	
    public int getAccessibleWindowHandle()  throws InvalidComponentException {
    	checkIsValid();
    	return internalGetIA2WindowHandle();
    }
    protected native int internalGetIA2WindowHandle();

    public int getAccessibleIndexInParent()  throws InvalidComponentException {
    	checkIsValid();
    	return internalGetIndexInParent();
    }
	protected native int internalGetIndexInParent();
	
    public Locale getAccessibleLocale()  throws InvalidComponentException {
    	checkIsValid();
    	String[] ls = internalGetLocale();
    	if(ls !=null && ls.length==3)
    	{
    		if(ls[0]!=null && ls[1]!=null && ls[2]!=null)
    			return  new Locale(ls[0], ls[1], ls[2]);
    	}
    	return null;
    }
    protected native String[] internalGetLocale();
    
    public String getAccessibleAttributes()  throws InvalidComponentException {
    	checkIsValid();
    	return internalGetAttributes();
    }
    protected native String internalGetAttributes();
	
	public boolean equals (Object other) {
		boolean result = other != null && other instanceof IA2Accessible;
		if (result) {
			IA2Accessible accOther = (IA2Accessible) other;
			try {
				result = this.getWindowHandle() == accOther.getWindowHandle()
					&& this.getChildId() == accOther.getChildId()
					&& this.getUniqueID() == accOther.getUniqueID();
			} catch (InvalidComponentException e) {
				result = false;
			}
		}
		
		return result;
	}

	public boolean doDefaultAction()throws InvalidComponentException {
		checkIsValid();
		boolean ret = false;
		try {
			IA2AccessibleAction act = (IA2AccessibleAction) getAccessibleAction();
			if( act!=null){
				int  ct = act.getnActions();
				for(int i=0;i<ct;i++){
					ret = act.doAction(i);
				}
			}else{
				ret = super.accDoDefaultAction(getChildId());
			}
				
		} catch (InvalidComponentException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	protected void finalize () throws Throwable {
		super.finalize();
		IA2AccessibilityService.internalCoUnInitialize();
	}
	public static Map<String, String> propertyMap(){
		Map<String, String> pMap = new HashMap<String, String>();
		//pMap = MsaaAccessible.propertyMap();
		pMap.put("accessibleAction2", "IAccessibleAction");
		pMap.put("accessibleApplication", "IAccessibleApplication");
		pMap.put("accessibleComponent", "IAccessibleComponent");
		pMap.put("accessibleEditableText", "IAccessibleEditableText");
		pMap.put("accessibleHyperlink", "IAccessibleHyperlink");
		pMap.put("accessibleHypertext", "IAccessibleHypertext");
		pMap.put("accessibleImage", "IAccessibleImage");
		pMap.put("accessibleTable", "IAccessibleTable");
		pMap.put("accessibleTable2", "IAccessibleTable2");
		pMap.put("accessibleTableCell", "IAccessibleTableCell");
		pMap.put("accessibleText", "IAccessibleText");
		pMap.put("accessibleValue2", "IAccessibleValue");
		pMap.put("accessibleAttributes", "attributes");
		pMap.put("accessibleExtendedRole", "extendedRole");
		pMap.put("accessibleExtendedStateCount", "nExtendedStates");
		pMap.put("accessibleExtendedStates", "extendedStates");
		pMap.put("accessibleGroupPosition", "groupPosition");
		pMap.put("accessibleIndexInParent", "indexInParent");
		pMap.put("accessibleLocale", "locale");
		pMap.put("accessibleLocalizedExtendedRole", "localizedExtendedRole");
		pMap.put("accessibleLocalizedExtendedStates", "localizedExtendedStates");
		pMap.put("accessibleRelationCount", "nRelations");
		pMap.put("accessibleRelations", "relations");
		pMap.put("accessibleRole", "role");
		pMap.put("accessibleStates", "states");
		pMap.put("accessibleWindowHandle", "windowHandle");
		pMap.put("uniqueID", "uniqueID");
		pMap.put("relation", "relation");
		pMap.put("scrollTo", "scrollTo");
		pMap.put("scrollToPoint", "scrollToPoint");

		return pMap;
	}
}
