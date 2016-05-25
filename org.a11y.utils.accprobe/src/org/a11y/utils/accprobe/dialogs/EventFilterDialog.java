/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.dialogs;

import java.util.ArrayList;

import org.a11y.utils.accprobe.Activator;
import org.a11y.utils.accprobe.GuiUtils;
import org.a11y.utils.accprobe.accservice.core.win32.ia2.IA2GuiModel;
import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaGuiModel;
import org.a11y.utils.accprobe.core.model.DefaultModelFactory;
import org.a11y.utils.accprobe.core.model.IRenderableModel;
import org.a11y.utils.accprobe.core.model.events.ModelEventType;
import org.a11y.utils.accprobe.providers.EventFilterLabelProvider;
import org.a11y.utils.accprobe.providers.ExplorerViewNode;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;


public class EventFilterDialog extends Dialog
{

	public static final String DIALOG_TITLE = "Choose Events to Watch";

	public static final String ID = "org.a11y.utils.accprobe.dialogs.EventFilterDialog";

	public static final String EVENT_COLUMN_NAME = "Select events to display";

	public static final String EVENTS_KEY = "selectedEvents";

	public static final String[] defaultEventTypes = {
			// MSAA events
			"EVENT_SYSTEM_MENUSTART",
			"EVENT_SYSTEM_MENUEND",
			"EVENT_SYSTEM_MENUPOPUPSTART",
			"EVENT_SYSTEM_MENUPOPUPEND",
			"EVENT_OBJECT_FOCUS",
			"EVENT_OBJECT_SELECTION",
			"EVENT_OBJECT_SELECTIONADD",
			"EVENT_OBJECT_SELECTIONREMOVE",
			"EVENT_OBJECT_SELECTIONWITHIN",
			"EVENT_OBJECT_STATECHANGE",
			"EVENT_OBJECT_NAMECHANGE",
			"EVENT_OBJECT_DESCRIPTIONCHANGE",
			"EVENT_OBJECT_VALUECHANGE",
			//IA2 Events
			"IA2_EVENT_DOCUMENT_LOAD_COMPLETE",
			"IA2_EVENT_DOCUMENT_LOAD_STOPPED",
			"IA2_EVENT_DOCUMENT_RELOAD",
			"IA2_EVENT_HYPERTEXT_LINK_SELECTED", 
			"IA2_EVENT_TABLE_MODEL_CHANGED",
			"IA2_EVENT_TEXT_CARET_MOVED",
			"IA2_EVENT_TEXT_CHANGED",
			"IA2_EVENT_TEXT_SELECTION_CHANGED",
			};

	@SuppressWarnings("unused")
	private ExplorerViewNode node = null;

	private ModelEventType[] eventTypes = null;

	private CheckboxTableViewer viewer = null;

	private TableColumn eventColumn = null;

	private IRenderableModel model = null;
	
	private ArrayList<String> savedIA2selections = new ArrayList<String>();

	// buttons group
	protected Button selectAllButton = null;

	protected Button deselectAllButton = null;

	protected Button restoreDefaultsButton = null;

	public EventFilterDialog (Shell parentShell, ExplorerViewNode node) {
		super(parentShell);
		this.node = node;
	}

	protected void configureShell (Shell shell) {
		super.configureShell(shell);
		shell.setText(DIALOG_TITLE);
	}

	protected Control createDialogArea (Composite parent) {
		initializeDialogUnits(parent);
		Composite c = (Composite) super.createDialogArea(parent);
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		c.setLayout(layout);
		createEventTable(c);
		createButtonGroup(c);
		loadSettings();
		return c;
	}

