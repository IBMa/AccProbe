/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/ 


package org.a11y.utils.accprobe.accservice.core.win32.msaa;

import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.a11y.utils.accprobe.accservice.IWindowService;
import org.a11y.utils.accprobe.accservice.event.TopLevelWindowEvent;
import org.a11y.utils.accprobe.accservice.event.TopLevelWindowEventListener;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;


/**
 * implementation of a window service for the Windows operating system
*
 * @author <a href="mailto:masquill@us.ibm.com>Mike Squillace</a>
 *
 */
public class MsaaWindowService implements IWindowService
{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -131504994255159827L;
	//windows hook codes
	public static final int WH_CALLWNDPROC = 4;
	public static final int WH_CBT = 5;
	public static final int HWND_TOP = 0 ;
	public static final int  HWND_BOTTOM =1;
	public static final int  HWND_TOPMOST =-1;
	public static final int HWND_NOTOPMOST =-2;

	private List listenerList = new LinkedList();


	private int activeWindow=0;
	private boolean alwaysonTop= false;
	
	public void setAlwaysonTop(boolean alwaysonTop) {
		this.alwaysonTop = alwaysonTop;
	}

	public boolean isAlwaysonTop() {
		return alwaysonTop;
	}

	/** {@inheritDoc} */
	public Object[] getTopLevelWindows() {
		int[] hwnds = internalGetWindowsList();
		if (hwnds == null) {
			hwnds = new int[0];
		}
		Object[] objList = new Object [hwnds.length];
		for (int i=0; i< hwnds.length; i++) {			
			objList[i]=  new Integer( hwnds[i]);
		}
		
		return objList;
	}

	protected native static int[] internalGetWindowsList ();
	
	public void setActiveWindow (Object window) {
		if (window instanceof Integer) {
			activeWindow = ((Integer) window).intValue();
		}
	}
	
	public Object getActiveWindow () {
		return new Integer(activeWindow);
	}
	
	protected native int internalGetActiveWindow ();

	/** {@inheritDoc} */
	public int getProcessId (Object window) {
		int pid = -1;
		if (window instanceof Integer) {
			if(((Integer) window).intValue()==0){
				pid =0;
			}else{
				pid = internalGetProcessId(((Integer) window).intValue());
			}
		}
		return pid;
	}

	protected native int internalGetProcessId (int hwnd);
	
	public int getCurrentProcessId(){
		return internalGetCurrentProcessId();
	}

	protected native int internalGetCurrentProcessId();
	/**
	 * set a windows hook for monitoring window-related events
	 * 
	 * @param hwndHookParam - one of the pre-defined constants in this class
	 * @return hook reference for unregistering listener or -1 if set was unsuccessful
	 */
	public int setWindowsHook (int hwndHookParam) {
		int hook = -1;
		hook = internalSetWindowsHook(hwndHookParam);
		return hook;
	}

	protected native int internalSetWindowsHook (int idHook);

	/**
	 * remove a previously registered window hook
	 * 
	 * @param hook - hook reference obtained from <code>setWindowsHook</code>
	 * @return <code>true</code> if hook successfully removed, <code>false</code> otherwise
	 * @see #setWindowsHook(int)
	 */
	public boolean removeWindowsHook (int hook) {
		return internalRemoveWindowsHook(hook);
	}

	protected native boolean internalRemoveWindowsHook (int hook);

	/**
	 * not to be used by clients; made public so that native code can reference
	 * 
	 * @param hwnd
	 * @throws InvalidComponentException
	 */
	protected void windowCallback (int hwnd) throws InvalidComponentException {
		MsaaAccessible ca = new MsaaAccessible(hwnd, MsaaAccessible.childId_self);
		fireTopLevelWindowEvent(ca);
	}

	/**
	 * remove a previously added top-level window listener
	 * 
	 * @param listener
	 */
	public void removeTopLevelWindowListener (TopLevelWindowEventListener listener) {
		listenerList.remove(listener);
	}

	protected void fireTopLevelWindowEvent (MsaaAccessible ca) {
		TopLevelWindowEvent evt = new TopLevelWindowEvent(ca);
		for (Iterator iter = listenerList.iterator(); iter.hasNext();) {
			((TopLevelWindowEventListener) iter.next()).WindowEventCreated(evt);
		}
	}

	/**
	 * add a listener to be notified of creation and destruction of top-level native windows
	 * 
	 * @param listener
	 */
	public void addTopLevelWindowListener (TopLevelWindowEventListener listener) {
		if (listener != null) {
			listenerList.add(listener);
		}
	}


	public static String getWindowClass(int hwnd) {
		return internalGetWindowClass(hwnd);
	}

	public void setWindowOnTop(int hwnd) {
		internalSetWindowPosition(hwnd, HWND_TOPMOST);
		setAlwaysonTop(true);
	}
	
	public void setWindowNoTop(int hwnd){
		internalSetWindowPosition(hwnd, HWND_NOTOPMOST);
		setAlwaysonTop(false);
	}
	
	public void showWindow(int hwnd) {
		 internalShowWindow(hwnd);	
		 internalSetWindowPosition(hwnd, HWND_TOP);
	}

	public static Point getCurrentCursorLocation() {
		return  internalGetCurrentCursorLocation();
	}
	protected native static Point internalGetCurrentCursorLocation ();
	
	protected  native static void internalShowWindow(int hwnd);

	protected native static void internalSetWindowPosition(int hwnd, int pos);

	public static final native int setLayeredWindowAttributes(int hwnd, int crKey, char bAlpha, int dwFlags);

	public static native int getSystemMetricsWidth();
	
	public static native int getSystemMetricsHeight();
	
	public static native String internalGetWindowClass(int hwnd);


}
