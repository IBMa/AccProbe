/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core.win32.msaa;

import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.a11y.utils.accprobe.accservice.AccessibilityServiceException;
import org.a11y.utils.accprobe.accservice.AccessibilityServiceManager;
import org.a11y.utils.accprobe.accservice.core.IAccessibleElement;
import org.a11y.utils.accprobe.core.logging.LoggingUtil;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;
import org.a11y.utils.accprobe.core.resources.ClassLoaderCache;


/**
 * implementation of <code>IAccessibleElement</code> for GUI controls that implement Microsoft Active Accessibility (MSAA) interfaces.
*
 * <p>This class is a wrapper for an IAccessible pointer, a pointer that Provides
 * access to a native Windows object that provides assistive technologies (ATs) with properties of GUI components 
 * that allow the AT to offer an alternative interface to the control. This class relies upon a11y-msaa.dll
 * for most of its implementation. The documentation for the Microsoft COM
 * library and, in particular, for MSAA will be helpful.
*
 * @see <a href="http://msdn.microsoft.com/library/default.asp?url=/library/en-us/msaa/msaastart_9w2t.asp">The MSDN MSAA Documentation</a>
 * @author Mike Squillace, Barry Feigenbaum
 */
public class MsaaAccessible implements IAccessibleElement
{

	protected static final String SWT_CTRL_TYPENAME = "org.eclipse.swt.widgets.Control";
	protected static final String SWT_WIDGET_TYPENAME = "org.eclipse.swt.widgets.Widget";
	protected static final String SWT_ACC_TYPENAME = "org.eclipse.swt.accessibility.ACC";
	public static int childId_self = -1; // ACC.CHILDID_SELF
	
	protected static boolean highLightEnabled =false;
	protected static boolean computingIndexInParent = false;
	
	protected int accRef; // pointer to helper class that wraps an MSAA IAccessible 
	protected int hwnd = -1; // native window handle
	protected int childId;
	protected Object element;
	protected int indexInParent = -1;
	public static String errString = "HRESULT = ";
	protected String howFound ="Navigating the tree";
	
	protected Map<String, String> errorCodeReturnMap = new HashMap<String, String>();
	protected static final int MAX_CHILDREN = 1000;
	private Logger logger = Logger.getLogger(LoggingUtil.ACCSERVICE_LOGGER_NAME);
	
		/**
	 * wrap the given object as an A11Y <code>IAccessibleElement</code>. The 
	 * A11Y engine will invoke this constructor using a registered adaptor factory. Clients do not 
	 * typically call this constructor.
	 * 
	 * <p>Note: To create a MsaaAccessible from a handle for an SWT control, use a child id of
	 * <code>ACC.CHILDID_SELF</code>.
	 * 
	 * @param hwnd -
	 *            window handle for an SWT control
	 * @param childID -
	 *            child ID (if any)
	 */
	public MsaaAccessible (int hwnd, int childID) {
		initFromHwnd(hwnd, childID);
	}

