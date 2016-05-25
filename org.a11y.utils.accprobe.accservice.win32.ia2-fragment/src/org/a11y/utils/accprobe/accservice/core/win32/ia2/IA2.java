/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
* IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core.win32.ia2;

import org.a11y.utils.accprobe.accservice.core.AccessibleConstants;

/**
 * utility constants and methods for IA2
*
 * @author johnb, Kavitha Teegala
 *
 */
public final class IA2
{


	// These values were obtained from the IA2 specification
	public static final long  IA2_STATE_ACTIVE = 0x1 ;
	public static final long  IA2_STATE_ARMED = 0x2 ;
	public static final long  IA2_STATE_DEFUNCT = 0x4 ;
	public static final long  IA2_STATE_EDITABLE = 0x8 ;
	public static final long  IA2_STATE_HORIZONTAL = 0x10 ;
	public static final long  IA2_STATE_ICONIFIED = 0x20 ;
	public static final long  IA2_STATE_INVALID_ENTRY = 0x40 ;
	public static final long  IA2_STATE_MANAGES_DESCENDANTS = 0x80 ;
	public static final long  IA2_STATE_MODAL = 0x100 ;
	public static final long  IA2_STATE_MULTI_LINE = 0x200 ;
	public static final long  IA2_STATE_OPAQUE = 0x400 ;
	public static final long  IA2_STATE_REQUIRED = 0x800 ;
	public static final long  IA2_STATE_SELECTABLE_TEXT = 0x1000 ;
	public static final long  IA2_STATE_SINGLE_LINE = 0x2000 ;
	public static final long  IA2_STATE_STALE = 0x4000 ;
	public static final long  IA2_STATE_SUPPORTS_AUTOCOMPLETION = 0x8000 ;
	public static final long  IA2_STATE_TRANSIENT = 0x10000 ;
	public static final long  IA2_STATE_VERTICAL = 0x20000 ;
	public static final long  IA2_STATE_CHECKABLE = 0x40000 ;
	public static final long  IA2_STATE_PINNED = 0x80000 ;


