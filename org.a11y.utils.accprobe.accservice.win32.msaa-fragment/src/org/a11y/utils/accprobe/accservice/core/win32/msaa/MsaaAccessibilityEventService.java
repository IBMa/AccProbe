/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 
package org.a11y.utils.accprobe.accservice.core.win32.msaa;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.a11y.utils.accprobe.accservice.AccessibilityServiceException;
import org.a11y.utils.accprobe.accservice.AccessibilityServiceManager;
import org.a11y.utils.accprobe.accservice.IAccessibilityEventService;
import org.a11y.utils.accprobe.accservice.IWindowService;
import org.a11y.utils.accprobe.accservice.core.IAccessibleElement;
import org.a11y.utils.accprobe.accservice.event.AccessibilityModelEvent;
import org.a11y.utils.accprobe.core.model.events.IModelEventListener;
import org.a11y.utils.accprobe.core.model.events.ModelEventType;


public class MsaaAccessibilityEventService implements IAccessibilityEventService
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int EVENT_SYSTEM_ALERT  = 0x0002;
	public static final int EVENT_SYSTEM_FOREGROUND  = 0x0003;
	public static final int EVENT_SYSTEM_MENUSTART = 0x0004;
	public static final int EVENT_SYSTEM_MENUEND = 0x0005;
	public static final int  EVENT_SYSTEM_SOUND           =   0x0001;
	public static final int  EVENT_SYSTEM_CAPTURESTART    =  0x0008;
	public static final int  EVENT_SYSTEM_CAPTUREEND       =  0x0009;
	public static final int  EVENT_SYSTEM_MOVESIZESTART    =  0x000A;
	public static final int  EVENT_SYSTEM_MOVESIZEEND      =  0x000B;
	public static final int  EVENT_SYSTEM_CONTEXTHELPSTART =  0x000C;
	public static final int  EVENT_SYSTEM_CONTEXTHELPEND   =  0x000D;
	public static final int  EVENT_SYSTEM_DRAGDROPSTART    =  0x000E;
	public static final int  EVENT_SYSTEM_DRAGDROPEND      =  0x000F;
	public static final int  EVENT_SYSTEM_DIALOGSTART      =  0x0010;
	public static final int  EVENT_SYSTEM_DIALOGEND        =  0x0011;
	public static final int  EVENT_SYSTEM_SCROLLINGSTART   =  0x0012;
	public static final int  EVENT_SYSTEM_SCROLLINGEND     =  0x0013;
	public static final int  EVENT_SYSTEM_SWITCHSTART      =  0x0014;
	public static final int  EVENT_SYSTEM_SWITCHEND        =  0x0015;
	public static final int  EVENT_SYSTEM_MINIMIZESTART    =  0x0016;
	public static final int  EVENT_SYSTEM_MINIMIZEEND      =  0x0017;
	public static final int EVENT_SYSTEM_MENUPOPUPSTART =0x0006;
	public static final int EVENT_SYSTEM_MENUPOPUPEND =0x0007;
	public static final int EVENT_OBJECT_CREATE = 0x8000; // hwnd + ID + idChild is created item
	public static final int EVENT_OBJECT_DESTROY = 0x8001; // hwnd + ID + idChild is destroyed item
	public static final int EVENT_OBJECT_SHOW = 0x8002; // hwnd + ID + idChild is shown item
	public static final int EVENT_OBJECT_HIDE = 0x8003; // hwnd + ID + idChild is hidden item
	public static final int EVENT_OBJECT_REORDER = 0x8004;
	public static final int EVENT_OBJECT_FOCUS = 0x8005; // hwnd + ID + idChild is focused item
	public static final int EVENT_OBJECT_SELECTION = 0x8006; // hwnd + ID + idChild is selected item (if only one), or idChild is OBJID_WINDOW if complex
	public static final int EVENT_OBJECT_SELECTIONADD = 0x8007;// hwnd + ID + idChild is item added
	public static final int EVENT_OBJECT_SELECTIONREMOVE = 0x8008;// hwnd + ID + idChild is item removed
	public static final int EVENT_OBJECT_SELECTIONWITHIN = 0x8009;
	public static final int EVENT_OBJECT_STATECHANGE = 0x800A;
	public static final int EVENT_OBJECT_LOCATIONCHANGE = 0x800B;
	public static final int EVENT_OBJECT_NAMECHANGE = 0x800C; // hwnd + ID + idChild is item w/ name change
	public static final int EVENT_OBJECT_DESCRIPTIONCHANGE = 0x800D; // hwnd + ID + idChild is item w/ desc change
	public static final int EVENT_OBJECT_VALUECHANGE = 0x800E; // hwnd + ID + idChild is item w/ value change
	public static final int EVENT_OBJECT_PARENTCHANGE = 0x800F; // hwnd + ID + idChild is item w/ new parent
	public static final int EVENT_OBJECT_HELPCHANGE = 0x8010; // hwnd + ID + idChild is item w/ help change
	public static final int EVENT_OBJECT_DEFACTIONCHANGE = 0x8011; // hwnd + ID + idChild is item w/ def action change
	public static final int EVENT_OBJECT_ACCELERATORCHANGE = 0x8012;

	public static final int WINEVENT_OUTOFCONTEXT   = 0x0000 ; // Events are ASYNC
	public static final int WINEVENT_SKIPOWNTHREAD  = 0x0001 ; // Don't call back for events on installer's thread
	public static final int WINEVENT_SKIPOWNPROCESS = 0x0002 ;  // Don't call back for events on installer's process
	public static final int WINEVENT_INCONTEXT      = 0x0004 ; // Events are SYNC, this causes your dll to be injected into every process

	/** default flag if none specified: <code>WINEVENT_OUTOFCONTEXT | WINEVENT_SKIPOWNPROCESS</code> */
	public static final int DEFAULT_CONTEXT_FLAG = WINEVENT_OUTOFCONTEXT | WINEVENT_SKIPOWNPROCESS;

	//Memory Map
	public static final String fileMappingObjName = "Event_data_Mem_Map_File";
	public static final int DWMEM_FILESIZE = 2 * 1024;
	public static final int PAGE_READONLY = 0x02;
	public static final int PAGE_READWRITE = 0x04;
	public static final int PAGE_WRITECOPY = 0x08;

	public static final int FILE_MAP_COPY = 0x0001;
	public static final int FILE_MAP_WRITE = 0x0002;
	public static final int FILE_MAP_READ = 0x0004;

	protected static long timeDiff=0;
	protected static long initClockTicks =0;
	protected static int mapFilePtr;
	protected static boolean isMapFileOpen = false;
	protected static Map<WinEventData, List<IModelEventListener>> listenerMap
		= Collections.synchronizedMap(new HashMap<WinEventData, List<IModelEventListener>>());
	
	protected IWindowService windowService;
	protected int inContextListenerCount = 0;
	
	private static class WinEventData {
		private int eventId;
		private int processId;
		private boolean inContext;
		private int eventHook;
		
		public WinEventData (int eventId, int processId, boolean inContext) {
			this.eventId = eventId;
			this.processId = processId;
			this.inContext = inContext;
		}
		
		public boolean equals (Object other) {
			boolean result = false;
			if (other instanceof WinEventData) {
				WinEventData data = (WinEventData) other;
				result = this.eventId == data.eventId
					&& this.processId == data.processId
					&& (this.inContext == data.inContext);
			}
			return result;
		}
		
		public int hashCode () {
			return eventId;
		}
		
		public int getEventId () {
			return this.eventId;
		}

		public int getProcessId () {
			return this.processId;
		}

		public boolean isInContext () {
			return this.inContext;
		}

		public int getEventHook () {
			return this.eventHook;
		}
		
		public void setEventHook (int eventHook) {
			this.eventHook = eventHook;
		}

	}
	
	public static native int createFileMapping(int lProtect, int dwMaximumSizeHigh, int dwMaximumSizeLow, String name);
	public static native int openFileMapping(int dwDesiredAccess, boolean bInheritHandle, String name);
	public static native int mapViewOfFile(int hFileMappingObj, int dwDesiredAccess, int dwFileOffsetHigh, int dwFileOffsetLow, int dwNumberOfBytesToMap);
	public static native boolean unmapViewOfFile(int lpBaseAddress);
	public static native void writeToMem(int lpBaseAddress, int content);
	public static native String[] readFromMem(int lpBaseAddress);
	public static native boolean closeHandle(int hObject);
	public native boolean initThread();

	public MsaaAccessibilityEventService (IWindowService windowService) {
		this.windowService = windowService;
	}

	public static long  getClockTicks(){
		return internalGetClockTicks();
	}
	protected native static long internalGetClockTicks();
	
	/**
	 * MSAA SetWinEventHook function wrapper
	 * 
	 * @param eventMin - minimum event type for hook 
	 * (use one of the pre-defined constants in this class)
	 * @param eventMax - maximum event value for hook
	 * (use one of the pre-defined constants in this class)
	 * @param idProcess - usually to be obtained from getProcessId(int)
	 * @param idThread - use id of desired thread or 0 for all threads
	 * @return hook reference to be used with unregisterWinEventHook or -1 if set was not successful
	 * @see #removeWinEventHook(int)
	 */
	protected int setWinEventHook (int eventMin, int eventMax, int idProcess, int idThread, int flag) {
		int hook = -1;
		hook = internalSetWinEventHook(eventMin, eventMax, idProcess, idThread, flag);
		return hook;
	}
	protected native int internalSetWinEventHook (int eventMin, int eventMax, int idThread, int idProcess, int  dwFlags);

	/**
	 * remove a previously registered winEventHook
	 * 
	 * @param hook - hook reference obtained from <code>setWinEventHook</code>
	 * @return <code>true</code> if hook successfully removed, <code>false</code> otherwise
	 * @see #setWinEventHook(int, int, int, int)
	 */
	protected boolean removeWinEventHook (int hook) {
		return internalRemoveWinEventHook(hook);
	}

	protected native boolean internalRemoveWinEventHook (int hook);

	/**
	 * callback from the native Windows system for out-of-process Msaa events. The parameters are:
	 * 
	 * <p><ul>
	 * <li>event information (hwnd, child Id, Object ID, event ID)to form a new <code>MsaaAccessible</code>. 
	 * In this case, the source of the resulting event will be this <code>MsaaAccessible</code> object.
	 * </ul></p>
	 * 
	 * <p>The appropriate listeners for the given event id are notified and the <code>AccesibilityModelEvent</code> is 
	 * created from the formed <code>MsaaAccessible</code> </p>
	 * 
	 * @param eventId
	 * @param hwnd
	 * @param idObject
	 * @param idChild
	 * @param time
	 */
	protected static void winEventCallback(int eventId, int hwnd, 
			int idObject, int idChild, long threadId, long time, int isGlobal) {

		if(initClockTicks > time){
			setTimeDiff();
		}
		long dms =System.currentTimeMillis();
		long ctime = time+ timeDiff;
		AccessibilityModelEvent accEvent = null;	
		MsaaAccessible macc = null;
		int ref = createAccessibleObjectFromEvent(hwnd,idObject,idChild);
		if (ref!=0) {
				macc = new MsaaAccessible(ref);
		}
		if (macc != null) {
			IAccessibleElement acc = macc;
			try {
				acc = macc.testAndConvertToIA2(macc);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			accEvent = new AccessibilityModelEvent(acc);
			accEvent.setEventType(eventName(eventId));
			accEvent.setTimeMillis(ctime);
			String windowClass = MsaaWindowService.getWindowClass(hwnd);
			String miscData= "hwnd="+ Integer.toHexString(hwnd).toUpperCase()+
							 " ;objectId="+idObject+
							 "; childId="+ idChild+
							 "; threadId="+ threadId +
							 "; windowClass=" + windowClass+ "; ";
			accEvent.setMiscData(miscData);
			
			try {
				IWindowService winService = AccessibilityServiceManager.getInstance()
					.getAccessibilityService(MsaaAccessibilityService.MSAA_ACCSERVICE_NAME).getWindowService();
				int pid;
				if(isGlobal==0){
				 pid = hwnd!=0 ? winService.getProcessId(hwnd): winService.getProcessId(winService.getActiveWindow());
				}else{
					pid =0;
				}
				fireAccessibilityModelEvent(accEvent, eventId, pid , false);
			} catch (AccessibilityServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected static native int createAccessibleObjectFromEvent(int hwnd, int idObject, int idChild);

	
	/**
	 * callback from the native Windows system for in-process Msaa events. 
	 * The event information is read from a memory map file as a string array.
	 *  
	 * <p>The appropriate listeners for the given event id are notified and the <code>AccesibilityModelEvent</code> is  
	 * created. Here, the source of the AccesibilityModelEvent is a string array containing information about the 
	 * accessible that fired the event</p>
	 */
	protected void winEventIPCallback () {
		int mPtr = openFileMapping(FILE_MAP_READ|FILE_MAP_WRITE, false, fileMappingObjName);
		int state =0;
		int eventId =0;
		long timeMillis =0;
		String[] source= null;
		
		if(mPtr != 0) {
			int vPtr = mapViewOfFile(mPtr,
					FILE_MAP_READ|FILE_MAP_WRITE, 0, 0, 0);
			if(vPtr != 0) {
				String props[] = readFromMem(vPtr);
				if(props!=null && props.length==5){
					String res =null;
					int role = 0;
					try {
						role = Integer.parseInt(props[1]);
						res = Msaa.getMsaaA11yRoleName(role);
						if (res == null || res.length() < 1) {
						try {
							res = (String) Class.forName("org.a11y.utils.accprobe.accservice.core.win32.ia2.IA2")
								.getMethod("getIA2A11yRoleName", new Class<?>[] {long.class})
									.invoke(null, new Object[] {new Integer(role)});
						}catch (Exception e) {
						}
						}
					} catch (NumberFormatException e1) {
						//role is returned as a  string 
						res= props[1];
					}
					state= Integer.parseInt(props[2]);
					eventId = Integer.parseInt(props[3]);
					timeMillis = Long.parseLong(props[4]);
					if(initClockTicks > timeMillis){
							setTimeDiff();
					}
					timeMillis = timeMillis+timeDiff;
					 
					source = new String[5];
					source[0]=props[0];
					source[1]= res;
					source[2]= Msaa.getState(state).toString();
					source[3]= eventName(eventId);
					source[4]= Long.valueOf(timeMillis).toString();
				}
				
				unmapViewOfFile(vPtr);
			}
			
			closeHandle(mPtr);
		}
		
		AccessibilityModelEvent accEvent = null;
		if (source!=null){
			try {
				accEvent = new AccessibilityModelEvent(source);		
				fireAccessibilityModelEvent(accEvent, eventId, windowService.getProcessId(windowService.getActiveWindow()), true);
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	protected static void fireAccessibilityModelEvent (AccessibilityModelEvent accEvent, int eventId,
												int processId, boolean inContext) {
		WinEventData eventData = new WinEventData(eventId, processId, inContext);
		List<IModelEventListener> listeners = listenerMap.get(eventData);
		if (listeners != null) {
			IModelEventListener[] listenera = listeners.toArray(new IModelEventListener[listeners.size()]);
			for (IModelEventListener listener : listenera) {
				listener.handleEvent(accEvent);					
			}
		}
	}

	/**
	 * add the listener to be notified when events of the given type are fired from the underlying system. The optional 
	 * parameters array, if specified, may have only one element that is of type <code>Integer</code> and that 
	 * represents a bit-wise oring of the flag constants defined in this class.
	 * 
	 * @param listener
	 * @param eventTypes to be chosen from constants in this class
	 * @param params flag parameters taken from symbolic constants in this class
	 * @see org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaGuiModel#registerModelEventListener(IModelEventListener, ModelEventType[], Object[])
	 */
	public synchronized void addAccessibilityEventListener(IModelEventListener listener, Object[] eventTypes, Object[] params) {
		int processId = windowService.getProcessId(windowService.getActiveWindow());
		int flag = params != null && params.length == 1 && params[0] instanceof Integer
		? ((Integer) params[0]).intValue() : DEFAULT_CONTEXT_FLAG;
	boolean inContext = (flag & WINEVENT_INCONTEXT) != 0;
		
		setTimeDiff();
	if (!isMapFileOpen  && inContext) {
			mapFilePtr = createFileMapping(
					PAGE_READWRITE, 0, DWMEM_FILESIZE, fileMappingObjName);
			if(mapFilePtr!=0){
				isMapFileOpen = true;
				initThread();
			}
		}

		for (int i = 0; i < eventTypes.length; ++i) {
			int eventId = ((Integer) eventTypes[i]).intValue();
			WinEventData weData = new WinEventData(eventId, processId, inContext);
			List<IModelEventListener> listeners = listenerMap.get(weData);
			
			if (listeners == null) {
				int hook = setWinEventHook(eventId, eventId, processId, 0, flag);
				if (hook != -1) {
					listeners = new LinkedList<IModelEventListener>();
					weData.setEventHook(hook);
					listenerMap.put(weData, listeners);
				}
			}
			
			listeners.add(listener);
			if (inContext) {
				++inContextListenerCount;
			}
		}
	}

	protected static void setTimeDiff() {
		initClockTicks = getClockTicks();
		timeDiff = System.currentTimeMillis() - initClockTicks;
	}
	
	/** {@inheritDoc} */
	public synchronized void removeAccessibilityEventListener(IModelEventListener listener, Object[] eventTypes) {
		List<WinEventData> removeList = new LinkedList<WinEventData>();
		for (int i = 0; i < eventTypes.length; ++i) {
			int eventId = ((Integer) eventTypes[i]).intValue();
			for (WinEventData eventData : listenerMap.keySet()) {
				if (eventData.getEventId() == eventId) {
					List<IModelEventListener> listeners = listenerMap.get(eventData);
					if (listeners.contains(listener)) {
						listeners.remove(listener);
						if (eventData.isInContext()) {
							--inContextListenerCount;
						}
						if (listeners.isEmpty()) {
							removeList.add(eventData);
							removeWinEventHook(eventData.getEventHook());
						}
					}
				}
			}
		}
		for (WinEventData eventData : removeList) {
			listenerMap.remove(eventData);
		}
			
		if (isMapFileOpen && inContextListenerCount == 0) {
			if(closeHandle(mapFilePtr)){
				isMapFileOpen = false;
				mapFilePtr = 0;
			}
		}
		}

	public static String eventName(int event){
		String evName = null;

		MsaaGuiModel march = new MsaaGuiModel();
		ModelEventType mtype= march.getModelEventType(Integer.valueOf(event));
		if(mtype!=null){
			evName = mtype.getEventName();
		}
		return evName;
	}

}
