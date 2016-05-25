/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.accservice.core;

/** 
 * This class is a set of constants containing a 
 * common set of states and roles which should be
 * used when returning a state or role from the
 * classes in this package. 
*
 * @author Mike Smith
 */
public final class AccessibleConstants
{

	// relations
	public static final String RELATION_LABEL_FOR = "labelFor";
	public static final String RELATION_LABELED_BY = "labeledBy";
	public static final String RELATION_CONTROLLER_FOR = "controllerFor";
	public static final String RELATION_CONTROLLED_BY = "controlledBy";
	public static final String RELATION_MEMBER_OF = "memberOf";
	public static final String RELATION_NODE_CHILD_OF = "nodeChildOf";
	public static final String RELATION_EMBEDS = "embeds";
	public static final String RELATION_EMBEDDED_BY = "embeddedBy";
	public static final String RELATION_DESCRIPTION_FOR = "descriptionFor";
	public static final String RELATION_DESCRIBED_BY = "describedBy";
	public static final String RELATION_PARENT_WINDOW_OF = "parentWindowOf";
	public static final String RELATION_SUBWINDOW_OF = "subwindowOf";
	public static final String RELATION_FLOWS_TO = "flowsTo";
	public static final String RELATION_FLOWS_FROM = "flowsFrom";
	
	// states
	public static final String STATE_INVISIBLE = "invisible";
	public static final String STATE_VISIBLE = "visible";
	public static final String STATE_NORMAL = "normal";
	public static final String STATE_UNAVAILABLE = "unavailable";
	public static final String STATE_SELECTED = "selected";
	public static final String STATE_SELECTABLE = "selectable";
	public static final String STATE_SELECTABLE_TEXT = "selectableText";
	public static final String STATE_MOVEABLE = "moveable";
	public static final String STATE_MULTI_SELECTABLE = "multiSelectable";
	public static final String STATE_FOCUSED = "focused";
	public static final String STATE_FOCUSABLE = "focusable";
	public static final String STATE_PRESSED = "pressed";
	public static final String STATE_CHECKED = "checked";
	public static final String STATE_CHECKABLE = "checkable";
	public static final String STATE_EXPANDED = "expanded";
	public static final String STATE_COLLAPSED = "collapsed";
	public static final String STATE_HOT_TRACKED = "hotTracked";
	public static final String STATE_BUSY = "busy";
	public static final String STATE_READ_ONLY = "readOnly";
	public static final String STATE_OFF_SCREEN = "offScreen";
	public static final String STATE_SIZEABLE = "sizeable";
	public static final String STATE_LINKED = "linked";
	public static final String STATE_ACTIVE = "active";
	public static final String STATE_ARMED = "armed";
	public static final String STATE_EDITABLE = "editable";
	public static final String STATE_EXPANDABLE = "expandable";
	public static final String STATE_ENABLED = "enabled";
	public static final String STATE_ICONIFIED = "iconified";
	public static final String STATE_MODAL = "modal";
	public static final String STATE_OPAQUE = "opaque";
	public static final String STATE_SHOWING = "showing";
	public static final String STATE_SINGLE_LINE = "singleLine";
	public static final String STATE_MIXED = "mixed";
	public static final String STATE_MULTI_LINE = "multiLine";
	public static final String STATE_TRANSIENT = "transient";
	public static final String STATE_MANAGES_DESCENDENTS = "managesDescendents";
	public static final String STATE_INDETERMINANT = "indeterminant";
	public static final String STATE_TRUNCATED = "truncated";
	public static final String STATE_VERTICAL = "vertical";
	public static final String STATE_HORIZONTAL = "horizontal";
	public static final String STATE_DEFUNCT = "defunct";
	public static final String STATE_INVALID = "invalid";
	public static final String STATE_INVALID_ENTRY = "invalidEntry";
	public static final String STATE_REQUIRED = "required";
	public static final String STATE_STALE = "stale";
	public static final String STATE_SUPPORT_AUTOCOMPLETION = "supportAutocompletion";
	public static final String STATE_HASPOPUP ="hasPopup";
	public static final String STATE_PINNED ="pinned";
	public static final String STATE_DEFAULT = "default";
	public static final String STATE_FLOATING = "floating";
	public static final String STATE_MARQUEED = "marqueed";
	public static final String STATE_ANIMATED = "animated";
	
