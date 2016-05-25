/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core.win32.ia2;

import org.a11y.utils.accprobe.accservice.core.win32.msaa.Msaa;
import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaAccessibilityEventService;
import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaAccessible;
import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaWindowService;
import org.a11y.utils.accprobe.accservice.AccessibilityServiceException;
import org.a11y.utils.accprobe.accservice.AccessibilityServiceManager;
import org.a11y.utils.accprobe.accservice.IWindowService;
import org.a11y.utils.accprobe.accservice.core.IAccessibleElement;
import org.a11y.utils.accprobe.accservice.event.AccessibilityModelEvent;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;
import org.a11y.utils.accprobe.core.model.events.ModelEventType;


public class IA2AccessibilityEventService extends MsaaAccessibilityEventService
{	
	private static final long serialVersionUID = 4370682108473838982L;
	public static final int IA2_EVENT_ACTION_CHANGED	= 0x101;
	public static final int IA2_EVENT_ACTIVE_DECENDENT_CHANGED	= IA2_EVENT_ACTION_CHANGED + 1;
	public static final int IA2_EVENT_ACTIVE_DESCENDENT_CHANGED = IA2_EVENT_ACTIVE_DECENDENT_CHANGED;
	public static final int IA2_EVENT_DOCUMENT_ATTRIBUTE_CHANGED	= IA2_EVENT_ACTIVE_DECENDENT_CHANGED + 1;
	public static final int IA2_EVENT_DOCUMENT_CONTENT_CHANGED	= IA2_EVENT_DOCUMENT_ATTRIBUTE_CHANGED + 1;
	public static final int IA2_EVENT_DOCUMENT_LOAD_COMPLETE	= IA2_EVENT_DOCUMENT_CONTENT_CHANGED + 1;
	public static final int IA2_EVENT_DOCUMENT_LOAD_STOPPED	= IA2_EVENT_DOCUMENT_LOAD_COMPLETE + 1;
	public static final int IA2_EVENT_DOCUMENT_RELOAD	= IA2_EVENT_DOCUMENT_LOAD_STOPPED + 1;
	public static final int IA2_EVENT_HYPERLINK_END_INDEX_CHANGED	= IA2_EVENT_DOCUMENT_RELOAD + 1;
	public static final int IA2_EVENT_HYPERLINK_NUMBER_OF_ANCHORS_CHANGED	= IA2_EVENT_HYPERLINK_END_INDEX_CHANGED + 1;
	public static final int IA2_EVENT_HYPERLINK_SELECTED_LINK_CHANGED	= IA2_EVENT_HYPERLINK_NUMBER_OF_ANCHORS_CHANGED + 1;
	public static final int IA2_EVENT_HYPERTEXT_LINK_ACTIVATED	= IA2_EVENT_HYPERLINK_SELECTED_LINK_CHANGED + 1;
	public static final int IA2_EVENT_HYPERTEXT_LINK_SELECTED	= IA2_EVENT_HYPERTEXT_LINK_ACTIVATED + 1;
	public static final int IA2_EVENT_HYPERLINK_START_INDEX_CHANGED	= IA2_EVENT_HYPERTEXT_LINK_SELECTED + 1;
	public static final int IA2_EVENT_HYPERTEXT_CHANGED	= IA2_EVENT_HYPERLINK_START_INDEX_CHANGED + 1;
	public static final int IA2_EVENT_HYPERTEXT_NLINKS_CHANGED	= IA2_EVENT_HYPERTEXT_CHANGED + 1;
	public static final int IA2_EVENT_OBJECT_ATTRIBUTE_CHANGED	= IA2_EVENT_HYPERTEXT_NLINKS_CHANGED + 1;
	public static final int IA2_EVENT_PAGE_CHANGED	= IA2_EVENT_OBJECT_ATTRIBUTE_CHANGED + 1;
	public static final int IA2_EVENT_ROLE_CHANGED	= IA2_EVENT_PAGE_CHANGED + 1;
	public static final int IA2_EVENT_TABLE_CAPTION_CHANGED	= IA2_EVENT_ROLE_CHANGED + 1;
	public static final int IA2_EVENT_TABLE_COLUMN_DESCRIPTION_CHANGED	= IA2_EVENT_TABLE_CAPTION_CHANGED + 1;
	public static final int IA2_EVENT_TABLE_COLUMN_HEADER_CHANGED	= IA2_EVENT_TABLE_COLUMN_DESCRIPTION_CHANGED + 1;
	public static final int IA2_EVENT_TABLE_MODEL_CHANGED	= IA2_EVENT_TABLE_COLUMN_HEADER_CHANGED + 1;
	public static final int IA2_EVENT_TABLE_ROW_DESCRIPTION_CHANGED	= IA2_EVENT_TABLE_MODEL_CHANGED + 1;
	public static final int IA2_EVENT_TABLE_ROW_HEADER_CHANGED	= IA2_EVENT_TABLE_ROW_DESCRIPTION_CHANGED + 1;
	public static final int IA2_EVENT_TABLE_SUMMARY_CHANGED	= IA2_EVENT_TABLE_ROW_HEADER_CHANGED + 1;
	public static final int IA2_EVENT_TEXT_ATTRIBUTE_CHANGED	= IA2_EVENT_TABLE_SUMMARY_CHANGED + 1;
	public static final int IA2_EVENT_TEXT_CARET_MOVED	= IA2_EVENT_TEXT_ATTRIBUTE_CHANGED + 1;
	public static final int IA2_EVENT_TEXT_CHANGED	= IA2_EVENT_TEXT_CARET_MOVED + 1;
	public static final int IA2_EVENT_TEXT_COLUMN_CHANGED	= IA2_EVENT_TEXT_CHANGED + 1;
	public static final int IA2_EVENT_TEXT_INSERTED	= IA2_EVENT_TEXT_COLUMN_CHANGED + 1;
	public static final int IA2_EVENT_TEXT_REMOVED	= IA2_EVENT_TEXT_INSERTED + 1;
	public static final int IA2_EVENT_TEXT_UPDATED	= IA2_EVENT_TEXT_REMOVED + 1;
	public static final int IA2_EVENT_TEXT_SELECTION_CHANGED	= IA2_EVENT_TEXT_UPDATED + 1;
	public static final int IA2_EVENT_VISIBLE_DATA_CHANGED	= IA2_EVENT_TEXT_SELECTION_CHANGED + 1;
	private static final int INCONTEXT_PROPS_LENGTH = 6;

