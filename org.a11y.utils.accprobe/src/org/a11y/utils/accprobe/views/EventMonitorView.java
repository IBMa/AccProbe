/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.views;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.a11y.utils.accprobe.Activator;
import org.a11y.utils.accprobe.GuiUtils;
import org.a11y.utils.accprobe.accservice.AccessibilityServiceException;
import org.a11y.utils.accprobe.accservice.AccessibilityServiceManager;
import org.a11y.utils.accprobe.accservice.IAccessibilityService;
import org.a11y.utils.accprobe.accservice.core.IAccessibleElement;
import org.a11y.utils.accprobe.accservice.core.win32.ia2.IA2AccessibilityService;
import org.a11y.utils.accprobe.accservice.core.win32.ia2.IA2Accessible;
import org.a11y.utils.accprobe.accservice.core.win32.ia2.IA2GuiModel;
import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaAccessibilityEventService;
import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaAccessible;
import org.a11y.utils.accprobe.accservice.event.AccessibilityModelEvent;
import org.a11y.utils.accprobe.actions.PauseEventDisplayAction;
import org.a11y.utils.accprobe.actions.ResumeEventDisplayAction;
import org.a11y.utils.accprobe.actions.StartEventCaptureAction;
import org.a11y.utils.accprobe.actions.StopEventCaptureAction;
import org.a11y.utils.accprobe.core.model.DefaultModelFactory;
import org.a11y.utils.accprobe.core.model.IModel;
import org.a11y.utils.accprobe.core.model.IRenderableModel;
import org.a11y.utils.accprobe.core.model.events.IModelEventListener;
import org.a11y.utils.accprobe.core.model.events.ModelEventType;
import org.a11y.utils.accprobe.dialogs.EventFilterDialog;
import org.a11y.utils.accprobe.dialogs.EventViewPreferencesDialog;
import org.a11y.utils.accprobe.providers.EventRecord;
import org.a11y.utils.accprobe.providers.EventTableContentProvider;
import org.a11y.utils.accprobe.providers.EventTableLabelProvider;
import org.a11y.utils.accprobe.providers.ExplorerViewNode;
import org.a11y.utils.accprobe.sorters.EventTableViewerSorter;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;


public class EventMonitorView extends AbstractViewPart
{

	public static final String ID = "org.a11y.utils.accprobe.views.EventMonitorView";
	public static final String TITLE = "Event Monitor";

	public static final String NAME_COLUMN = "Accessible Name";
	public static final String ROLE_COLUMN = "Accessible Role";
	public static final String TYPE_COLUMN = "Event Type";
	public static final String STATE_COLUMN = "Accessible State";
	public static final String TIMESTAMP_COLUMN = "Event Time";
	public static final String MISC_DATA_COLUMN = "Event Data";

	public static final String[] allColumns = { TYPE_COLUMN, TIMESTAMP_COLUMN, NAME_COLUMN, ROLE_COLUMN, STATE_COLUMN, MISC_DATA_COLUMN };
	public static final String[] defaultColumns = {MISC_DATA_COLUMN, TYPE_COLUMN, TIMESTAMP_COLUMN, NAME_COLUMN, ROLE_COLUMN };

	public static final String NEWLINE = "\n";
	private static boolean globalMonitoringEnabled =true;
	
	private Map<String, ColumnData> columnData = new HashMap<String, ColumnData>();
	private TableViewer viewer = null;
	private EventTableViewerSorter sorter = new EventTableViewerSorter();
	private Display display = null;

	// flags to control what happens to events as they occur
	private boolean captureEvents = false; // controls setting the event hook
	private boolean displayEvents = true; // controls whether events are pumped to the view
	private boolean logEvents = false; // controls if events are logged or not