	private void createButtonGroup (Composite parent) {
		Composite area = new Composite(parent, SWT.NONE);
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		area.setLayout(layout);
		selectAllButton = new Button(area, SWT.PUSH);
		GuiUtils.configureControl(
			selectAllButton, "selectAll", "Select all events", "Select All");
		SelectionListener listener = new SelectionAdapter() {

			public void widgetSelected (SelectionEvent e) {
				selectAllEvents(true);
			}
		};
		selectAllButton.addSelectionListener(listener);
		deselectAllButton = new Button(area, SWT.PUSH);
		deselectAllButton.setText("Deselect All");
		GuiUtils.configureControl(
			deselectAllButton, "deselectAll", "Deselect all events",
			"Deselect All");
		listener = new SelectionAdapter() {

			public void widgetSelected (SelectionEvent e) {
				selectAllEvents(false);
			}
		};
		deselectAllButton.addSelectionListener(listener);
		restoreDefaultsButton = new Button(area, SWT.PUSH);
		restoreDefaultsButton.setText("Restore defaults");
		GuiUtils.configureControl(
			restoreDefaultsButton, "restoreDefaults",
			"Restore default selections", "Restore Defaults");
		listener = new SelectionAdapter() {

			public void widgetSelected (SelectionEvent e) {
				setDefaults();
			}
		};
		restoreDefaultsButton.addSelectionListener(listener);
	}

	protected void setDefaults () {
		selectAllEvents(false); //first clear them all
		String[] events = defaultEventTypes;
		for (int i = 0; i < eventTypes.length; i++) {
			for (int j = 0; j < events.length; j++) {
				if (events[j].equals(eventTypes[i].getEventName())) {
					viewer.setChecked(eventTypes[i], true);
				}
			}
		}
	}

	protected void selectAllEvents (boolean b) {
		viewer.setAllChecked(b);
	}

	private void createEventTable (Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		GridData gd2 = new GridData(GridData.FILL_BOTH);
		gd2.grabExcessHorizontalSpace = true;
		gd2.grabExcessVerticalSpace = true;
		c.setLayout(new GridLayout());
		c.setLayoutData(gd2);
		GridData ttgd = new GridData(GridData.FILL_BOTH
				| GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		Table table = new Table(c, SWT.CHECK);
		GuiUtils.configureControl(
			table, "eventsTable", ttgd, "Displays event types in a table.");
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		createTableColumns(table);
		viewer = new CheckboxTableViewer(table);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new EventFilterLabelProvider());
		viewer.setSorter(new ViewerSorter());
		model = (IRenderableModel) DefaultModelFactory.getInstance().resolveModel(
				IA2GuiModel.IA2_MODEL);
		eventTypes = model.getModelEventTypes(null);
		viewer.setInput(eventTypes);
	}

	protected void createTableColumns (Table t) {
		eventColumn = new TableColumn(t, SWT.CENTER);
		eventColumn.setText(EVENT_COLUMN_NAME);
		eventColumn.setWidth(300);
	}

	protected void okPressed () {
		saveSettings();
		super.okPressed();
	}

	protected void loadSettings () {
		IDialogSettings settings = Activator.getDefault().getDialogSettings().getSection(
			ID);
		if (settings == null) { // none saved or get failed
			setDefaults();
		}else {
			String[] selectedEvents = settings.getArray(EVENTS_KEY);
			if (selectedEvents != null) {
				if (model != null && model instanceof MsaaGuiModel && !(model instanceof IA2GuiModel)){
					saveIA2Selections(selectedEvents);
				}
				for (int i = 0; i < eventTypes.length; i++) {
					for (int j = 0; j < selectedEvents.length; j++) {
						if (selectedEvents[j].equals(eventTypes[i].getEventName())) {
							viewer.setChecked(eventTypes[i], true);
						}
					}
				}
			}else {
				setDefaults();
			}
		}
	}

	private void saveIA2Selections (String[] selectedEvents) {
		
		for (int i=0; i<selectedEvents.length; i++){
			if (selectedEvents[i].startsWith("IA2_")){
				savedIA2selections.add(selectedEvents[i]);
			}
		}
		
	}

	protected void saveSettings () {
		IDialogSettings settings = Activator.getDefault().getDialogSettings().getSection(
			ID);
		if (settings == null) {
			settings = Activator.getDefault().getDialogSettings().addNewSection(
				ID);
		}
		Object[] checked = viewer.getCheckedElements();
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < checked.length; i++) {
			list.add(((ModelEventType) checked[i]).getEventName());
		}
		if (model != null && model instanceof MsaaGuiModel && !(model instanceof IA2GuiModel)){
			addSavedIA2Selections(list);
		}
		settings.put(
			EVENTS_KEY, (String[]) list.toArray(new String[list.size()]));
	}

	private void addSavedIA2Selections (ArrayList<String> list) {
		list.addAll(savedIA2selections);
		
	}
}