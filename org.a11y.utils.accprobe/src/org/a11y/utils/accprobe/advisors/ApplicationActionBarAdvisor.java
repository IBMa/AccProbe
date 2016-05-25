/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.advisors;

import org.a11y.utils.accprobe.actions.AlwaysOnTopAction;
import org.a11y.utils.accprobe.actions.CaretPositionAction;
import org.a11y.utils.accprobe.actions.GlobalTrackingAction;
import org.a11y.utils.accprobe.actions.HighlightSelectedAction;
import org.a11y.utils.accprobe.actions.KeyboardFocusAction;
import org.a11y.utils.accprobe.actions.MouseCursorAction;
import org.a11y.utils.accprobe.actions.ResetViewsAction;
import org.a11y.utils.accprobe.actions.SuspendAllAction;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor
{

	public static final String M_OPTIONS = "options";

	public static final String ON_TOP_KEY = "onTopPref";

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.
	// File menu
	private IWorkbenchAction preferencesAction;
	private ResetViewsAction resetViewsAction;
	
	private IWorkbenchAction exitAction;

	// Navigation menu
	private IWorkbenchAction showPartPaneMenuAction;

	private IWorkbenchAction showViewMenuAction;

	private IWorkbenchAction maxViewAction;

	private IWorkbenchAction minViewAction;

	private IWorkbenchAction nextViewAction;

	private IWorkbenchAction prevViewAction;

	// Options menu
	private AlwaysOnTopAction alwaysOnTopAction;
	
	private HighlightSelectedAction highlightSelectedAction;
	
	private KeyboardFocusAction keyboardFocusAction;
	private MouseCursorAction mouseCursorAction;
	private CaretPositionAction caretPositionAction;
	private GlobalTrackingAction globalTrackingAction;
	private SuspendAllAction suspendAllAction;


	// Help Menu
	private IWorkbenchAction aboutAction;

	public ApplicationActionBarAdvisor (IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions (final IWorkbenchWindow window) {
		// Creates the actions and registers them.
		// Registering is needed to ensure that key bindings work.
		// The corresponding commands keybindings are defined in the plugin.xml
		// file.
		// Registering also provides automatic disposal of the actions when
		// the window is closed.
		// file menu
		/* comment this out until we actually have some preferences for AccProbe
		preferencesAction = ActionFactory.PREFERENCES.create(window);
		register(preferencesAction);
		*/
		resetViewsAction = new ResetViewsAction(window, "Restore Views to default positions");
		exitAction = ActionFactory.QUIT.create(window);
		register(exitAction);
		// nav menu
		// this is the Show System Menu action
		showPartPaneMenuAction = ActionFactory.SHOW_PART_PANE_MENU.create(window);
		register(showPartPaneMenuAction);
		showViewMenuAction = ActionFactory.SHOW_VIEW_MENU.create(window);
		register(showViewMenuAction);
		maxViewAction = ActionFactory.MAXIMIZE.create(window);
		register(maxViewAction);
		minViewAction = ActionFactory.MINIMIZE.create(window);
		register(minViewAction);
		nextViewAction = ActionFactory.NEXT_PART.create(window);
		register(nextViewAction);
		prevViewAction = ActionFactory.PREVIOUS_PART.create(window);
		register(prevViewAction);
		// Link next and previous view together
		ActionFactory.linkCycleActionPair(nextViewAction, prevViewAction);
		//options menu
		alwaysOnTopAction = new AlwaysOnTopAction(window, "&Always on top");
		register(alwaysOnTopAction);
		
		keyboardFocusAction = new KeyboardFocusAction(window,"&Keyboard focus");
		register(keyboardFocusAction);
		
		mouseCursorAction = new MouseCursorAction(window,"&Mouse cursor");
		register(mouseCursorAction);
		
		caretPositionAction = new CaretPositionAction(window,"&Caret position");
		register(caretPositionAction);
		
		globalTrackingAction = new GlobalTrackingAction(window, "&Enable Global Tracking");
		register(globalTrackingAction);
					
		highlightSelectedAction = new HighlightSelectedAction(window, "&Highlight Selected");
		register(highlightSelectedAction);
		//toolbar
		suspendAllAction = new SuspendAllAction(window, "&Suspend/Resume all actions");
		register(suspendAllAction);
		// help menu
		aboutAction = ActionFactory.ABOUT.create(window);
		register(aboutAction);
	}

	protected void fillMenuBar (IMenuManager menuBar) {
		menuBar.add(createFileMenu());
		menuBar.add(createNavigationMenu());
		menuBar.add(createOptionsMenu());
		// Add a group marker indicating where action set menus will appear.
		menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		menuBar.add(createHelpMenu());
	}

	private IContributionItem createFileMenu () {
		MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
		fileMenu.add(new GroupMarker(IWorkbenchActionConstants.FILE_START));
		//fileMenu.add(preferencesAction);
		fileMenu.add(resetViewsAction);
		fileMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		fileMenu.add(new Separator());
		fileMenu.add(exitAction);
		fileMenu.add(new GroupMarker(IWorkbenchActionConstants.FILE_END));
		return fileMenu;
	}

	private IContributionItem createNavigationMenu () {
		MenuManager navMenu = new MenuManager("&Navigation", IWorkbenchActionConstants.M_NAVIGATE);
		navMenu.add(new GroupMarker(IWorkbenchActionConstants.NAV_START));
		navMenu.add(showPartPaneMenuAction);
		navMenu.add(showViewMenuAction);
		navMenu.add(new Separator());
		navMenu.add(maxViewAction);
		navMenu.add(minViewAction);
		navMenu.add(new Separator());
		navMenu.add(nextViewAction);
		navMenu.add(prevViewAction);
		navMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		navMenu.add(new GroupMarker(IWorkbenchActionConstants.NAV_END));
		return navMenu;
	}

	private IContributionItem createOptionsMenu () {
		MenuManager optionsMenu = new MenuManager("&Options", M_OPTIONS);
		optionsMenu.add(alwaysOnTopAction);
		optionsMenu.add(highlightSelectedAction);
		optionsMenu.add(createTrackingMenu());
		return optionsMenu;
	}

	private IContributionItem createTrackingMenu () {
		MenuManager trackingMenu = new MenuManager("&Tracking", M_OPTIONS);
		//ClearWorkingSetAction noAction = new ClearWorkingSetAction(null);
		GroupMarker gmarker = new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS);
		trackingMenu.add(gmarker);
		trackingMenu.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS,keyboardFocusAction);
		trackingMenu.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS,mouseCursorAction);
		trackingMenu.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS,caretPositionAction);
		trackingMenu.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS,globalTrackingAction);
		return trackingMenu;
	}
	
	private IContributionItem createHelpMenu () {
		MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);
		helpMenu.add(aboutAction);
		return helpMenu;
	}

	protected void fillCoolBar (ICoolBarManager coolBar) {
		//IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		//coolBar.add(new ToolBarContributionItem(toolbar, "main"));
		// TODO none defined at this time
		/*
		 * toolbar.add(openViewAction); toolbar.add(messagePopupAction);
		 */
		ToolBarManager toolbar = new ToolBarManager(SWT.COLOR_BLUE);
		toolbar.add(suspendAllAction);
		coolBar.add(toolbar);
	}

	@Override
	public IStatus restoreState (IMemento memento) {
		String onTop = memento.getString(ON_TOP_KEY);
		if (onTop != null) {
			Boolean bool = new Boolean(onTop);
			alwaysOnTopAction.setChecked(bool.booleanValue());
			alwaysOnTopAction.run();
		}
		return super.restoreState(memento);
	}

	@Override
	public IStatus saveState (IMemento memento) {
		String onTop = Boolean.toString(alwaysOnTopAction.isChecked());
		memento.putString(ON_TOP_KEY, onTop);
		return super.saveState(memento);
	}
}