	// roles
	public static final String ROLE_ALERT = "alert";
	public static final String ROLE_AWT_COMPONENT = "awtComponent";
	public static final String ROLE_CANVAS = "canvas";
	public static final String ROLE_CAPTION = "caption";
	public static final String ROLE_CHECK_MENU_ITEM = "checkMenuItem";
	public static final String ROLE_CHECK_BOX = "checkBox";
	public static final String ROLE_COLOR_CHOOSER = "colorChooser";
	public static final String ROLE_COLUMN_HEADER = "columnHeader";
	public static final String ROLE_COMBO_BOX = "comboBox";
	public static final String ROLE_DATE_EDITOR = "dateEditor";
	public static final String ROLE_DESKTOP_ICON = "desktopIcon";
	public static final String ROLE_DESKTOP_PANE = "desktopPane";
	public static final String ROLE_DIALOG = "dialog";
	public static final String ROLE_DIRECTORY_PANE = "directoryPane";
	public static final String ROLE_EDIT_BAR = "editBar";
	public static final String ROLE_EDIT_LIST = "editList";
	public static final String ROLE_EMBEDDED_OBJECT = "embeddedObject";
	public static final String ROLE_END_NOTE = "endNote";
	public static final String ROLE_FILE_CHOOSER = "fileChooser";
	public static final String ROLE_FILLER = "filler";
	public static final String ROLE_FONT_CHOOSER = "fontChooser";
	public static final String ROLE_FOOTER = "footer";
	public static final String ROLE_FOOTNOTE = "footnote";
	public static final String ROLE_FORM = "form";
	public static final String ROLE_FRAME = "frame";
	public static final String ROLE_GLASS_PANE = "glassPanel";
	public static final String ROLE_GROUP_BOX = "groupBox";
	public static final String ROLE_HEADER = "header";
	public static final String ROLE_HEADING = "heading";
	public static final String ROLE_HEADING1 = "heading1";
	public static final String ROLE_HEADING2 = "heading2";
	public static final String ROLE_HEADING3 = "heading3";
	public static final String ROLE_HEADING4 = "heading4";
	public static final String ROLE_HEADING5 = "heading5";
	public static final String ROLE_HEADING6 = "heading6";
	public static final String ROLE_HEADING7 = "heading7";
	public static final String ROLE_HEADING8 = "heading8";
	public static final String ROLE_HEADING9 = "heading9";
	public static final String ROLE_HEADING10 = "heading10";
	public static final String ROLE_HYPERLINK = "hyperlink";
	public static final String ROLE_ICON = "icon";
	public static final String ROLE_IMAGE_MAP = "imageMap";
	public static final String ROLE_INPUT_METHOD_WINDOW = "inputMethodWindow";
	public static final String ROLE_INTERNAL_FRAME = "internalFrame";
	public static final String ROLE_LABEL = "label";
	public static final String ROLE_LAYERED_PANE = "layeredPane";
	public static final String ROLE_LIST = "list";
	public static final String ROLE_LIST_ITEM = "listItem";
	public static final String ROLE_MENU = "menu";
	public static final String ROLE_MENU_BAR = "menuBar";
	public static final String ROLE_MENU_ITEM = "menuItem";
	public static final String ROLE_NOTE = "note";
	public static final String ROLE_OPTION_PANE = "optionPane";
	public static final String ROLE_PAGE = "page";
	public static final String ROLE_PAGE_TAB = "pageTab";
	public static final String ROLE_PAGE_TAB_LIST = "pageTabList";
	public static final String ROLE_PANEL = "panel";
	public static final String ROLE_PARAGRAPH = "paragraph";
	public static final String ROLE_PASSWORD_TEXT = "passwordText";
	public static final String ROLE_POPUP_MENU = "popupMenu";
	public static final String ROLE_PROGRESS_BAR = "progressBar";
	public static final String ROLE_PROGRESS_MONITOR = "progressMonitor";
	public static final String ROLE_PUSH_BUTTON = "pushButton";
	public static final String ROLE_RADIO_BUTTON = "radioButton";
	public static final String ROLE_RADIO_MENU_ITEM = "radioMenuItem";
	public static final String ROLE_REDUNDANT_OBJECT = "redundantObject";
	public static final String ROLE_ROOT_PANE = "rootPane";
	public static final String ROLE_ROW_HEADER = "rowHeader";
	public static final String ROLE_RULER = "ruler";
	public static final String ROLE_SCROLL_BAR = "scrollBar";
	public static final String ROLE_SCROLL_PANE = "scrollPane";
	public static final String ROLE_SEPARATOR = "separator";
	public static final String ROLE_SHAPE = "shape";
	public static final String ROLE_SECTION = "section";
	public static final String ROLE_SLIDER = "slider";
	public static final String ROLE_SPIN_BOX = "spinBox";
	public static final String ROLE_SPLIT_PANE = "splitPane";
	public static final String ROLE_STATUS_BAR = "statusBar";
	public static final String ROLE_SWING_COMPONENT = "swingComponent";
	public static final String ROLE_TABLE = "table";
	public static final String ROLE_TEXT = "text";
	public static final String ROLE_TEXT_FRAME = "textFrame";
	public static final String ROLE_TOGGLE_BUTTON = "toggleButton";
	public static final String ROLE_TOOL_BAR = "toolBar";
	public static final String ROLE_TOOL_TIP = "toolTip";
	public static final String ROLE_TREE = "tree";
	public static final String ROLE_UNKNOWN = "unknown";
	public static final String ROLE_VIEW_PORT = "viewPort";
	public static final String ROLE_WINDOW = "window";
	public static final String ROLE_COMPLEMENTARY_CONTENT = "complementaryContent";
	
