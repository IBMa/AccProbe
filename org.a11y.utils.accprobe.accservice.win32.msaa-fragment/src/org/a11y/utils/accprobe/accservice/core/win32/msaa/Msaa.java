/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core.win32.msaa;

import java.util.HashSet;
import java.util.Set;

import org.a11y.utils.accprobe.accservice.core.AccessibleConstants;


/**
 * utility constants and methods for MSAA
*
 * @author Mike Smith, Kavitha Tegala, Mike Squillace
 *
 */
public final class Msaa
{
	public static enum MSAA_STATE {
		NORMAL				(0x00000000, AccessibleConstants.STATE_NORMAL),
		UNAVAILABLE 		(0x00000001, AccessibleConstants.STATE_UNAVAILABLE),
		SELECTED 			(0x00000002, AccessibleConstants.STATE_SELECTED),
		FOCUSED 			(0x00000004, AccessibleConstants.STATE_FOCUSED),
		PRESSED 			(0x00000008, AccessibleConstants.STATE_PRESSED),
		CHECKED 			(0x00000010, AccessibleConstants.STATE_CHECKED),
		MIXED 				(0x00000020, AccessibleConstants.STATE_MIXED),
		READ_ONLY 			(0x00000040, AccessibleConstants.STATE_READ_ONLY),
		HOTTRACKED 			(0x00000080, AccessibleConstants.STATE_HOT_TRACKED),
		DEFAULT 			(0x00000100, AccessibleConstants.STATE_DEFAULT),
		EXPANDED 			(0x00000200, AccessibleConstants.STATE_EXPANDED),
		COLLAPSED 			(0x00000400, AccessibleConstants.STATE_COLLAPSED),
		BUSY 				(0x00000800, AccessibleConstants.STATE_BUSY),
		FLOATING			(0x00001000, AccessibleConstants.STATE_FLOATING),
		MARQUEED			(0x00002000, AccessibleConstants.STATE_MARQUEED),
		ANIMATED			(0x00004000, AccessibleConstants.STATE_ANIMATED),
		INVISIBLE 			(0x00008000, AccessibleConstants.STATE_INVISIBLE),
		OFF_SCREEN 			(0x00010000, AccessibleConstants.STATE_OFF_SCREEN),
		SIZEABLE 			(0x00020000, AccessibleConstants.STATE_SIZEABLE),
	    MOVEABLE 			(0x00040000, AccessibleConstants.STATE_MOVEABLE),
	    SELFVOICING 		(0x00080000, AccessibleConstants.STATE_SELF_VOICING),
		FOCUSABLE 			(0x00100000, AccessibleConstants.STATE_FOCUSABLE),
		SELECTABLE 			(0x00200000, AccessibleConstants.STATE_SELECTABLE),
		LINKED 				(0x00400000, AccessibleConstants.STATE_LINKED),
		TRAVERSED  			(0x00800000, AccessibleConstants.STATE_TRAVERSED),
		MULTI_SELECTABLE	(0x01000000, AccessibleConstants.STATE_MULTI_SELECTABLE),
	    EXTSELECTABLE		(0x02000000, AccessibleConstants.STATE_EXTSELECTABLE),
	    ALERT_LOW 			(0x04000000, AccessibleConstants.STATE_ALERT_LOW),
	    ALERT_MEDIUM		(0x08000000, AccessibleConstants.STATE_ALERT_MEDIUM),
	    ALERT_HIGH 			(0x10000000, AccessibleConstants.STATE_ALERT_HIGH),
	    HASPOPUP 			(0x40000000, AccessibleConstants.STATE_HASPOPUP);
		private int mask;
		private String str;

		MSAA_STATE(int mask, String str) {
			this.mask = mask;
			this.str = str;
		}
		
		public int getMask() {
			return this.mask;
		}
		
		public String toString() {
			return this.str;
		}
	}
	
