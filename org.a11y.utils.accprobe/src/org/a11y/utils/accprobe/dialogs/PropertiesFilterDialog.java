/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.dialogs;

import java.util.Map.Entry;

import org.a11y.utils.accprobe.Activator;
import org.a11y.utils.accprobe.GuiUtils;
import org.a11y.utils.accprobe.accservice.core.IAccessibleElement;
import org.a11y.utils.accprobe.filters.PropertyFilter;
import org.a11y.utils.accprobe.providers.ExplorerViewNode;
import org.a11y.utils.accprobe.providers.PropertiesFilterContentProvider;
import org.a11y.utils.accprobe.providers.PropertiesFilterLabelProvider;
import org.a11y.utils.accprobe.providers.PropertiesViewContentProvider;
import org.a11y.utils.accprobe.views.PropertiesView;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;


public class PropertiesFilterDialog extends Dialog
{

	public static final String DIALOG_TITLE = "Properties Filter";
	public static final String ID = "org.a11y.utils.accprobe.dialogs.PropertiesFilterDialog";
	public static final String PROPERTY_COLUMN_NAME = "Select properties to display";
	public static final String CHECKED_STATE_KEY = "selectedProperties";

	protected ExplorerViewNode node = null;
	protected TreeItem[] properties;
	protected CheckboxTreeViewer viewer = null;

	//	 buttons group
	protected Button selectAllButton = null;
	protected Button deselectAllButton = null;
	protected Button restoreDefaultsButton = null;
	protected Button specifySortButton = null;
	protected Button nullFilterButton =null;
	
	private PropertiesView  propertiesView = null;
	

	public PropertiesFilterDialog (Shell parentShell, ExplorerViewNode node, PropertiesView propertiesView) {
		super(parentShell);
		setShellStyle(getShellStyle()|SWT.RESIZE |SWT.MAX | SWT.MIN);
		this.node = node;
		this.propertiesView = propertiesView;
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
		createPropertiesTree(c);
		createButtonGroup(c);
		loadSettings();
		return c;
	}