	public static final long IA2_ROLE_UNKNOWN	= 0;
	public static final long IA2_ROLE_CANVAS	= 0x401;
	public static final long IA2_ROLE_CAPTION	= IA2_ROLE_CANVAS+1;
	public static final long IA2_ROLE_CHECK_MENU_ITEM	= IA2_ROLE_CAPTION + 1;
	public static final long IA2_ROLE_COLOR_CHOOSER	= IA2_ROLE_CHECK_MENU_ITEM + 1;
	public static final long IA2_ROLE_DATE_EDITOR	= IA2_ROLE_COLOR_CHOOSER + 1;
	public static final long IA2_ROLE_DESKTOP_ICON	= IA2_ROLE_DATE_EDITOR + 1;
	public static final long IA2_ROLE_DESKTOP_PANE	= IA2_ROLE_DESKTOP_ICON + 1;
	public static final long IA2_ROLE_DIRECTORY_PANE	= IA2_ROLE_DESKTOP_PANE + 1;
	public static final long IA2_ROLE_EDITBAR	= IA2_ROLE_DIRECTORY_PANE + 1;
	public static final long IA2_ROLE_EMBEDDED_OBJECT	= IA2_ROLE_EDITBAR + 1;
	public static final long IA2_ROLE_ENDNOTE	= IA2_ROLE_EMBEDDED_OBJECT + 1;
	public static final long IA2_ROLE_FILE_CHOOSER	= IA2_ROLE_ENDNOTE + 1;
	public static final long IA2_ROLE_FONT_CHOOSER	= IA2_ROLE_FILE_CHOOSER + 1;
	public static final long IA2_ROLE_FOOTER	= IA2_ROLE_FONT_CHOOSER + 1;
	public static final long IA2_ROLE_FOOTNOTE	= IA2_ROLE_FOOTER + 1;
	public static final long IA2_ROLE_FORM	= IA2_ROLE_FOOTNOTE + 1;
	public static final long IA2_ROLE_FRAME	= IA2_ROLE_FORM + 1;
	public static final long IA2_ROLE_GLASS_PANE	= IA2_ROLE_FRAME + 1;
	public static final long IA2_ROLE_HEADER	= IA2_ROLE_GLASS_PANE + 1;
	public static final long IA2_ROLE_HEADING	= IA2_ROLE_HEADER + 1;
	public static final long IA2_ROLE_ICON	= IA2_ROLE_HEADING + 1;
	public static final long IA2_ROLE_IMAGE_MAP	= IA2_ROLE_ICON + 1;
	public static final long IA2_ROLE_INPUT_METHOD_WINDOW	= IA2_ROLE_IMAGE_MAP + 1;
	public static final long IA2_ROLE_INTERNAL_FRAME	= IA2_ROLE_INPUT_METHOD_WINDOW + 1;
	public static final long IA2_ROLE_LABEL	= IA2_ROLE_INTERNAL_FRAME + 1;
	public static final long IA2_ROLE_LAYERED_PANE	= IA2_ROLE_LABEL + 1;
	public static final long IA2_ROLE_NOTE	= IA2_ROLE_LAYERED_PANE + 1;
	public static final long IA2_ROLE_OPTION_PANE	= IA2_ROLE_NOTE + 1;
	public static final long IA2_ROLE_PAGE	= IA2_ROLE_OPTION_PANE + 1;
	public static final long IA2_ROLE_PARAGRAPH	= IA2_ROLE_PAGE + 1;
	public static final long IA2_ROLE_RADIO_MENU_ITEM	= IA2_ROLE_PARAGRAPH + 1;
	public static final long IA2_ROLE_REDUNDANT_OBJECT	= IA2_ROLE_RADIO_MENU_ITEM + 1;
	public static final long IA2_ROLE_ROOT_PANE	= IA2_ROLE_REDUNDANT_OBJECT + 1;
	public static final long IA2_ROLE_RULER	= IA2_ROLE_ROOT_PANE + 1;
	public static final long IA2_ROLE_SCROLL_PANE	= IA2_ROLE_RULER + 1;
	public static final long IA2_ROLE_SECTION	= IA2_ROLE_SCROLL_PANE + 1;
	public static final long IA2_ROLE_SHAPE	= IA2_ROLE_SECTION + 1;
	public static final long IA2_ROLE_SPLIT_PANE	= IA2_ROLE_SHAPE + 1;
	public static final long IA2_ROLE_TEAR_OFF_MENU	= IA2_ROLE_SPLIT_PANE + 1;
	public static final long IA2_ROLE_TERMINAL	= IA2_ROLE_TEAR_OFF_MENU + 1;
	public static final long IA2_ROLE_TEXT_FRAME	= IA2_ROLE_TERMINAL + 1;
	public static final long IA2_ROLE_TOGGLE_BUTTON	= IA2_ROLE_TEXT_FRAME + 1;
	public static final long IA2_ROLE_VIEW_PORT	= IA2_ROLE_TOGGLE_BUTTON + 1;
	public static final long IA2_ROLE_COMPLEMENTARY_CONTENT	= IA2_ROLE_VIEW_PORT + 1;


	/*public static final long IA2_ROLE_UNKNOWN = 0;

	public static final long IA2_ROLE_CAPTION = 1025;

	public static final long IA2_ROLE_CHECK_MENU_ITEM = 1026;

	public static final long IA2_ROLE_COLOR_CHOOSER = 1027;

	public static final long IA2_ROLE_DATE_EDITOR = 1028;

	public static final long IA2_ROLE_DESKTOP_ICON = 1029;

	public static final long IA2_ROLE_DESKTOP_PANE = 1030;

	public static final long IA2_ROLE_DIRECTORY_PANE = 1031;

	public static final long IA2_ROLE_EDIT_BAR = 1032;

	// Often used in font selection dialogs.
	public static final long IA2_ROLE_EMBEDDED_OBJECT = 1033;

	public static final long IA2_ROLE_ENDNOTE = 1034;

	public static final long IA2_ROLE_FILE_CHOOSER = 1035;

	public static final long IA2_ROLE_FONT_CHOOSER = 1036;

	public static final long IA2_ROLE_FOOTER = 1037;

	public static final long IA2_ROLE_FOOTNOTE = 1038;

	public static final long IA2_ROLE_FRAME = 1039;

	public static final long IA2_ROLE_GLASS_PANE = 1040;

	public static final long IA2_ROLE_HEADER = 1041;

	public static final long IA2_ROLE_HEADING1 = 1042;

	public static final long IA2_ROLE_HEADING2 = 1043;

	public static final long IA2_ROLE_HEADING3 = 1044;

	public static final long IA2_ROLE_HEADING4 = 1045;

	public static final long IA2_ROLE_HEADING5 = 1046;

	public static final long IA2_ROLE_HEADING6 = 1047;

	public static final long IA2_ROLE_HEADING7 = 1048;

	public static final long IA2_ROLE_HEADING8 = 1049;

	public static final long IA2_ROLE_HEADING9 = 1050;

	public static final long IA2_ROLE_HEADING10 = 1051;

	public static final long IA2_ROLE_ICON = 1052;

	public static final long IA2_ROLE_IMAGE_MAP = 1053;

	public static final long IA2_ROLE_INTERNAL_FRAME = 1054;

	public static final long IA2_ROLE_LABEL = 1055;

	public static final long IA2_ROLE_LAYERED_PANE = 1056;

	public static final long IA2_ROLE_NOTE = 1057;

	public static final long IA2_ROLE_OPTION_PANE = 1058;

	public static final long IA2_ROLE_PARAGRAPH = 1059;

	public static final long IA2_ROLE_RADIO_MENU_ITEM = 1060;

	public static final long IA2_ROLE_ROOT_PANE = 1061;

	public static final long IA2_ROLE_RULER = 1062;

	public static final long IA2_ROLE_SCROLL_PANE = 1063;

	public static final long IA2_ROLE_SHAPE = 1064;

	public static final long IA2_ROLE_SPLIT_PANE = 1065;

	public static final long IA2_ROLE_TEXT_FRAME = 1066;

	public static final long IA2_ROLE_TOGGLE_BUTTON = 1067;

	public static final long IA2_ROLE_VIEW_PORT = 1068;*/