	private FileWriter logFile = null;
	@SuppressWarnings("unused")
	private TableColumn[] tableColumns;
	private String[] columnProperties;
	private String[] selectedColumns;
	private Composite viewParent;
	private boolean listenerActive = false;
	private int contextFlag = MsaaAccessibilityEventService.DEFAULT_CONTEXT_FLAG;
	private ModelEventType[] eventTypes = null;
	private IModel currentModel = null;
	private Logger logger = Logger.getLogger(Activator.PLUGIN_ID);

	/*
	 * events is list of all the events (EventRecords) that have been collected
	 * during the session. They are what appears in the table
	 */
	private ArrayList<EventRecord> events = new ArrayList<EventRecord>();

	private ExplorerViewNode node = null;

	private IPartListener partListener = new PartAdapter() {

		public void partActivated(IWorkbenchPart part) {
			enableToolbarButton(StartEventCaptureAction.ID, !captureEvents);
			enableToolbarButton(StopEventCaptureAction.ID, captureEvents);
			if (captureEvents) {
				enableToolbarButton(ResumeEventDisplayAction.ID, !displayEvents);
				enableToolbarButton(PauseEventDisplayAction.ID, displayEvents);
			} else {
				enableToolbarButton(ResumeEventDisplayAction.ID, captureEvents);
				enableToolbarButton(PauseEventDisplayAction.ID, captureEvents);
			}
		}
	};