	/**
	 * @param parent
	 */
	private void createPropertiesTree (Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 20;
		layout.marginHeight = 50;
		c.setLayout(layout);
		Tree tree = new Tree(c, SWT.SINGLE | SWT.CHECK
				| SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 400;
		gd.widthHint = 300;
		tree.setLayoutData(gd);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		createTreeColumns(tree);
		viewer = new CheckboxTreeViewer(tree);
		viewer.setContentProvider(new PropertiesFilterContentProvider());
		viewer.setLabelProvider(new PropertiesFilterLabelProvider());
		viewer.setInput(node.getUnderlyingComponent());
		
		ICheckStateListener listener = new ICheckStateListener(){

			public void checkStateChanged(CheckStateChangedEvent event) {
				Object obj = event.getElement();
				String key = null;
				boolean res = event.getChecked();
				if( obj instanceof Entry){
					key = ((Entry)obj).getKey().toString();
				}

				if( obj instanceof Entry){
					if (((Entry)obj).getValue() instanceof IAccessibleElement) {
						viewer.setExpandedState(event.getElement(), res);
						properties = viewer.getTree().getItems();
						for( TreeItem t: properties){
							if(t.getData() instanceof Entry){
								String objKey = ((Entry)t.getData()).getKey().toString();
								if(objKey.equals(key)){
									for (int i=0; i< t.getItemCount();i++){
										viewer.setGrayed(t.getItem(i).getData(), !res);
										if(!res){
											viewer.setChecked(t.getItem(i).getData(), res);
										}
									}
								}
							}
						}
					}
				}
			}
		};
		
		viewer.addCheckStateListener(listener);
		viewer.setSorter(new ViewerSorter());
		properties = viewer.getTree().getItems();
		
	}
	private void createTreeColumns(Tree t) {
		TreeColumn nameCol = new TreeColumn(t, SWT.LEFT);
		nameCol.setText(PROPERTY_COLUMN_NAME);
		nameCol.setWidth(300);
		nameCol.setResizable(true);
	}
	private void createButtonGroup (Composite parent) {
		Composite area = new Composite(parent, SWT.NONE);
		SelectionListener  listener;
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		area.setLayout(layout);
		
		selectAllButton = new Button(area, SWT.PUSH);
		GuiUtils.configureControl(
			selectAllButton, "selectAll", "Select all properties", "&Select All");
		listener = new SelectionAdapter() {
			public void widgetSelected (SelectionEvent e) {
				selectAllProperties(true);
			}
		};
		selectAllButton.addSelectionListener(listener);
		
		deselectAllButton = new Button(area, SWT.PUSH);
		GuiUtils.configureControl(
			deselectAllButton, "deselectAll", "Deselect all properties", "&Deselect All");
		listener = new SelectionAdapter() {
			public void widgetSelected (SelectionEvent e) {
				selectAllProperties(false);
			}
		};
		deselectAllButton.addSelectionListener(listener);
		
		restoreDefaultsButton = new Button(area, SWT.PUSH);
		GuiUtils.configureControl(
				restoreDefaultsButton, "restoreDefaults", "Restore default selections", "&Restore Defaults");
		listener = new SelectionAdapter() {
			public void widgetSelected (SelectionEvent e) {
				IDialogSettings settings = getDefaultSettings();
				setViewerSettings(viewer.getTree().getItems(), settings);
			}
		};
		restoreDefaultsButton.addSelectionListener(listener);
		
		specifySortButton = new Button(area, SWT.PUSH);
		GuiUtils.configureControl(
				specifySortButton, "specifySort","Specify sort order for the selected properties", "S&ort order...");
		listener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				openSortDialog();
			}
		};
		specifySortButton.addSelectionListener(listener);

		
		Composite area1 = new Composite(parent, SWT.NONE);
		area1.setLayout(layout);
		nullFilterButton = new Button(area1, SWT.CHECK);
		nullFilterButton.setSelection(PropertiesView.FILTER_NULLS);
		GuiUtils.configureControl(
				nullFilterButton, "nullFilter","Filter null values for selected properties", 
				"&Filter properties with <null> values");
		listener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Button src  = (Button)e.getSource();
				if(src.getSelection()){
					PropertiesView.FILTER_NULLS =true;
				}else{
					PropertiesView.FILTER_NULLS =false;
				}
			}
		};
		nullFilterButton.addSelectionListener(listener);
		

	}

	protected void openSortDialog() {
		PropertiesSortDialog dialog = new PropertiesSortDialog(getShell(), viewer.getCheckedElements(), propertiesView);
		if (dialog.open() == IDialogConstants.OK_ID) {
			//dialog.getSortedList();
		}
	}

	protected void selectAllProperties (boolean b) {
		viewer.setAllChecked(b);
	}

	protected void okPressed () {
		saveSettings();
		super.okPressed();
	}
	
	protected IDialogSettings getDefaultSettings (){
		IDialogSettings settings = Activator.getDefault().getDialogSettings();
		for( String s: PropertyFilter.DEFAULT_INCLUDES){
			settings.put(s, true);
		}
		return settings;
	}

	protected void loadSettings () {
		IDialogSettings settings = Activator.getDefault().getDialogSettings().getSection(
			ID);
		if (settings == null) { // none saved or get failed
			settings = getDefaultSettings();
			
		}
		setViewerSettings( viewer.getTree().getItems(), settings);
	}
	
	protected void setViewerSettings(TreeItem[] t, IDialogSettings s){
		for (int i = 0; i < t.length; i++) {
			Entry e = (Entry) t[i].getData();
			String txt = e.getKey().toString();
			Object val = e.getValue();
			boolean res = s.getBoolean(txt);
			viewer.setChecked(e, res);
			if(res){	
				if (PropertyFilter.isPropertyGroup(e) ){
						viewer.setExpandedState(e, true);
					if(t[i].getItemCount() >0)
						setViewerSettings(t[i].getItems(), s);
				}
			}

		}
	}

	protected void saveSettings () {
		IDialogSettings settings = Activator.getDefault().getDialogSettings().getSection(ID);
		if (settings == null) {
			settings = Activator.getDefault().getDialogSettings().addNewSection(ID);
			for( String s: PropertyFilter.DEFAULT_INCLUDES){
				settings.put(s, true);
			}
		}
		properties = viewer.getTree().getItems();
		for (int i = 0; i < properties.length; i++) {
			Entry e = (Entry) properties[i].getData();
			String txt = e.getKey().toString();
			Object val = e.getValue();
			boolean res = viewer.getChecked(e);
			settings.put(txt, res);
			if( res && PropertyFilter.isPropertyGroup(e)){
				TreeItem[] ti = properties[i].getItems();
				for (int j = 0;j  < ti.length; j++) {
					 e = (Entry) ti[j].getData();
					 txt = e.getKey().toString();
					 settings.put(txt, viewer.getChecked(e));
				}
			}
		}
	}
	
	
}