/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 
package org.a11y.utils.accprobe.dialogs;

import org.a11y.utils.accprobe.Activator;
import org.a11y.utils.accprobe.GuiUtils;
import org.a11y.utils.accprobe.providers.EventPreferencesLabelProvider;
import org.a11y.utils.accprobe.views.EventMonitorView;
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


public class EventViewPreferencesDialog extends Dialog {

	public static final String DIALOG_TITLE = "Choose Table Columns";

	public static final String ID = "org.a11y.utils.accprobe.dialogs.EventViewPreferencesDialog";

	public static final String COLUMN_NAMES_COLUMN_NAME = "Select table columns";

	public static final String EVENT_COLUMNS_KEY = "selectedEventColumns";

	private CheckboxTableViewer viewer = null;

	private String[] allColumns = EventMonitorView.allColumns;

	private String[] defaultColumns = EventMonitorView.defaultColumns;

	// buttons group
	protected Button selectAllButton = null;

	protected Button deselectAllButton = null;

	protected Button restoreDefaultsButton = null;

	public EventViewPreferencesDialog(Shell shell) {
		super(shell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		initializeDialogUnits(parent);
		Composite c = (Composite) super.createDialogArea(parent);
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		c.setLayout(layout);
		createColumnsTable(c);
		createButtonGroup(c);

		loadSettings();
		return c;
	}

	private void createColumnsTable(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		GridData gd2 = new GridData(GridData.FILL_BOTH);
		gd2.grabExcessHorizontalSpace = true;
		gd2.grabExcessVerticalSpace = true;
		c.setLayout(new GridLayout());
		c.setLayoutData(gd2);
		GridData ttgd = new GridData(GridData.FILL_BOTH
				| GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		Table table = new Table(c, SWT.CHECK);
		GuiUtils.configureControl(table, "columnsTable", ttgd,
				"Displays possible event view columns in a table.");
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		createTableColumns(table);
		viewer = new CheckboxTableViewer(table);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new EventPreferencesLabelProvider());
		viewer.setSorter(new ViewerSorter());
		viewer.setInput(EventMonitorView.allColumns);

	}

	private void createTableColumns(Table table) {
		TableColumn columnNames = new TableColumn(table, SWT.CENTER);
		columnNames.setText(COLUMN_NAMES_COLUMN_NAME);
		columnNames.setWidth(200);

	}

	private void createButtonGroup(Composite parent) {
		Composite area = new Composite(parent, SWT.NONE);
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		area.setLayout(layout);
		selectAllButton = new Button(area, SWT.PUSH);
		GuiUtils.configureControl(selectAllButton, "selectAll",
				"Select all events", "Select All");
		SelectionListener listener = new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				selectAllColumns(true);
			}
		};
		selectAllButton.addSelectionListener(listener);
		deselectAllButton = new Button(area, SWT.PUSH);
		deselectAllButton.setText("Deselect All");
		GuiUtils.configureControl(deselectAllButton, "deselectAll",
				"Deselect all events", "Deselect All");
		listener = new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				selectAllColumns(false);
			}
		};
		deselectAllButton.addSelectionListener(listener);

		restoreDefaultsButton = new Button(area, SWT.PUSH);
		restoreDefaultsButton.setText("Restore defaults");
		GuiUtils.configureControl(restoreDefaultsButton, "restoreDefaults",
				"Restore default selections", "Restore Defaults");
		listener = new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				setDefaults();
			}
		};
		restoreDefaultsButton.addSelectionListener(listener);
	}

	protected void selectAllColumns(boolean b) {
		viewer.setAllChecked(b);
	}

	protected void setDefaults() {
		// first clear them all
		viewer.setAllChecked(false);
		// then set them to the defaults
		for (int i = 0; i < allColumns.length; i++) {
			for (int j = 0; j < defaultColumns.length; j++) {
				if (defaultColumns[j].equals(allColumns[i])) {
					viewer.setChecked(allColumns[i], true);
				}
			}

		}
	}

	@Override
	protected void okPressed() {
		saveSettings();
		super.okPressed();
	}

	private void saveSettings() {
		IDialogSettings settings = Activator.getDefault().getDialogSettings()
				.getSection(ID);
		if (settings == null) {
			settings = Activator.getDefault().getDialogSettings()
					.addNewSection(ID);
		}
		for (int i =0; i<allColumns.length; i++){
			settings.put(allColumns[i], viewer.getChecked(allColumns[i]));
		}

	}

	protected void loadSettings() {
		IDialogSettings settings = Activator.getDefault().getDialogSettings().getSection(ID);
		if (settings == null) { // none saved or get failed
			setDefaults();
		} else {
			for (int i=0; i<allColumns.length; i++){
			 viewer.setChecked(allColumns[i], settings.getBoolean(allColumns[i]));
						}
		}

	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(DIALOG_TITLE);
	}

}