	public IA2AccessibilityEventService (IWindowService windowService) {
		super(windowService);
	}
	
	/**
	 * callback from the native Windows system for out-of-process IA2 events. The parameters are:
	 * 
	 * <p><ul>
	 * <li>event information (hwnd, child Id, Object ID, event ID)to form a new <code>IA2Accessible</code>. 
	 * In this case, the source of the resulting event will be this <code>IA2Accessible</code> object.
	 * </ul></p>
	 * 
	 * <p>The appropriate listeners for the given event id are notified and the <code>AccesibilityModelEvent</code> is 
	 * created from the formed <code>IA2Accessible</code> </p>
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
		IAccessibleElement acc = null;
		int ref = createAccessibleObjectFromEvent(hwnd,idObject,idChild);
		if (ref!=0) {
			acc = new IA2Accessible(ref);
		} else {
			ref = MsaaAccessibilityEventService.createAccessibleObjectFromEvent(hwnd,idObject,idChild);
			if (ref!=0) {
				acc = new MsaaAccessible(ref);
			}
		}
		
		if (acc != null) {
			String evName = eventName(eventId);
			if(eventId == EVENT_OBJECT_FOCUS){
			 ((MsaaAccessible)acc).setHowFound("Focus Event; hwnd="+Integer.toHexString(hwnd).toUpperCase());
			}
			accEvent = new AccessibilityModelEvent(acc);
			accEvent.setEventType(evName);
			accEvent.setTimeMillis(ctime);
			String windowClass = MsaaWindowService.getWindowClass(hwnd);
			String miscData= "hwnd="+ Integer.toHexString(hwnd).toUpperCase()+
							 " ;objectId="+idObject+
							 "; childId="+ idChild+
							 "; threadId="+ threadId +
							 "; windowClass=" + windowClass+ "; ";
			accEvent.setMiscData(miscData);
			
			if(evName.startsWith("IA2_EVENT_TEXT")){
				if (acc instanceof IA2Accessible) {
					IA2Accessible ia2Acc = (IA2Accessible) acc;
					try {
						IA2AccessibleText accText = (IA2AccessibleText) ia2Acc.getAccessibleText();
						if(accText!=null){
							StringBuffer sb = new StringBuffer();
							long offset =  accText.getCaretOffset();
							if(offset>=0){
								sb.append("Caret Offset="+offset+ "; ");
							}
							IA2TextSegment oldText = accText.getOldText();
							if(oldText!=null && evName.equals("IA2_EVENT_TEXT_REMOVED")){
								sb.append("oldText="+ oldText.getText()+ "; ");
							}
							IA2TextSegment newText = accText.getNewText();	
							if(newText!=null && evName.equals("IA2_EVENT_TEXT_INSERTED")){
								sb.append("newText="+ newText.getText()+ "; ");
							}
							if(evName.equals("IA2_EVENT_TEXT_SELECTION_CHANGED")){
								String sel = accText.getSelection(0);
								String text = accText.getText();
								long start =text.indexOf(sel, 0);
								sb.append("SelStart=" + start);
								sb.append(" ;SelEnd=" + (start+sel.length()));
							}
							miscData = miscData + sb.toString();
						}
					}
					catch (InvalidComponentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 
			
			}
			accEvent.setMiscData(miscData);
			try {
				IWindowService winService = AccessibilityServiceManager.getInstance()
					.getAccessibilityService(IA2AccessibilityService.IA2_ACCSERVICE_NAME).getWindowService();
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

	public static String eventName(int event){
		String evName = null;
		IA2GuiModel iarch = new IA2GuiModel();
		ModelEventType itype= iarch.getModelEventType(Integer.valueOf(event));
		if(itype!=null){
			evName = itype.getEventName();
		}
		return evName;
	}
	
	/**
	 * callback from the native Windows system for in-process IA2 events. 
	 * The event information is read from a memory map file as a string array.
	 *  
	 * <p>The appropriate listeners for the given event id are notified and the <code>AccesibilityModelEvent</code> is  
	 * created. Here, the source of the AccesibilityModelEvent is a string array containing information about the 
	 * accessible that fired the event</p>
	 */
	protected void winEventIPCallback () {
		int mPtr = openFileMapping(FILE_MAP_READ|FILE_MAP_WRITE, false,
				fileMappingObjName);
		int state =0;
		int eventId =0;
		long timeMillis =0;
		String[] source=null;
		if(mPtr != 0) {
			int vPtr = mapViewOfFile(mPtr,
					FILE_MAP_READ|FILE_MAP_WRITE, 0, 0, 0);
			if(vPtr != 0) {
				String props[] = readFromMem(vPtr);
				if(props!=null && props.length==INCONTEXT_PROPS_LENGTH){
					String roleName =null;
					try {
						long role = Long.parseLong(props[1]);
						roleName= Msaa.getMsaaA11yRoleName(role);
						if(roleName==null){
							roleName= IA2.getIA2A11yRoleName(role);
						}
					} catch (NumberFormatException e) {
						// role returned as a string
						roleName= props[1];
					}
					state= Integer.parseInt(props[2]);
					eventId = Integer.parseInt(props[3]);
					timeMillis = Long.parseLong(props[4]);
					if(initClockTicks > timeMillis){
							setTimeDiff();
					}
					timeMillis = timeMillis+timeDiff;
					
					source = new String[INCONTEXT_PROPS_LENGTH];
					source[0]=props[0];
					source[1]=  roleName;
					source[2]= Msaa.getState(state).toString();
					source[3]= eventName(eventId);
					source[4]= Long.valueOf(timeMillis).toString();
					source[5]= props[5];
				}
				unmapViewOfFile(vPtr);
			}
			closeHandle(mPtr);
		}
		//handle data
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

	//protected native int internalSetWinEventHook (int eventMin, int eventMax, int idThread, int idProcess, int  dwFlags);
	//public native boolean initThread();
}
