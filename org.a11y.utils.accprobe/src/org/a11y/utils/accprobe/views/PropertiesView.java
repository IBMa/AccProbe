/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 
package org.a11y.utils.accprobe.views;

import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import org.a11y.utils.accprobe.Activator;
import org.a11y.utils.accprobe.GuiUtils;
import org.a11y.utils.accprobe.dialogs.MethodInvocationDialog;
import org.a11y.utils.accprobe.filters.PropertyFilter;
import org.a11y.utils.accprobe.providers.ExplorerViewNode;
import org.a11y.utils.accprobe.providers.PropertiesViewContentProvider;
import org.a11y.utils.accprobe.providers.PropertiesViewLabelProvider;
import org.a11y.utils.accprobe.sorters.PropertyTableViewerSorter;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;


public class PropertiesView extends AbstractViewPart
{
	
	public static final String TITLE = "Accessibility Properties";
	public static final String NAME_COLUMN = "Name";
	public static final String VALUE_COLUMN = "Value";
	public static final String ID = "org.a11y.utils.accprobe.views.PropertiesView";
	public static final String ENTER_PARAMETERS="<Double click to enter parameters..>";

	public static final Set<String> DEFAULT_EXPAND = new HashSet<String>();
	public static boolean FILTER_NULLS = false;
	static {
		//msaa defaults
		DEFAULT_EXPAND.add("accservice");
		DEFAULT_EXPAND.add("msaa");
		DEFAULT_EXPAND.add("ia2");
	}
	
	private TreeViewer treeViewer = null;
	private ExplorerViewNode node = null;
	private PropertyTableViewerSorter sorter = new PropertyTableViewerSorter();

	public PropertiesView() {
		super();
	}

	public TreeViewer getTreeViewer () {
		return treeViewer;
	}
	
	protected String getMessageTitle() {
		return TITLE;
	}

	public void setFocus() {
		treeViewer.getTree().setFocus();
	}

	public void createPartControl(Composite parent) {
		treeViewer = createPropertiesTreeView(parent);
		createContextMenu(treeViewer);
		loadSettings();

		treeViewer.addTreeListener(new ITreeViewerListener(){

			public void treeCollapsed(TreeExpansionEvent event) {
				if(event.getElement() instanceof Entry){
					IDialogSettings settings = Activator.getDefault().getDialogSettings().getSection(ID);
					String txt = ((Entry)event.getElement()).getKey().toString();
					if(settings!=null && DEFAULT_EXPAND.contains(txt)){
						settings.put(txt, false);
					}
				}
			}
			public void treeExpanded(TreeExpansionEvent event) {
				if(event.getElement() instanceof Entry){
					IDialogSettings settings = Activator.getDefault().getDialogSettings().getSection(ID);
					String txt = ((Entry)event.getElement()).getKey().toString();
					if(settings!=null && DEFAULT_EXPAND.contains(txt)){
						settings.put(txt, true);
					}
				}
			}
		}
		);

		treeViewer.addDoubleClickListener(new IDoubleClickListener(){

			public void doubleClick(DoubleClickEvent event) {
				// TODO Auto-generated method stub
				IStructuredSelection currentSelection = (IStructuredSelection) event.getSelection();
				if (currentSelection.size() == 1) {
					Object target =null;
					Object selected =  (Entry) currentSelection.getFirstElement();
					if(((Entry)selected).getValue().equals(ENTER_PARAMETERS)){
						Tree tree = treeViewer.getTree();
						TreeItem tItem = null;
						if (tree.getSelectionCount() != 0 && tree.getSelection()!=null){
							tItem = tree.getSelection()[0];
						}
						if( tItem.getParentItem()==null){
							//if no parent,then class = currently selected ExplorerviewNode
							target = getNode().getUnderlyingComponent();
						}else{
							//get the class of parent tree Item- such as IAccesibleText
							target = ((Entry)tItem.getParentItem().getData()).getValue();
						}
						String mName = (String) ((Entry)selected).getKey();
						new MethodInvocationDialog(getSite().getShell(),target, mName).open();
					}	
				}
			}

		});
	}

	