	/**
	 * wrap the given object as a A11Y <code>IAccessibleElement</code>. The 
	 * A11Y engine will invoke this constructor using a registered adaptor factory. Clients do not 
	 * typically call this constructor.
	 * 
	 * <p>Currently, supported controls include:
	 * 
	 *  <p><ul>
	 *  <li>SWT controls
	 *  <li>any component implementing the Windows <code>IHTMLElement</code> interface
	 *  </ul>
	 *  
	 * @param elem
	 */
	public MsaaAccessible (Object elem) {
		try {
			ClassLoaderCache cache = ClassLoaderCache.getDefault();
			Class<?> ctrlCls = cache.classForName(SWT_CTRL_TYPENAME);
			Class<?> accCls = cache.classForName(SWT_ACC_TYPENAME);
			if (ctrlCls.isAssignableFrom(elem.getClass())) {
				int handle = ctrlCls.getField("handle").getInt(elem);
				childId_self = accCls.getField("CHILDID_SELF").getInt(null);
				initFromHwnd(handle, childId_self);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
			hwnd = -1;
		}
		element = elem;
	}

	/**
	 * create an MsaaAccessible element by utilizing the MSAA function <code>AccessibleObjectFromPoint</code>.
	 *  
	 * @param location - any location on the current display device
	 */
	public MsaaAccessible (Point location) {
		initFromPoint(location.x, location.y);
	}

	/**
	 * create a MsaaAccessible from an IAccessible pointer.
	 * 
	 * @param ref - pointer value
	 */
	public MsaaAccessible (int ref) {
		accRef = ref;
		try {
			MsaaAccessibilityService.internalCoInitialize();
			this.hwnd = internalGetWindowHandle();
			this.childId = internalGetChildId();
		}catch (Throwable e) {
			hwnd = -1;
			accRef = 0;
		}
	}

	/** {@inheritDoc} */
	public Object element () {
		return element;
	}

	public int getRef () {
		return internalRef();
	}
	/**
	 * used by native code only. Clients should not call directly.
	 * @return ptr address for native object
	 */
	protected int internalRef () {
		return accRef;
	}

	public int getAccessibleAddress () {
		return internalGetAddress();
	}
	protected native int internalGetAddress();
	
	public int getWindowHandle () {
		return hwnd ;
	}
	
	/**
	 * get the handle value as a hex string
	 * 
	 * @return handle value as a hex string
	 */
	public String  getWindowHandleAsHex () {
		return getHex(hwnd);
	}

	public static String getHex(int x){
		String id = Integer.toHexString(x).toUpperCase();
		return "0x"+"00000000".substring(0, 8 - id.length()) + id;
	}
	/**
	 * get the child ID for this MsaaAccessible, if any. Child IDs are associated
	 * with "simple" (i.e. non-IAccessible) children starting at 0. The child ID
	 * for a control (rather than any of its children) is
	 * <code>ACC.CHILDID_SELF</code>
	 * 
	 * @return child ID
	 */
	public int getChildId () {
		return childId;
	}

	/**
	 * tests whether or not this MsaaAccessible is in a valid state. Validity
	 * consists of:
	 * <p><ul>
	 * <li>the handle for the SWT control associated with this MsaaAccessible (if
	 * any) is a valid window handle
	 * <li>the native code, upon initialization, returned a valid pointer to
	 * the underlying MsaaAccessible object
	 * <li>the underlying element returned by <code>getElement()</code> (if any) is not disposed
	 * </ul>
	 *
	 * @throws InvalidComponentException
	 */
	public void checkIsValid () throws InvalidComponentException {
		boolean disposed = element() != null && isDisposed(element());
		boolean isAddressInvalid = (accRef!= 0 && (getAccessibleAddress()==0));
		if ( accRef == 0 || isAddressInvalid || disposed ){
				throw new InvalidComponentException("Invalid accessible element: hwnd=" + hwnd + ",ref=" + accRef + ",isDisposed:" + disposed); 
		}
	}

	protected boolean isDisposed (Object control) {
		boolean isDisposed = true;
		Class<?> widgetClass = ClassLoaderCache.getDefault().classForName(SWT_WIDGET_TYPENAME);
		try {
			if (control != null && widgetClass != null) {
				Method meth = widgetClass.getMethod("isDisposed", (Class[]) null);
				isDisposed = widgetClass.isAssignableFrom(control.getClass())
						&& ((Boolean) meth.invoke(control, (Object[]) null)).booleanValue();
			}
		}catch (Exception e) {
			logger.log(Level.WARNING, e.getMessage(), e);
		}
		return isDisposed;
	}

	public boolean equals (Object other) {
		boolean result = other != null && other instanceof MsaaAccessible;
		if (result) {
			MsaaAccessible accOther = (MsaaAccessible) other;
			try {
				String accName = this.getAccessibleName();
				String accRole = this.getAccessibleRole();
				String oAccName = accOther.getAccessibleName();
				String oAccRole = accOther.getAccessibleRole();
				result = !((accName == null && oAccName != null)
					|| (accRole == null && oAccRole != null));
				if (result) {
					result = 
						   this.getWindowHandle() == accOther.getWindowHandle()
						&& (computingIndexInParent || (this.indexInParent == accOther.indexInParent))
						&& this.getChildId() == accOther.getChildId()
						  && ((accName == null && oAccName == null) || accName.equals(oAccName))
						  && ((accRole == null && oAccRole == null) || accRole.equals(oAccRole))
						  && this.getAccessibleLocation().equals(accOther.getAccessibleLocation());
				}
			} catch (InvalidComponentException e) {
				result = false;
			}
		}
		return result;
	}

	public int hashCode () {
		return accRef % 10000;
	}

	protected void finalize () throws Throwable {
		dispose();
		accRef = 0;
		hwnd = -1;
		MsaaAccessibilityService.internalCoUnInitialize();
	}

	public String toString () {
		String str = null;
		try {
			str = getAccessibleRole() + ":" + getAccessibleName() + "[" + getAccessibleIndexInParent() + "]";
		} catch (InvalidComponentException e) {
			str = "<Exception>";
		}
		return str;
	}

	protected void initFromHwnd (int hwnd, int childID) {
		accRef = internalInitFromHwnd(hwnd, childID);
		if (accRef != 0) {
			this.hwnd = internalGetWindowHandle();
			this.childId = internalGetChildId();
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

	protected native int internalGetWindowHandle ();
	protected native int internalGetChildId ();

	protected native static int internalInitFromHwnd (int hwnd, int childID);
	protected native static int internalInitFromHtmlElement (int htmlElemRef);
	protected native static int internalInitFromPoint (int x, int y);

	/**
	 * dispose the native resources
	 * 
	 */
	protected void dispose () throws InvalidComponentException {
		checkIsValid();
		internalDispose();
	}
	protected native void internalDispose ();

	public String getWindowClass() {
		return MsaaAccessible.classNameFromHwnd(hwnd);
	}
	/**
	 * gets the class name for the given handle
	 * 
	 * @param hwnd -
	 *            window handle
	 * @return name of class
	 */
	public static String classNameFromHwnd (int hwnd) {
		return internalGetClassNameFromHwnd(hwnd);
	}
	protected native static String internalGetClassNameFromHwnd (int hwnd);

	/** {@inheritDoc} */
	public IAccessibleElement getAccessibleParent () throws InvalidComponentException {
		checkIsValid();
		IAccessibleElement parent = null;
		int accRef = internalGetAccessibleParent();
		if (accRef != 0) {
			parent = new MsaaAccessible(accRef);
		}
		if (parent != null) {
			//see if the parent is Ia2 and, if so, convert it
			try {
				parent = testAndConvertToIA2((MsaaAccessible) parent);
			} catch (Exception e) {
			}
		}
			
		return parent;
	}
	protected native int internalGetAccessibleParent ();

	/**
	 * return the number of children. Note that children include either
	 * MsaaAccessible objects that represent MsaaAccessible wrappers of IAccessible
	 * pointers or MsaaAccessible objects that wrap "simple" children, children
	 * that share their properties with their IAccessible parent.
	 * 
	 * @return number of child MsaaAccessible objects
	 * @throws InvalidComponentException
	 */
	public int getAccessibleChildCount () throws InvalidComponentException {
		int res = 0;
		checkIsValid();
		res = internalGetAccessibleChildCount();
		return res;
	}
	protected native int internalGetAccessibleChildCount ();

	/**
	 * return the accessible that has the given index in its parent. Note that this is not the 
	 * same value as its childID, though children are typically numbered consecutively 
	 * starting with 0.
	 * 
	 * @param index index of desired child in parent
	 * @return child with the given index or <code>null</code> if 
	 * the index is invalid
	 */
	public IAccessibleElement getAccessibleChild (int index) throws InvalidComponentException {
		checkIsValid();
		if(getAccessibleChildCount()==0){
			return null;
		}
		int accRef = internalGetAccessibleChild(index);
		MsaaAccessible acc = null;
		if (accRef != 0) {
			acc = new MsaaAccessible(accRef);
		}
		
		if (acc != null) {
			try {
				acc = testAndConvertToIA2(acc);
			} catch (Exception e) {
			}
			acc.indexInParent = index;
		}

		return acc;
	}
	protected native int internalGetAccessibleChild (int childID);

	/** {@inheritDoc} */
	public int getAccessibleIndexInParent () throws InvalidComponentException {
		checkIsValid();
		if (indexInParent == -1) {
			IAccessibleElement parent = getAccessibleParent();
			String accName = parent != null ? parent.getAccessibleName() : "";
			IAccessibleElement[] elems = parent != null && (accName == null || !accName.equalsIgnoreCase("desktop"))
				? parent.getAccessibleChildren() : new IAccessibleElement[0];
			
				// MAS: when computing index in parent, the equals method should not refer to indecies for
				// testing equality nor can we call this method from equals method else we have
				// an infinite loop; this switch is the solution
				computingIndexInParent = true;
				for (int c = 0; indexInParent == -1 && elems!=null && c < elems.length; ++c) {
				if (this.equals(elems[c])) {
					indexInParent = c;
				}
			}
				computingIndexInParent = false;
		}
		return indexInParent;
	}
	
	/**
	 * return all of the children of this MsaaAccessible. Note that children
	 * include either MsaaAccessible objects that represent MsaaAccessible wrappers of
	 * IAccessible pointers or MsaaAccessible objects that wrap "simple" children,
	 * children that share their properties with their IAccessible parent.
	 * 
	 * @return children of this MsaaAccessible
	 * @throws InvalidComponentException
	 */
	public IAccessibleElement[]  getAccessibleChildren () throws InvalidComponentException {
		checkIsValid();
		ArrayList<IAccessibleElement> children = new ArrayList<IAccessibleElement>();
		int childCount = getAccessibleChildCount();
		if(childCount > MAX_CHILDREN){
			return null;
		}
		if (childCount > 0) {
			int[] childRefs = internalGetAccessibleChildren();
			if (childRefs != null && childRefs.length > 0) {
				for (int i = 0; i < childRefs.length; i++) {
					if (childRefs[i] != 0) {
						MsaaAccessible acc = new MsaaAccessible(childRefs[i]);
						if (acc != null) {
							try {
								acc = testAndConvertToIA2(acc);
							} catch (Exception e) {
							}
							acc.indexInParent = i;
						}
						children.add(acc);
					}
				}
				return (IAccessibleElement[]) children.toArray(new IAccessibleElement[children.size()]);
			}
		}
		
		return null;
	}
	protected native int[] internalGetAccessibleChildren ();
	
	/**
	 * returns whether or not this MsaaAccessible is a simple child. Simple
	 * children obtain their properties from their parent IAccessible object.
	 * The parent IAccessible object has a child ID of
	 * <code>ACC.CHILDID_SELF</code>.
	 * 
	 * @return <code>true</code> if this is a simple child, <code>false</code> otherwise
	 */
	protected boolean isSimpleChild () {
		return childId != childId_self;
	}

	/**
	 * returns whether the accessible object has the keyboard focus
	 * 
	 * @return whether or not this object has keyboard focus
	 * @throws InvalidComponentException
	 */
	public boolean hasFocus () throws InvalidComponentException {
		boolean res = false;
		checkIsValid();
		res = internalHasFocus();
		return res;
	}
	protected native boolean internalHasFocus ();

	/** {@inheritDoc} */
	public String getAccessibleName () throws InvalidComponentException {
		String res = null;
		checkIsValid();
		res = internalGetAccessibleName();
		return res;
	}
	protected native String internalGetAccessibleName ();

	/** {@inheritDoc} */
	public Object getAccessibleValue () throws InvalidComponentException {
		String res = null;
		checkIsValid();
		res = internalGetAccessibleValue();
		return res;
	}
	protected native String internalGetAccessibleValue ();

	/**
	 * return help info (usually a tool tip)
	 * 
	 * @return help or "" if no help is provided
	 * @throws InvalidComponentException
	 */
	public String getAccessibleHelp () throws InvalidComponentException {
		String res = null;
		checkIsValid();
		res = internalGetAccessibleHelp();
		return res;
	}
	protected native String internalGetAccessibleHelp ();

	/** {@inheritDoc} */
	public String getAccessibleKeyboardShortcut ()
		throws InvalidComponentException {
		String res = null;
		checkIsValid();
		res = internalGetAccessibleKeyboardShortcut();
		return res;
	}
	protected native String internalGetAccessibleKeyboardShortcut ();

	/** {@inheritDoc} */
	public Object getAccessibleAction () throws InvalidComponentException {
		return getAccessibleDefaultAction();
	}
	
	private Object getAccessibleDefaultAction () throws InvalidComponentException {
		String res = null;
		checkIsValid();
		res = internalGetAccessibleAction();
		return res;
	}
	protected native String internalGetAccessibleAction ();

	/** {@inheritDoc} */
	public String getAccessibleDescription () throws InvalidComponentException {
		String res = null;
		checkIsValid();
		res = internalGetAccessibleDescription();
		return res;
	}
	protected native String internalGetAccessibleDescription ();

	/** {@inheritDoc} */
	public String getAccessibleRole () throws InvalidComponentException {
		checkIsValid();
		String res = null;
		int role = internalGetAccessibleRoleAsInt();
		
		res = Msaa.getMsaaA11yRoleName(role);
		if (res == null || res.length() < 1) {
			try {
				res = (String) Class.forName("org.a11y.utils.accprobe.accservice.core.win32.ia2.IA2")
					.getMethod("getIA2A11yRoleName", new Class[] {long.class})
						.invoke(null, new Object[] {new Integer(role)});
			}catch (Exception e) {
			}
		}
		if(res==null || res.length() <1){
			res = internalGetAccessibleRole();
		}
		return res;
	}
	
	public String getAccessibleMsaaRole () throws InvalidComponentException {
		checkIsValid();
		String res = null;
		int role = internalGetAccessibleRoleAsInt();
		
		res = Msaa.getMsaaA11yRoleName(role);
		if(res==null || res.length() <1){
			res = internalGetAccessibleRole();
		}
		return res;
	}
	protected native int internalGetAccessibleRoleAsInt ();
	protected native String internalGetAccessibleRole();

	/** {@inheritDoc} */
	public Set<String> getAccessibleMsaaState () throws InvalidComponentException {
		checkIsValid();
		int state = internalGetAccessibleState();
		return Msaa.getState(state);
	}
	protected native int internalGetAccessibleState ();

	/**
	 * get the list of selected accessibles. Currently this method will only
	 * return a single selected item, although MSAA supports multiple
	 * selection in some controls. If a control supports multiple selection and
	 * multiple selections are present, this method will return an empty array.
	 * 
	 * @return selections or empty array if no selection
	 * @throws InvalidComponentException
	 */
	public IAccessibleElement[] getAccessibleSelection () throws InvalidComponentException {
		// TODO support multiple selections in dll
		checkIsValid();
		int[] accRefs = internalGetAccessibleSelection();
		IAccessibleElement[] selections = new IAccessibleElement[accRefs != null ? accRefs.length : 0];
		if ((accRefs != null) && (accRefs.length > 0)) {
			for (int i = 0; i < selections.length; i++) {
				selections[i] = new MsaaAccessible(accRefs[i]);
			}
		}
		return selections;
	}
	protected native int[] internalGetAccessibleSelection ();

	/** {@inheritDoc} */
	public Rectangle getAccessibleLocation() throws InvalidComponentException {
		Rectangle pt = new Rectangle();
		checkIsValid();
		pt = internalGetAccessibleLocation();
		return pt;
	}
	protected native Rectangle internalGetAccessibleLocation ();

	public boolean drawRectangle(Rectangle pt) {
		//Rectangle pt = internalGetAccessibleLocation();
		return highLightEnabled  && internalDrawRectangle(pt.x, pt.y, pt.width, pt.height);
	}
	protected native boolean internalDrawRectangle(int x, int y, int wt, int ht);

	public boolean eraseRectangle(Rectangle drawRef) {
		if (drawRef == null) {
			return eraseDesktop();
		}
		int left = drawRef.x;
		int top = drawRef.y;
		int right = drawRef.x + drawRef.width;
		int bottom = drawRef.y + drawRef.height;
		return internalEraseRectangle(left, top, right, bottom);
	}

	public static boolean eraseDesktop() {
		return internalEraseDesktop();
	}
	
	protected native static boolean internalEraseDesktop();
	protected native boolean internalEraseRectangle(int left, int top, int right, int bottom);
	
	public static boolean isHighlightEnabled(){
		return highLightEnabled;
	}
	
	public static void setHighlightEnabled(boolean val){
		highLightEnabled = val;
	}
	
	public MsaaAccessible testAndConvertToIA2 (MsaaAccessible acc) throws Exception {
		boolean isIA2 = false;
		MsaaAccessible result = acc;
		isIA2 = ((Boolean) Class.forName("org.a11y.utils.accprobe.accservice.core.win32.ia2.IA2Accessible")
			.getMethod("isIA2Accessible", new Class[] {IAccessibleElement.class})
				.invoke(null, new Object[] {acc})).booleanValue();
		if (isIA2){
//			if(acc.hwnd!=0) {
//			result = (MsaaAccessible) Class.forName("org.a11y.utils.accprobe.accservice.core.win32.ia2.IA2Accessible")
//				.getConstructor(new Class[] {int.class, int.class})
//					.newInstance(new Object[] {new Integer(acc.hwnd), new Integer(acc.childId)});
//			}

				int ia2Acc =0;
				ia2Acc = ((Integer) Class.forName("org.a11y.utils.accprobe.accservice.core.win32.ia2.IA2Accessible")
						.getMethod("IA2FromIAcc", new Class[] {IAccessibleElement.class})
						.invoke(null, new Object[] {acc})).intValue();
				result = (MsaaAccessible) Class.forName("org.a11y.utils.accprobe.accservice.core.win32.ia2.IA2Accessible")
				.getConstructor(new Class[] {int.class})
					.newInstance(new Object[]{new Integer(ia2Acc)});
		}
		return result;
	}
	
	public String getAccessibleHelpTopic () throws InvalidComponentException {
		String res = null;
		checkIsValid();
		res = internalGetAccessibleHelpTopic();
		return res;
	}
	protected native String internalGetAccessibleHelpTopic ();

	protected void putErrorCode(String key, String value){
		if(errorCodeReturnMap!=null){
			if(!errorCodeReturnMap.containsKey(key))
				errorCodeReturnMap.put(key, errString.concat(value) );
			}
	}
	public Map<String, String> errorCodeMap(){
		return errorCodeReturnMap;
	}
	
	public String getAccessibleHowFound(){
		return howFound;		
	}
	
	public void setHowFound(String howFound) {
		this.howFound = howFound;
	}

	public static Map<String, String> propertyMap(){
		Map<String, String> pMap = new HashMap<String, String>();
//		pMap.put("accessibleChild", "accChild");
		pMap.put("accessibleHowFound", "howFound");
		pMap.put("accessibleChildCount", "accChildCount");
		pMap.put("accessibleChildren", "accChildren");
		pMap.put("accessibleAction", "accDefaultAction");
		pMap.put("accessibleDescription", "accDescription");
		pMap.put("hasFocus", "accFocus");
		pMap.put("accessibleHelp", "accHelp");
		pMap.put("accessibleHelpTopic", "accHelpTopic");
		pMap.put("accessibleKeyboardShortcut", "accKeyboardShortcut");
		pMap.put("accessibleName", "accName");
		pMap.put("accessibleParent", "accParent");
		pMap.put("accessibleMsaaRole", "accRole");
		pMap.put("accessibleSelection", "accSelection");
		pMap.put("accessibleMsaaState", "accState");
		pMap.put("accessibleValue", "accValue");
		pMap.put("accChild","accChild");
		pMap.put("accSelect", "accSelect");
		pMap.put("accDoDefaultAction","accDoDefaultAction");
		pMap.put("accLocation","accLocation");
		pMap.put("put_accValue","put_accValue");
				
		return pMap;
	}

	public int getPid() {
		MsaaWindowService windowService;
		try {
			windowService = (MsaaWindowService) AccessibilityServiceManager.getInstance().getAccessibilityService(MsaaAccessibilityService.MSAA_ACCSERVICE_NAME).getWindowService();
			return windowService.getProcessId(hwnd);
		} catch (AccessibilityServiceException e) {
			e.printStackTrace();
		}
		
		return 0;
		
	}

	public Set<String> getAccessibleState() throws InvalidComponentException {
		return getAccessibleMsaaState ();
	}
	
	public boolean put_accValue(int childId, String value){
		return internalPutValue( childId, value);
	}
	protected native boolean internalPutValue(int childId, String value);

		
	public boolean accDoDefaultAction(int childId) throws InvalidComponentException{
		checkIsValid();
		return internalDoDefaultAction(childId);
	}
	protected native boolean internalDoDefaultAction(int childId);
	
	public boolean accSelect(int flag, int childId) throws InvalidComponentException{
		checkIsValid();
		return internalSelect(flag, childId);
	}
	protected native boolean internalSelect(int flag, int childId);

	public Rectangle accLocation(int childId) throws InvalidComponentException{
			if(childId == getChildId()){
			    return getAccessibleLocation();
			}
			else{
				return internalLocation(childId);
			}
	}
	protected native Rectangle internalLocation(int childId);
	
	public IAccessibleElement accChild (int index) throws InvalidComponentException {
	 return getAccessibleChild(index);
	}
}