/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation.
*
*
*
 *
 *
 * Contributors:
 *  IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.a11y.utils.accprobe.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;

import org.a11y.utils.accprobe.Activator;
import org.a11y.utils.accprobe.GuiUtils;
import org.a11y.utils.accprobe.views.PropertiesView;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

public class PropertiesSortDialog extends Dialog {

	public static final String DIALOG_TITLE = "Sort Properties";
	public static final String ID = "org.a11y.utils.accprobe.dialogs.PropertiesSortDialog";
	
	public static final int UP = -1;
	public static final int DOWN = +1;
	public static final String SORT_ORDER_KEY = ID + ".sortOrder";
	public static final String INDEX_DELIMITER = "#@#";
	
	private Button upButton, downButton;
	private ListViewer viewer;
	private PropertiesView propertiesView;
	private ArrayList<String> selectedProps = new ArrayList<String>();

	public PropertiesSortDialog(Shell parentShell, Object[] selected, PropertiesView propView) {
		super(parentShell);
		setShellStyle(getShellStyle()|SWT.RESIZE |SWT.MAX | SWT.MIN);
		propertiesView = propView;

		for (Object item : selected) {
			if(item instanceof String){
				selectedProps.add((String) item);
			}
			else if(item instanceof Entry){
				selectedProps.add( ((Entry)item).getKey().toString());
			}
		}
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(DIALOG_TITLE);
	}

	protected Control createDialogArea(Composite parent) {
		initializeDialogUnits(parent);
		Composite c = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(4, false);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		c.setLayoutData(gd);
		c.setLayout(layout);
		
		createPropertiesList(c);
		createButtonGroup(c);
		loadSettings();
		
		return c;
	}

	private void createPropertiesList(Composite parent) {
		Composite area = new Composite(parent, SWT.NONE);
		FillLayout layout = new FillLayout(SWT.HORIZONTAL);
		area.setLayout(layout);
		
		viewer = new ListViewer(area);
		viewer.setLabelProvider(new LabelProvider());
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(selectedProps.toArray());
	}

	private void loadSettings() {
		IDialogSettings settings = Activator.getDefault().getDialogSettings().getSection(ID);
		
		if (settings != null) {
			ArrayList<String> sortedProps = new ArrayList<String>();
			String[] prevSorted = settings.getArray(SORT_ORDER_KEY);
			int removed = 0;
			
			// populate array to obtain a size
			sortedProps.addAll(Arrays.asList(prevSorted));
			for (String entry : prevSorted) {
				int delimIndex = entry.indexOf(INDEX_DELIMITER);
				String property = entry.substring(0, delimIndex); 
				int order = Integer.parseInt(entry.substring(delimIndex + INDEX_DELIMITER.length()));
				if (selectedProps.contains(property)) {
					// selected properties includes saved property so add it to resulting list
					sortedProps.set(order - removed, property);
				} else {
					// passed-in selected properties does not contain saved property so remove it
					++removed;
				}
			}
			
			// for each remaining property in selected properties that was not
			// added in last phase, append to end of list
			for (String prop : selectedProps) {
				if (!sortedProps.contains(prop)) {
					sortedProps.add(prop);
				}
			}
			
			// finally, remove any element in sortedList with delimiter since it
			// represents a property that was previously saved but not now present in selected properties
			String[] props = (String[]) sortedProps.toArray(new String[sortedProps.size()]);
			for (String prop : props) {
				if (prop.indexOf(INDEX_DELIMITER) >= 0) {
					sortedProps.remove(prop);
				}
			}
			selectedProps = sortedProps;
			viewer.setInput(sortedProps.toArray());
		}
	}

	protected void okPressed() {
		saveSettings();
		super.okPressed();
	}

	private void saveSettings() {
		IDialogSettings settings = Activator.getDefault().getDialogSettings().getSection(ID);
		String[] items = viewer.getList().getItems();
		
		if (settings == null) {
			settings = Activator.getDefault().getDialogSettings().addNewSection(ID);
		}
		
		String[] value = new String[items.length];
		for (int i = 0; i < items.length; ++i) {
			value[i] = items[i] + INDEX_DELIMITER + i;
		}
		settings.put(SORT_ORDER_KEY, value);
		propertiesView.sortOrderChanged();
	}

	private void createButtonGroup(Composite parent) {
		Composite area = new Composite(parent, SWT.NONE);
		FillLayout layout = new FillLayout(SWT.VERTICAL);
		SelectionListener  listener;
		area.setLayout(layout);

		// Up Button
		upButton = new Button(area, SWT.PUSH);
		GuiUtils.configureControl(
				upButton, "Up", "Move selected item to a higher sort order", "&Up");
		listener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				doShiftItem(UP);
			}
		};
		upButton.addSelectionListener(listener);

		// Down button
		downButton = new Button(area, SWT.PUSH);
		GuiUtils.configureControl(
				downButton, "Down", "Move selected item to a lower sort order", "&Down");
		listener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				doShiftItem(DOWN);
			}
		};
		downButton.addSelectionListener(listener);
	}

	private void doShiftItem (int direction) {
		List list = viewer.getList();		
		int count = list.getItemCount();
		int index = list.getSelectionIndex();
		if((index==0 && direction == UP)|| (index==count-1 && direction ==DOWN)){
			return; //do not shift
		}else{
			reSortList(index, direction);
			list.setFocus();
			list.select(index + direction);
		}
	}
	private void reSortList(int index, int direction) {
		String saveItem = selectedProps.get(index);
		selectedProps.remove(index);
		selectedProps.add(index + direction, saveItem);
		viewer.setInput(selectedProps.toArray());
	}

}