		public static String getIA2A11yRoleName (long role) {
		String res = null;
		if (role == IA2_ROLE_CANVAS) {
			res = AccessibleConstants.ROLE_CANVAS;
		}else if (role == IA2_ROLE_CAPTION) {
			res = AccessibleConstants.ROLE_CAPTION;
		}else if (role == IA2_ROLE_CHECK_MENU_ITEM) {
			res = AccessibleConstants.ROLE_CHECK_MENU_ITEM;
		}else if (role == IA2_ROLE_COLOR_CHOOSER) {
			res = AccessibleConstants.ROLE_COLOR_CHOOSER;
		}else if (role == IA2_ROLE_DATE_EDITOR) {
			res = AccessibleConstants.ROLE_DATE_EDITOR;
		}else if (role == IA2_ROLE_DESKTOP_ICON) {
			res = AccessibleConstants.ROLE_DESKTOP_ICON;
		}else if (role == IA2_ROLE_DESKTOP_PANE) {
			res = AccessibleConstants.ROLE_DESKTOP_PANE;
		}else if (role == IA2_ROLE_DIRECTORY_PANE) {
			res = AccessibleConstants.ROLE_DIRECTORY_PANE;
		}else if (role == IA2_ROLE_EDITBAR) {
			res = AccessibleConstants.ROLE_EDIT_BAR;
		}else if (role == IA2_ROLE_EMBEDDED_OBJECT) {
			res = AccessibleConstants.ROLE_EMBEDDED_OBJECT;
		}else if (role == IA2_ROLE_ENDNOTE) {
			res = AccessibleConstants.ROLE_END_NOTE;
		}else if (role == IA2_ROLE_FILE_CHOOSER) {
			res = AccessibleConstants.ROLE_FILE_CHOOSER;
		}else if (role == IA2_ROLE_FONT_CHOOSER) {
			res = AccessibleConstants.ROLE_FONT_CHOOSER;
		}else if (role == IA2_ROLE_FOOTER) {
			res = AccessibleConstants.ROLE_FOOTER;
		}else if (role == IA2_ROLE_FOOTNOTE) {
			res = AccessibleConstants.ROLE_FOOTNOTE;
		}else if (role == IA2_ROLE_FORM) {
			res = AccessibleConstants.ROLE_FORM;
		}else if (role == IA2_ROLE_FRAME) {
			res = AccessibleConstants.ROLE_FRAME;
		}else if (role == IA2_ROLE_GLASS_PANE) {
			res = AccessibleConstants.ROLE_GLASS_PANE;
		}else if (role == IA2_ROLE_HEADER) {
			res = AccessibleConstants.ROLE_HEADER;
		}else if (role == IA2_ROLE_HEADING) {
			res = AccessibleConstants.ROLE_HEADING;
//		}else if (role == IA2_ROLE_HEADING2) {
//			res = AccessibleConstants.ROLE_HEADING2;
//		}else if (role == IA2_ROLE_HEADING3) {
//			res = AccessibleConstants.ROLE_HEADING3;
//		}else if (role == IA2_ROLE_HEADING4) {
//			res = AccessibleConstants.ROLE_HEADING4;
//		}else if (role == IA2_ROLE_HEADING5) {
//			res = AccessibleConstants.ROLE_HEADING5;
//		}else if (role == IA2_ROLE_HEADING6) {
//			res = AccessibleConstants.ROLE_HEADING6;
//		}else if (role == IA2_ROLE_HEADING7) {
//			res = AccessibleConstants.ROLE_HEADING7;
//		}else if (role == IA2_ROLE_HEADING8) {
//			res = AccessibleConstants.ROLE_HEADING8;
//		}else if (role == IA2_ROLE_HEADING9) {
//			res = AccessibleConstants.ROLE_HEADING9;
//		}else if (role == IA2_ROLE_HEADING10) {
//			res = AccessibleConstants.ROLE_HEADING10;
		}else if (role == IA2_ROLE_ICON) {
			res = AccessibleConstants.ROLE_ICON;
		}else if (role == IA2_ROLE_IMAGE_MAP) {
			res = AccessibleConstants.ROLE_IMAGE_MAP;
		}else if (role == IA2_ROLE_INPUT_METHOD_WINDOW) {
			res = AccessibleConstants.ROLE_INPUT_METHOD_WINDOW;
		}else if (role == IA2_ROLE_INTERNAL_FRAME) {
			res = AccessibleConstants.ROLE_INTERNAL_FRAME;
		}else if (role == IA2_ROLE_LABEL) {
			res = AccessibleConstants.ROLE_LABEL;
		}else if (role == IA2_ROLE_LAYERED_PANE) {
			res = AccessibleConstants.ROLE_LAYERED_PANE;
		}else if (role == IA2_ROLE_NOTE) {
			res = AccessibleConstants.ROLE_NOTE;
		}else if (role == IA2_ROLE_OPTION_PANE) {
			res = AccessibleConstants.ROLE_OPTION_PANE;
		}else if (role == IA2_ROLE_PAGE) {
			res = AccessibleConstants.ROLE_PAGE;
		}else if (role == IA2_ROLE_PARAGRAPH) {
			res = AccessibleConstants.ROLE_PARAGRAPH;
		}else if (role == IA2_ROLE_RADIO_MENU_ITEM) {
			res = AccessibleConstants.ROLE_RADIO_MENU_ITEM;
		}else if (role == IA2_ROLE_REDUNDANT_OBJECT) {
			res = AccessibleConstants.ROLE_REDUNDANT_OBJECT;
		}else if (role == IA2_ROLE_ROOT_PANE) {
			res = AccessibleConstants.ROLE_ROOT_PANE;
		}else if (role == IA2_ROLE_RULER) {
			res = AccessibleConstants.ROLE_RULER;
		}else if (role == IA2_ROLE_SCROLL_PANE) {
			res = AccessibleConstants.ROLE_SCROLL_PANE;
		}else if (role == IA2_ROLE_SECTION) {
			res = AccessibleConstants.ROLE_SECTION;
		}else if (role == IA2_ROLE_SHAPE) {
			res = AccessibleConstants.ROLE_SHAPE;
		}else if (role == IA2_ROLE_SPLIT_PANE) {
			res = AccessibleConstants.ROLE_SPLIT_PANE;
		}else if (role == IA2_ROLE_TEAR_OFF_MENU) {
			res = AccessibleConstants.ROLE_TEAR_OFF_MENU;
		}else if (role == IA2_ROLE_TERMINAL) {
			res = AccessibleConstants.ROLE_TERMINAL;
		}else if (role == IA2_ROLE_TEXT_FRAME) {
			res = AccessibleConstants.ROLE_TEXT_FRAME;
		}else if (role == IA2_ROLE_TOGGLE_BUTTON) {
			res = AccessibleConstants.ROLE_TOGGLE_BUTTON;
		}else if (role == IA2_ROLE_VIEW_PORT) {
			res = AccessibleConstants.ROLE_VIEW_PORT;
		}else if (role == IA2_ROLE_COMPLEMENTARY_CONTENT) {
			res = AccessibleConstants.ROLE_COMPLEMENTARY_CONTENT;
		}
		return res;
	}

}