	// -------------- Additional Roles in ACC class but not in java
	public static final String ROLE_CLIENT_AREA = "clientArea";
	public static final String ROLE_TABLECELL = "tableCell";
	public static final String ROLE_TABLECOLUMNHEADER = "tableColumnHeader";
	public static final String ROLE_TABLEROWHEADER = "tableRowHeader";
	public static final String ROLE_TREEITEM = "treeItem";
	public static final String ROLE_LINK = "link";
	public static final String ROLE_TABFOLDER = "tabFolder";
	public static final String ROLE_TABITEM = "tabItem";

	//	 -------------- Additional Roles in oleacc.h but not in java
	public static final String ROLE_TITLE_BAR = "titleBar";
	public static final String ROLE_GRIP = "grip";
	public static final String ROLE_INDICATOR = "indicator";
	public static final String ROLE_PANE = "pane";
	public static final String ROLE_GRAPHIC = "graphic";
	public static final String ROLE_SPLIT_BUTTON = "splitButton";
	public static final String ROLE_SOUND = "sound";
	public static final String ROLE_CURSOR = "cursor";
	public static final String ROLE_CARET = "caret";
	public static final String ROLE_APPLICATION = "application";
	public static final String ROLE_DOCUMENT = "document";
	public static final String ROLE_CHART = "chart";
	public static final String ROLE_BORDER = "border";
	public static final String ROLE_GROUPING = "grouping";
	public static final String ROLE_COLUMN = "column";
	public static final String ROLE_ROW = "row";
	public static final String ROLE_HELP_BALLOON = "helpBalloon";
	public static final String ROLE_CHARACTER = "character";
	public static final String ROLE_PROPERTY_PAGE = "propertyPage";
	public static final String ROLE_DROP_LIST = "dropList";
	public static final String ROLE_DIAL = "dial";
	public static final String ROLE_HOTKEY_FIELD = "hotkeyField";
	public static final String ROLE_SPIN_BUTTON = "spinButton";
	public static final String ROLE_DIAGRAM = "diagram";
	public static final String ROLE_ANIMATION = "animation";
	public static final String ROLE_EQUATION = "equation";
	public static final String ROLE_BUTTON_DROPDOWN = "buttonDropdown";
	public static final String ROLE_BUTTON_MENU = "buttonMenu";
	public static final String ROLE_BUTTON_DROPDOWN_GRID = "buttonDropdownGrid";
	public static final String ROLE_WHITE_SPACE = "whiteSpace";
	public static final String ROLE_CLOCK = "clock";
	public static final String ROLE_IP_ADDRESS = "ipAddress";
	public static final String ROLE_OUTLINE_BUTTON = "outlineButton";
	public static final String ROLE_TEAR_OFF_MENU ="tearOffMenu";
	public static final String ROLE_TERMINAL ="terminal";
	public static final String STATE_EXTSELECTABLE = "extendSelectable";
	public static final String STATE_ALERT_LOW = "alertLow";
	public static final String STATE_ALERT_HIGH = "alertHigh";
	public static final String STATE_ALERT_MEDIUM = "alertMedium";
	public static final String ROLE_MENU_POPUP = "menuPopup";
	public static final String ROLE_OUTLINE = "outline";
	public static final String ROLE_OUTLINEITEM = "outlineItem";
	public static final String ROLE_STATICTEXT = "staticText";
	public static final String STATE_SELF_VOICING = "selfVoicing";
	public static final String STATE_TRAVERSED = "traversed";
	
}