	public EventMonitorView() {
		super();
		initializeColumnData(columnData);
		getUserPreferences();
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	public void setContextFlag (int flag) {
		contextFlag = flag;
	}

	public void createPartControl(Composite parent) {
		viewParent = parent;
		display = PlatformUI.getWorkbench().getDisplay();
		try {
			viewer = createEventTableView(parent);
			initializePreferences();
		} catch (Exception e) {
			e.printStackTrace();
		}
		getSite().getPage().addPartListener(partListener);
	}

	private TableViewer createEventTableView(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(1, false);
		comp.setLayout(gl);
		Composite tcomp = new Composite(comp, SWT.NONE);
		GridData gd2 = new GridData(GridData.FILL_BOTH);
		gd2.grabExcessHorizontalSpace = true;
		gd2.grabExcessVerticalSpace = true;
		tcomp.setLayoutData(gd2);
		TableViewer tv = createEventTableViewer(tcomp);
		updateEventTable(tv, tv.getInput());
		Table t = tv.getTable();
		sizeEventTableViewerTable(t, parent);
		return tv;
	}

	private TableViewer createEventTableViewer(Composite parent) {
		Table table = createEventTable(parent);
		parent.addControlListener(new ViewerControlAdapter(table));
		createEventTableColumns(table);
		TableViewer tv = new TableViewer(table);
		configureEventTableViewer(tv);
		createEventTableViewerProviders(tv);
		tv.setSorter(sorter);
		return tv;
	}

	private void updateEventTable(TableViewer tv, Object input) {
		if (tv != null) {
			tv.setInput(input);
			tv.setSorter(sorter);
		}
	}

	private void sizeEventTableViewerTable(Table t, Composite parent) {
		Point p = t.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		Display d = parent.getDisplay();
		Rectangle r = d.getBounds();
		Point s = new Point(Math.min(r.width - 400, Math.max(p.x, 600)), Math
				.min(r.height - 200, Math.max(p.y, 200)));
		t.setSize(s);
		parent.layout();
	}

	private Table createEventTable(Composite parent) {
		GridData ttgd = new GridData(GridData.FILL_BOTH
				| GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		Table t = new Table(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE
				| SWT.FULL_SELECTION);
		GuiUtils
				.configureControl(t, "reportTable", ttgd,
						"Displays report records in a table.  Select a record for more information.");
		t.setHeaderVisible(true);
		t.setLinesVisible(true);
		return t;
	}

	private void createEventTableColumns(Table t) {
		List<TableColumn> columns = new ArrayList<TableColumn>();
		List<String> props = new ArrayList<String>();
		for (int i = 0; i < selectedColumns.length; i++) {
			ColumnData cd = (ColumnData) columnData.get(selectedColumns[i]);
			if (cd != null) {
				TableColumn col = new TableColumn(t, SWT.LEFT);
				col.setText(cd.getTitle());
				col.setWidth(cd.getWidth());
				col.setResizable(true);
				col.setMoveable(true);
				final int sort = cd.getSortProperty();
				col.addSelectionListener(new SelectionAdapter() {

					public void widgetSelected(SelectionEvent e) {
						sorter.setSortingField(sort);
						viewer.refresh();
					}
				});
				columns.add(col);
				props.add(new String(cd.getColumnProperty()));
			}
		}
		tableColumns = (TableColumn[]) columns.toArray(new TableColumn[0]);
		columnProperties = (String[]) props.toArray(new String[0]);
	}

	private void configureEventTableViewer(TableViewer tv) {
		tv.setColumnProperties(columnProperties);
	}

	private void createEventTableViewerProviders(TableViewer tv) {
		tv.setContentProvider(new EventTableContentProvider(this));
		tv.setLabelProvider(new EventTableLabelProvider(tv.getTable()));
	}

	private void initializeColumnData(Map<String, ColumnData> columnData) {
		columnData.put(TYPE_COLUMN, new ColumnData(TYPE_COLUMN, 150, "type",
				EventTableViewerSorter.TYPE));
		columnData.put(TIMESTAMP_COLUMN, new ColumnData(TIMESTAMP_COLUMN, 100,
				"timestamp", EventTableViewerSorter.TIMESTAMP));
		columnData.put(NAME_COLUMN, new ColumnData(NAME_COLUMN, 100, "name",
				EventTableViewerSorter.NAME));
		columnData.put(ROLE_COLUMN, new ColumnData(ROLE_COLUMN, 100, "role",
				EventTableViewerSorter.ROLE));
		columnData.put(STATE_COLUMN, new ColumnData(STATE_COLUMN, 100, "state",
				EventTableViewerSorter.STATE));
		columnData.put(MISC_DATA_COLUMN, new ColumnData(MISC_DATA_COLUMN, 200, "misc",
				EventTableViewerSorter.MISC));
	}

	public void setInput(Object input) {
		if (viewer != null) {
			viewer.setInput(input);
			node = (ExplorerViewNode) input;
		}
	}

	public void updateView() {
		updateEventTable(viewer, getNode());
	}

	public ExplorerViewNode getNode() {
		return node;
	}

	public static IModel findModel(Object input) {
		IModel model = null;
		if (input instanceof ExplorerViewNode) {
			Object o = ((ExplorerViewNode) input).getUnderlyingComponent();
			if (o instanceof MsaaAccessible
					&& IA2Accessible.isIA2Accessible((MsaaAccessible) o)) {
				model = DefaultModelFactory.getInstance().resolveModel(
						IA2GuiModel.IA2_MODEL);
			} else {
				model = DefaultModelFactory.getInstance().resolveModel(
						o.getClass());
			}
		}
		return model;
	}

	private IModelEventListener eventListener = new IModelEventListener() {
		public void handleEvent(EventObject event) {
			if(isActionSuspended()){
				return;
			}
			final EventObject e = event;
			if (display != null) {
				display.asyncExec(new Runnable() {
					public void run() {
						if (e instanceof AccessibilityModelEvent) {
							AccessibilityModelEvent amv = (AccessibilityModelEvent) e;
							EventRecord er = new EventRecord();
							er.setName(amv.getAccessibleName());
							er.setRole(amv.getAccessibleRole());
							er.setState(amv.getAccessibleState());
							er.setType(amv.getEventType());
							er.setTimestamp(amv.getTimeMillis());
							er.setMiscData(amv.getMiscData());
							events.add(er);
							if (displayEvents) {
								viewer.refresh();
								// viewer.setSelection(new
								// StructuredSelection(er), true);
							}
							if (logEvents && logFile != null) {
								try {
									logFile.write(er.toString() + NEWLINE);
								} catch (IOException e1) {
									logger.log(Level.WARNING, e1.getMessage(), e1);
								}
							}
						}
					}
				});
			}
		}
	};

	public ArrayList<EventRecord> getEvents() {
		return events;
	}

	public void clearEventsList() {
		events.clear();
		viewer.refresh();
	}

	private void initializePreferences() {
		IDialogSettings settings = Activator.getDefault().getDialogSettings()
				.getSection(EventFilterDialog.ID);
		if (settings == null) {
			settings = Activator.getDefault().getDialogSettings()
					.addNewSection(EventFilterDialog.ID);
			settings.put(EventFilterDialog.EVENTS_KEY,
					EventFilterDialog.defaultEventTypes);
		}
	}

	/**
	 * 
	 */
	private void getUserPreferences() {
		IDialogSettings settings = Activator.getDefault().getDialogSettings()
				.getSection(EventViewPreferencesDialog.ID);
		if (settings == null) { // none saved or get failed
			selectedColumns = defaultColumns;
		} else {
			List<String> cols = new ArrayList<String>();
			for (int i = 0; i < allColumns.length; i++) {
				if (settings.getBoolean(allColumns[i])) {
					cols.add(allColumns[i]);
				}
			}
			selectedColumns = (String[]) cols.toArray(new String[0]);
		}
	}

	public void viewPreferencesChanged() {
		getUserPreferences();
		Composite c = viewParent;
		c.setEnabled(false);
		// save current setting of captureEvents
		// temporarily turn off processing while we re-arrange the table
		boolean saveProcessEventsState = captureEvents;
		setCaptureEvents(false);
		Table table = viewer.getTable();
		TableColumn[] columns = table.getColumns();
		for (int i = 0; i < columns.length; i++) {
			columns[i].dispose();
		}
		createEventTableColumns(table);
		viewer.refresh();
		c.setEnabled(true);
		setCaptureEvents(saveProcessEventsState);
	}

	public boolean isCaptureEvents() {
		return this.captureEvents;
	}

	public void setCaptureEvents(boolean captureEvents) {
		this.captureEvents = captureEvents;
	}

	public boolean isDisplayEvents() {
		return displayEvents;
	}

	public void setDisplayEvents(boolean displayEvents) {
		this.displayEvents = displayEvents;
		if (displayEvents) {
			viewer.refresh();
		}
	}

	public boolean isLogEvents() {
		return this.logEvents;
	}

	public void setLogEvents(String filename, boolean logEvents) {
		this.logEvents = logEvents;
		if (logEvents) {
			if (filename != null) {
				try {
					logFile = new FileWriter(filename);
				} catch (IOException e) {
					logger.log(Level.WARNING, e.getMessage(), e);
				}
			} else {
				logger.log(Level.WARNING, "No log file specified");
			}
		} else {
			if (logFile != null) {
				try {
					logFile.flush();
					logFile.close();
				} catch (IOException e) {
					logger.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * registerEventListener this version will be called by the start/stop
	 * actions and depends on the viewer already having input.
	 */
	public void registerEventListener() {
		registerEventListener(viewer.getInput());
	}

	/**
	 * registerEventListener
	 * 
	 * @param newInput
	 * 
	 */
	public void registerEventListener(Object newInput) {
		if (newInput != null) {
			IRenderableModel newModel = (IRenderableModel) DefaultModelFactory.getInstance().resolveModel(
					IA2GuiModel.IA2_MODEL);
			currentModel = newModel;
			eventTypes = newModel.getModelEventTypes(null);
			
			IDialogSettings settings = Activator.getDefault()
					.getDialogSettings().getSection(EventFilterDialog.ID);
			if (settings != null) {
				String[] events = settings.getArray(EventFilterDialog.EVENTS_KEY);
				ArrayList<ModelEventType> newEvents = new ArrayList<ModelEventType>();
				for (int i = 0; i < eventTypes.length; i++) {
					for (int j = 0; j < events.length; j++) {
						if (events[j].equals(eventTypes[i].getEventName())) {
							newEvents.add(eventTypes[i]);
						}
					}
				}
				eventTypes = (ModelEventType[]) newEvents.toArray(new ModelEventType[newEvents.size()]);
			}
			
			if (newInput instanceof ExplorerViewNode && captureEvents) {
				node = (ExplorerViewNode) newInput;
				Object o = node.getUnderlyingComponent();
				int handle = 0;
				if (o instanceof IAccessibleElement) {
					if(!isGlobalMonitoringEnabled()){
						handle = ((MsaaAccessible) o).getWindowHandle();
					}
					IAccessibilityService service = null;
					try {
						String svcString = null;
						svcString = IA2AccessibilityService.IA2_ACCSERVICE_NAME;
						service = AccessibilityServiceManager.getInstance().getAccessibilityService(svcString);
						service.getWindowService().setActiveWindow(handle);
					} catch (AccessibilityServiceException e) {
						logger.log(Level.WARNING, e.getMessage(), e);
					}
				}
				newModel.registerModelEventListener(
					eventListener, eventTypes, new Object[] {new Integer(contextFlag | MsaaAccessibilityEventService.WINEVENT_SKIPOWNPROCESS)});
				listenerActive = true;
			}
		}
	}

	/**
	 * removeEventListener this version will be called by the start/stop actions
	 * and depends on the viewer already having input.
	 */
	public void removeEventListener() {
		removeEventListener(viewer.getInput());
	}

	/**
	 * removeEventListener
	 * 
	 * @param oldInput
	 */
	public void removeEventListener(Object oldInput) {
		if (oldInput != null) {
			IRenderableModel oldModel = (IRenderableModel) DefaultModelFactory.getInstance().resolveModel(
					IA2GuiModel.IA2_MODEL);
			if (oldModel != null && listenerActive) {
				oldModel
						.unregisterModelEventListener(eventListener, eventTypes);
				listenerActive = false;
			}
		}
	}

	@Override
	public void dispose() {
		getSite().getPage().removePartListener(partListener);
		if (currentModel != null && listenerActive) {
			((IRenderableModel) currentModel).unregisterModelEventListener(
					eventListener, eventTypes);
		}
		if (logEvents && logFile != null) {
			try {
				logFile.flush();
				logFile.close();
			} catch (IOException e) {
				logger.log(Level.WARNING, e.getMessage(), e);
			}
		}
		super.dispose();
	}

	private class ColumnData {

		private String title;

		private int width;

		private String columnProperty;

		private int sortProperty;

		public ColumnData(String title, int width, String columnProperty,
				int sortProperty) {
			super();
			this.title = title;
			this.width = width;
			this.columnProperty = columnProperty;
			this.sortProperty = sortProperty;
		}

		public String getTitle() {
			return title;
		}

		@SuppressWarnings("unused")
		public void setTitle(String title) {
			this.title = title;
		}

		public int getWidth() {
			return width;
		}

		@SuppressWarnings("unused")
		public void setWidth(int width) {
			this.width = width;
		}

		public int getSortProperty() {
			return sortProperty;
		}

		@SuppressWarnings("unused")
		public void setSortProperty(int sortProperty) {
			this.sortProperty = sortProperty;
		}

		public String getColumnProperty() {
			return columnProperty;
		}

		@SuppressWarnings("unused")
		public void setColumnProperty(String columnProperty) {
			this.columnProperty = columnProperty;
		}
	}
	
	public static  boolean isGlobalMonitoringEnabled() {
		return globalMonitoringEnabled ;
	}

	public void setGlobalMonitoringEnabled(boolean globalMonitoringEnabled) {
		EventMonitorView.globalMonitoringEnabled = globalMonitoringEnabled;
	}
	
	public void enableGlobalMonitoring(){
		setGlobalMonitoringEnabled(true);
	}

	public void disableGlobalMonitoring(){
		setGlobalMonitoringEnabled(false);
	}
	
}