	private TreeViewer createPropertiesTreeView(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		main.setLayoutData(gd);
		
		TreeViewer tv = createPropertiesTreeViewer(main);
		updateTree(tv, tv.getInput());
		tv.setSorter(sorter);
		return tv;
	}

	private TreeViewer createPropertiesTreeViewer(Composite parent) {
		final Tree tree = createPropertiesTree(parent);
		TreeViewer tv = new TreeViewer(tree);
		configurePropertiesTreeViewer(tv);
		return tv;
	}

	/*
	 * Input to the viewer is assumed to be a ExplorerViewNode
	 */
	private void updateTree(TreeViewer tv, Object input) {
		if (tv != null) {
			tv.setInput(input);
			tv.setSorter(sorter);
		}
	}

	private Tree createPropertiesTree(Composite parent) {
		GridData ttgd = new GridData(
				GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		final Tree tree = new Tree(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
		GuiUtils.configureControl(tree, "propertiesTable", ttgd, "Displays GUI component properties in a table.");
		
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		parent.addControlListener(new ViewerControlAdapter(tree));
		createTreeColumns(tree);
		
		tree.getAccessible().addAccessibleListener(new AccessibleAdapter() {
			public void getName(AccessibleEvent e) {
			TreeItem[] sel = tree.getSelection();
			if (sel != null && sel.length == 1) {
				int index = tree.indexOf(sel[0]);
				if (index >= 0 && index < tree.getItemCount()) {
					TreeItem item = tree.getItem(index);
					e.result = item.getText(0) + " = " + item.getText(1);
				}
			}
		}
		});
		
		return tree;
	}

	private void createTreeColumns(Tree t) {
		TreeColumn nameCol = new TreeColumn(t, SWT.LEFT);
		nameCol.setText(NAME_COLUMN);
		nameCol.setWidth(150);
		nameCol.setResizable(true);
		
		TreeColumn valueCol = new TreeColumn(t, SWT.LEFT);
		valueCol.setText(VALUE_COLUMN);
		valueCol.setWidth(375);
		valueCol.setResizable(true);
	}

	private void configurePropertiesTreeViewer(TreeViewer tv) {
		tv.setColumnProperties(new String[] { NAME_COLUMN, VALUE_COLUMN });
		tv.addFilter(new PropertyFilter());
		tv.setContentProvider(new PropertiesViewContentProvider());
		tv.setLabelProvider(new PropertiesViewLabelProvider());
	}

	public void setInput(final Object input) {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				treeViewer.setInput(input);
				if (input instanceof ExplorerViewNode) {
					node = (ExplorerViewNode) input;
				}
			}
		});
		expandPropertyGroups();
	}
	public void expandPropertyGroups(){
		//	exapnded first level properties
		IDialogSettings settings = Activator.getDefault().getDialogSettings().getSection(ID);
		Tree propTree = treeViewer.getTree();
		TreeItem[] items = propTree.getItems();
		for (int i = 0; i < items.length ; i++) {
			if( (items[i]!=null)  && items[i].getData()!=null)
				if(settings.get(items[i].getText())!=null && settings.getBoolean(items[i].getText())){
					treeViewer.setExpandedState(items[i].getData(), true);
				}
		}
	}
	
	public void updateView() {
		treeViewer.setInput(treeViewer.getInput());
		expandPropertyGroups();
	}

	public ExplorerViewNode getNode() {
		return node;
	}
	
	public void sortOrderChanged() {
		if (sorter == null){
			sorter = new PropertyTableViewerSorter();
			treeViewer.setSorter(sorter);			
		} else {
			sorter.updateSortOrder();
		}
	}
	private void loadSettings() {
		IDialogSettings settings = Activator.getDefault().getDialogSettings().getSection(ID);
		if(settings==null){
			settings = Activator.getDefault().getDialogSettings().addNewSection(ID);
			for( String s: DEFAULT_EXPAND){
				settings.put(s, true);
			}
		}
	}
	
}