	public static enum MSAA_ROLE {
		ALERT(0x08,AccessibleConstants.ROLE_ALERT),
 	 	ANIMATION(0x36,AccessibleConstants.ROLE_ANIMATION),
 	 	APPLICATION(0x0e,AccessibleConstants.ROLE_APPLICATION),
 	 	BORDER(0x13,AccessibleConstants.ROLE_BORDER),
 	 	BUTTONDROPDOWN(0x38,AccessibleConstants.ROLE_BUTTON_DROPDOWN),
 	 	BUTTONDROPDOWNGRID(0x3a,AccessibleConstants.ROLE_BUTTON_DROPDOWN_GRID),
 	 	BUTTONMENU(0x39,AccessibleConstants.ROLE_BUTTON_MENU),
 	 	CARET(0x07,AccessibleConstants.ROLE_CARET),
 	 	CELL(0x1d,AccessibleConstants.ROLE_TABLECELL),
 	 	CHARACTER(0x20,AccessibleConstants.ROLE_CHARACTER),
 	 	CHART(0x11,AccessibleConstants.ROLE_CHART),
 	 	CHECKBUTTON(0x2c,AccessibleConstants.ROLE_CHECK_BOX),
 	 	CLIENT(0x0a,AccessibleConstants.ROLE_CLIENT_AREA),
 	 	CLOCK(0x3d,AccessibleConstants.ROLE_CLOCK),
 	 	COLUMN(0x1b,AccessibleConstants.ROLE_COLUMN),
 	 	COLUMNHEADER(0x19,AccessibleConstants.ROLE_COLUMN_HEADER),
 	 	COMBOBOX(0x2e,AccessibleConstants.ROLE_COMBO_BOX),
 	 	CURSOR(0x06,AccessibleConstants.ROLE_CURSOR),
 	 	DIAGRAM(0x35,AccessibleConstants.ROLE_DIAGRAM),
 	 	DIAL(0x31,AccessibleConstants.ROLE_DIAL),
 	 	DIALOG(0x12,AccessibleConstants.ROLE_DIALOG),
 	 	DOCUMENT(0x0f,AccessibleConstants.ROLE_DOCUMENT),
 	 	DROPLIST(0x2f,AccessibleConstants.ROLE_DROP_LIST),
 	 	EQUATION(0x37,AccessibleConstants.ROLE_EQUATION),
 	 	GRAPHIC(0x28,AccessibleConstants.ROLE_GRAPHIC),
 	 	GRIP(0x04,AccessibleConstants.ROLE_GRIP),
 	 	GROUPING(0x14,AccessibleConstants.ROLE_GROUPING),
 	 	HELPBALLOON(0x1f,AccessibleConstants.ROLE_HELP_BALLOON),
 	 	HOTKEYFIELD(0x32,AccessibleConstants.ROLE_HOTKEY_FIELD),
 	 	INDICATOR(0x27,AccessibleConstants.ROLE_INDICATOR),
 	 	LINK(0x1e,AccessibleConstants.ROLE_LINK),
 	 	LIST(0x21,AccessibleConstants.ROLE_LIST),
 	 	LISTITEM(0x22,AccessibleConstants.ROLE_LIST_ITEM),
 	 	MENUBAR(0x02,AccessibleConstants.ROLE_MENU_BAR),
 	 	MENUITEM(0x0c,AccessibleConstants.ROLE_MENU_ITEM),
 	 	MENUPOPUP(0x0b,AccessibleConstants.ROLE_MENU_POPUP),
 	 	OUTLINE(0x23,AccessibleConstants.ROLE_OUTLINE),
 	 	OUTLINEITEM(0x24,AccessibleConstants.ROLE_OUTLINEITEM),
 	 	PAGETAB(0x25,AccessibleConstants.ROLE_PAGE_TAB),
 	 	PAGETABLIST(0x3c,AccessibleConstants.ROLE_PAGE_TAB_LIST),
 	 	PANE(0x10,AccessibleConstants.ROLE_PANE),
 	 	PROGRESSBAR(0x30,AccessibleConstants.ROLE_PROGRESS_BAR),
 	 	PROPERTYPAGE(0x26,AccessibleConstants.ROLE_PROPERTY_PAGE),
 	 	PUSHBUTTON(0x2b,AccessibleConstants.ROLE_PUSH_BUTTON),
 	 	RADIOBUTTON(0x2d,AccessibleConstants.ROLE_RADIO_BUTTON),
 	 	ROW(0x1c,AccessibleConstants.ROLE_ROW),
 	 	ROWHEADER(0x1a,AccessibleConstants.ROLE_ROW_HEADER),
 	 	SCROLLBAR(0x03,AccessibleConstants.ROLE_SCROLL_BAR),
 	 	SEPARATOR(0x15,AccessibleConstants.ROLE_SEPARATOR),
 	 	SLIDER(0x33,AccessibleConstants.ROLE_SLIDER),
 	 	SOUND(0x05,AccessibleConstants.ROLE_SOUND),
 	 	SPINBUTTON(0x34,AccessibleConstants.ROLE_SPIN_BUTTON),
 	 	SPLITBUTTON(0x3e,AccessibleConstants.ROLE_SPLIT_BUTTON),
 	 	STATICTEXT(0x29,AccessibleConstants.ROLE_STATICTEXT),
 	 	STATUSBAR(0x17,AccessibleConstants.ROLE_STATUS_BAR),
 	 	TABLE(0x18,AccessibleConstants.ROLE_TABLE),
 	 	TEXT(0x2a,AccessibleConstants.ROLE_TEXT),
 	 	TITLEBAR(0x01,AccessibleConstants.ROLE_TITLE_BAR),
 	 	TOOLBAR(0x16,AccessibleConstants.ROLE_TOOL_BAR),
 	 	TOOLTIP(0x0d,AccessibleConstants.ROLE_TOOL_TIP),
 	 	WHITESPACE(0x3b,AccessibleConstants.ROLE_WHITE_SPACE),
 	 	WINDOW(0x09,AccessibleConstants.ROLE_WINDOW)
 	 	;
		private int val;
		private String str;
		MSAA_ROLE(int val, String str) {
			this.val = val;
			this.str = str;
		}

		public int getValue() {
			return this.val;
		}
		
		public String toString() {
			return this.str;
		}
	}
	
	private static String[] roleMap = new String[256];
	static {
		for (MSAA_ROLE eRole : MSAA_ROLE.values()) {
			roleMap[eRole.getValue()] = eRole.toString();
		}
	}
	public static final String A11Y_MSAA_BUNDLE = "org.a11y.utils.accprobe.accservice.win32.msaa";

	/**
	 * map the given MSAA role constant to a A11Y role constant
	 * 
	 * @param role - MSAA role constant
	 * @return A11Y role constant
	 * @see org.a11y.utils.accprobe.accservice.core.AccessibleConstants
	 */
	public static String getMsaaA11yRoleName (long role) {
		try {
			return roleMap[(int)role];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	public static Set<String> getState(int state) {
		HashSet<String> res = new HashSet<String>();
		if (state == MSAA_STATE.NORMAL.getMask()) {
			res.add(MSAA_STATE.NORMAL.toString());
		} else {
			for (MSAA_STATE chkState : MSAA_STATE.values()) {
				if ((state & chkState.getMask()) != 0) {
					res.add(chkState.toString());
				}
			}
		}
		return res;
	}
	
}
