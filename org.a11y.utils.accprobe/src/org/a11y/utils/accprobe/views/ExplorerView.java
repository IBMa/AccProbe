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

package org.a11y.utils.accprobe.views;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.a11y.utils.accprobe.accservice.AccessibilityServiceException;
import org.a11y.utils.accprobe.accservice.AccessibilityServiceManager;
import org.a11y.utils.accprobe.accservice.IAccessibilityService;
import org.a11y.utils.accprobe.accservice.core.AccessibleConstants;
import org.a11y.utils.accprobe.accservice.core.IAccessibleElement;
import org.a11y.utils.accprobe.accservice.core.win32.ia2.IA2AccessibilityEventService;
import org.a11y.utils.accprobe.accservice.core.win32.ia2.IA2AccessibilityService;
import org.a11y.utils.accprobe.accservice.core.win32.ia2.IA2Accessible;
import org.a11y.utils.accprobe.accservice.core.win32.ia2.IA2GuiModel;
import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaAccessibilityEventService;
import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaAccessible;
import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaWindowService;
import org.a11y.utils.accprobe.accservice.event.AccessibilityModelEvent;
import org.a11y.utils.accprobe.actions.ReadDesktopAction;
import org.a11y.utils.accprobe.core.model.DefaultModelFactory;
import org.a11y.utils.accprobe.core.model.IModel;
import org.a11y.utils.accprobe.core.model.IRenderableModel;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;
import org.a11y.utils.accprobe.core.model.ModelPlugin;
import org.a11y.utils.accprobe.core.model.events.IModelEventListener;
import org.a11y.utils.accprobe.core.model.events.ModelEventType;
import org.a11y.utils.accprobe.core.model.locate.AbstractNodeLocator;
import org.a11y.utils.accprobe.providers.ExplorerViewContentProvider;
import org.a11y.utils.accprobe.providers.ExplorerViewLabelProvider;
import org.a11y.utils.accprobe.providers.ExplorerViewNode;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

public class ExplorerView extends AbstractViewPart {

	public static final String ID = "org.a11y.utils.accprobe.views.ExplorerView";
	public static final String TITLE = "Explorer";

	public static final int DEFAULT_CONTEXT_FLAG =
		MsaaAccessibilityEventService.WINEVENT_OUTOFCONTEXT | MsaaAccessibilityEventService.WINEVENT_SKIPOWNPROCESS;

	 private static final Pattern FIREFOX2_BUG_REMEDY = Pattern.compile(
	 "(/application" +
	 "\\[@accessibleName\\=\\'" + AbstractNodeLocator.PREDICATEVAL_CHARCLASS +
	 "*\\'\\])" +
	 "(/window" +
	 "\\[@accessibleName\\=\\'" + AbstractNodeLocator.PREDICATEVAL_CHARCLASS +
	 "*\\'\\])" +
	 "(/document" +
	 "\\[@accessibleName\\=\\'(" + AbstractNodeLocator.PREDICATEVAL_CHARCLASS
	 + "*)\\'\\])"
	 );

	private static final String KYBD_FOCUS_TRACKING_KEY = "kybdFocusTracking";
	private static final String MOUSE_CURSOR_TRACKING_KEY = "mouseCursorTracking";
	private static final String CARET_MOTION_TRACKING_KEY = "caretMotionTracking";
	private static final Map<String, Boolean> TRACKING_DEFAULTS = new HashMap<String, Boolean>();
	static {
		// should be synced with defaults for corresponding actions
		TRACKING_DEFAULTS.put(KYBD_FOCUS_TRACKING_KEY, true);
		TRACKING_DEFAULTS.put(MOUSE_CURSOR_TRACKING_KEY, true);
		TRACKING_DEFAULTS.put(CARET_MOTION_TRACKING_KEY, false);
	}

	private static Rectangle drawRef =null;
	private static int TIMER_DELAY = 500;
	private static int selIndex = 0;
	private static boolean globalTrackingEnabled = true;
	private static boolean enableExpansionWithTracking = false;

	private Tree tree;
	private TreeViewer viewer;
	private IBaseLabelProvider labelProvider;
	private IContentProvider contentProvider;
	private LinkedList<String> targetNodePaths;
	private ExplorerViewNode rootNode, contextNode;
	private Timer timer;
	private IRenderableModel model;
	private IAccessibilityService service;
	private Map<String, TrackingData> trackingDataMap = new HashMap<String, TrackingData>();
	private PropertiesView propView;
	
	private class TrackingData {
		private ModelEventType[] eventTypes;
		private IModelEventListener listener;
		private boolean active;

		public TrackingData(IModelEventListener listener, ModelEventType[] eventTypes, boolean active) {
			this.listener = listener;
			this.eventTypes = eventTypes;
			this.active = active;
		}

		public void setModelListener(IModelEventListener listener) {
			this.listener = listener;
		}

		public IModelEventListener getModelListener() {
			return this.listener;
		}

		public void setModelEventTypes(ModelEventType[] eventTypes) {
			this.eventTypes = eventTypes;
		}

		public ModelEventType[] getEventTypes() {
			return this.eventTypes;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

		public boolean isActive() {
			return this.active && this.listener != null && this.eventTypes != null;
		}
	}

	private IPartListener partListener = new PartAdapter() {
		public void partActivated(IWorkbenchPart part) {
			
		}
	};

	public ExplorerView() {
		super();

		// TODO remove once code is remerged; forces node-locator
		// host plug-in to load
		ModelPlugin.getDefault();

		labelProvider = new ExplorerViewLabelProvider(this);
		contentProvider = new ExplorerViewContentProvider();
	}

	public void initFind(Object[] nodes, ExplorerViewNode contextNode) {
		targetNodePaths = nodes == null ? null : new LinkedList<String>();
		if (nodes != null && nodes.length > 0 && contextNode != null) {
			IModel model = DefaultModelFactory.getInstance().resolveModel(contextNode.getModelType());
			for (int n = 0; n < nodes.length; ++n) {
				// MAS: work-around bc contextNode is created on UI thread
				// whereas obj was created on this job's thread
				String objPath;
				try {
					objPath = model.getNodeLocator().locate(nodes[n], null);
					targetNodePaths.add(objPath);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
		this.contextNode = contextNode;
	}

	public boolean selectNextNode() {
		boolean result = false;

		if (contextNode != null && !targetNodePaths.isEmpty()) {
			getSite().getShell().getDisplay().syncExec(new Runnable() {
				public void run() {
					IModel model = DefaultModelFactory.getInstance()
							.resolveModel(contextNode.getModelType());
					String ctxPath = model.getNodeLocator()
						.locate(contextNode.getUnderlyingComponent(), null);
					ctxPath = checkForFirefox(ctxPath);
					//System.err.println("ctx:"+ctxPath);
					String objPath = (String) targetNodePaths.removeFirst();
					objPath = checkForFirefox(objPath);
					//System.err.println("obj:"+objPath);
					String selectPath = objPath.substring(ctxPath.lastIndexOf("]/") + 1);
					//System.err.println("selectPath:"+selectPath);
					selectItemFromPath(selectPath);
					setFocus();
				}
			});
			result = true;
		}

		return result;
	}

	public void createPartControl(Composite parent) {
		tree = new Tree(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		viewer = new TreeViewer(tree);
		viewer.setUseHashlookup(true);
		viewer.setContentProvider(getContentProvider());
		viewer.setLabelProvider(getLabelProvider());

		List<ExplorerViewNode> windows = ReadDesktopAction.readDesktop();
		viewer.setInput(windows);
		createContextMenu(viewer);
	// initialize tracking data objects for each tracking type in TRACKING_DEFAULTS
		initTracking();
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if(isActionSuspended()){
					return;
				}
				IStructuredSelection currentSelection = (IStructuredSelection) event.getSelection();
				if (currentSelection.size() == 1) {
					Object selected = currentSelection.getFirstElement();
					if (selected instanceof ExplorerViewNode) {
						ExplorerViewNode node = (ExplorerViewNode) selected;
						ExplorerViewNode currentRootNode = rootNode;
						
						if (node.getParent() == null) {
							// selection is a top-level node
							selIndex = tree.indexOf(tree.getSelection()[0]);
							if (rootNode == null) {
								rootNode = node.getRoot();
							} else if (rootNode != node.getRoot()) {
								rootNode = node.getRoot();
							}

							if(currentRootNode != rootNode){
								
								resetAllListeners();						
								// update event monitor source
								setViewerInput((ExplorerViewNode) rootNode, EventMonitorView.ID);
							}
						} else {
							// not a top-level node
							TreeItem rootItem = tree.getSelection()[0];
							while (rootItem.getParentItem() != null) {
								rootItem = rootItem.getParentItem();
							}
							selIndex = tree.indexOf(rootItem);
							if (rootNode != node.getRoot()) {
								rootNode = node.getRoot();
							}
						}
						
						highlightRectangle(node);
					}

					setViewerInput((ExplorerViewNode) selected,PropertiesView.ID);
				} else if (currentSelection.size() == 0) {
					setViewerInput(null, PropertiesView.ID);
				}
			}
		});

		/*
		 * the following line forces the activation of the Event view so that
		 * the toolbar suspend/resume buttons are properly enabled
		 */
		AbstractViewPart.activateView(EventMonitorView.ID);
		AbstractViewPart.activateView(ExplorerView.ID);
		getSite().getPage().addPartListener(partListener);

		TreeItem node =  viewer.getTree().getItem(0);
		viewer.setSelection(new StructuredSelection(node), true);
	}

	protected void highlightRectangle(ExplorerViewNode node) {
		if(isActionSuspended()){
			return;
		}

		if (MsaaAccessible.isHighlightEnabled() && node.getUnderlyingComponent() instanceof MsaaAccessible) {
			MsaaAccessible acc = (MsaaAccessible) node.getUnderlyingComponent();
			//if(drawRef!=null)
				//acc.eraseRectangle(drawRef);
			//else
				//MsaaAccessible.eraseDesktop();
			
			try {
				Rectangle rect = acc.getAccessibleLocation();
				if(!rect.equals(drawRef)){
					//acc.eraseRectangle(drawRef);
					MsaaAccessible.eraseDesktop();
				}
				if (acc.drawRectangle(rect)) {
					drawRef = rect;
				}
			} catch (InvalidComponentException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void dispose() {
		viewer = null;
		tree = null;
		removeAllListeners();
		getSite().getPage().removePartListener(partListener);
		MsaaAccessible.eraseDesktop();
		super.dispose();
	}

	public ExplorerViewNode getSelectedNode() {
		IStructuredSelection currentSelection = (IStructuredSelection) viewer.getSelection();
		ExplorerViewNode node = null;

		if (currentSelection.size() == 1) {
			Object selected = currentSelection.getFirstElement();
			if (selected instanceof ExplorerViewNode) {
				node = (ExplorerViewNode) selected;
			}
		}

		return node;
	}

	public void setSelectedNode(ExplorerViewNode node, boolean reveal) {
		if (node != null) {
			viewer.setSelection(new StructuredSelection(node), reveal);
		}
	}

	public void selectItemFromPath(String xpath) {
		TreeItem selected = tree.getItem(selIndex);
		Matcher matcher = AbstractNodeLocator.SEGMENT_PATTERN.matcher(xpath);
		while (matcher.find()) {
			String segment = xpath.substring(matcher.start() + 1, matcher.end());
			viewer.setExpandedState(selected.getData(), true);
			if (matcher.start() == 0 && segment.equals(selected.getText())) {
				continue;
			}

			int childCount = selected.getItemCount();
			int index = childCount == 1 ? 0 : -1;
			for (int c = 0; index == -1 && c < childCount; ++c) {
				if (selected.getItem(c).getText().equals(segment)) {
					index = c;
				}
			}
			if (index > -1) {
				selected = selected.getItem(index);
			}
		}
		viewer.getTree().setSelection(new TreeItem[] { selected });
		setSelectedNode((ExplorerViewNode) selected.getData(), true);
	}

	@SuppressWarnings("unchecked")
	public boolean selectNodeFromPid(int pid){
		boolean res = false;
		ExplorerViewNode selected =null;
		Object obj = viewer.getInput();
		if (obj instanceof ArrayList<?>){
			ArrayList<ExplorerViewNode> lst = (ArrayList<ExplorerViewNode>) obj;
			for(int i=0; i< lst.size()&& !res; i++){				
				MsaaAccessible macc = (MsaaAccessible)(lst.get(i)).getUnderlyingComponent();
				if(macc.getPid()==pid){
					res = true;
					selected = lst.get(i);
				}
			}
		}
		if(selected!=null && res){
			setSelectedNode(selected, true);
		}
		return res;
	}
	public TreeViewer getTreeViewer() {
		return viewer;
	}

	protected String getMessageTitle() {
		return TITLE;
	}

	protected IBaseLabelProvider getLabelProvider() {
		return labelProvider;
	}

	protected IContentProvider getContentProvider() {
		return contentProvider;
	}

	public void setInput(Object c) {
		setInput(c, false);
		selIndex = tree.indexOf(tree.getSelection()[0]);
	}

	public void setInput(Object c, boolean expand) {
		if (viewer != null) {
			tree.setRedraw(false);
			viewer.setInput(c);
			if (expand) {
				viewer.expandAll();
			}
			tree.setRedraw(true);
		}
	}

	public void setFocus() {
		if (tree != null) {
			tree.setFocus();
		}
	}

	protected void setViewerInput(final Object input, final String viewId) {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				AbstractViewPart view = (AbstractViewPart) findView(viewId);
				if (view != null) {
					view.setInput(input);
				}
			}
		});
	}

	private IModelEventListener kbEventListener = new IModelEventListener() {
		public void handleEvent(EventObject event) {
			final EventObject e = event;
			if (e instanceof AccessibilityModelEvent) {
				AccessibilityModelEvent amv = (AccessibilityModelEvent) e;
				if (amv.getSource() instanceof IAccessibleElement
						&& amv.getEventType().equals("EVENT_OBJECT_FOCUS")
						&& isActive(KYBD_FOCUS_TRACKING_KEY)) {
					processTrackingEvents(amv);
				}
			}
		}
	};

	private IModelEventListener mouseEventListener = new IModelEventListener() {
		public void handleEvent(EventObject event) {
			final EventObject e = event;
			if (e instanceof AccessibilityModelEvent) {
				AccessibilityModelEvent amv = (AccessibilityModelEvent) e;
				if (amv.getSource() instanceof IAccessibleElement
						&& amv.getEventType().equals("EVENT_OBJECT_LOCATIONCHANGE")
						&& amv.getAccessibleRole().equals(AccessibleConstants.ROLE_CURSOR)
						&& isActive(MOUSE_CURSOR_TRACKING_KEY)) {
					processTrackingEvents(amv);
				}
			}
		}
	};

	private IModelEventListener caretEventListener = new IModelEventListener() {
		public void handleEvent(EventObject event) {
			final EventObject e = event;
			if (e instanceof AccessibilityModelEvent) {
				AccessibilityModelEvent amv = (AccessibilityModelEvent) e;
				if (amv.getSource() instanceof IAccessibleElement
						&&(	(amv.getEventType().equals("EVENT_OBJECT_LOCATIONCHANGE")
							&& amv.getAccessibleRole().equals(AccessibleConstants.ROLE_CARET))
							||
							(amv.getEventType().equals("IA2_EVENT_TEXT_CARET_MOVED") 
							)
						  )
						&& isActive(CARET_MOTION_TRACKING_KEY)) {
					processTrackingEvents(amv);
				}
			}
		}
	};

	private void showNode(final IAccessibleElement source) throws AccessibilityServiceException, InvalidComponentException {
		initFind(new Object[] { source }, rootNode);
		selectNextNode();
	}

	protected void processTrackingEvents(final AccessibilityModelEvent amv) {
		if(isActionSuspended()){
			return;
		}
		propView = (PropertiesView) findView(PropertiesView.ID);
		final IAccessibleElement source = (IAccessibleElement) amv.getSource();
		final org.eclipse.swt.graphics.Rectangle rect = getSite().getShell().getBounds();
		TimerTask task = new TimerTask() {
			public void run() {
					final Point loc = MsaaWindowService.getCurrentCursorLocation();
					// check if cursor is on the AccProbe window
					
						getSite().getShell().getDisplay().syncExec(new Runnable() {
							public void run() {
								try {
									if (source instanceof IAccessibleElement
										&& amv.getEventType().equals("EVENT_OBJECT_FOCUS")
										&& isActive(KYBD_FOCUS_TRACKING_KEY)){
										if (source ==null ||
												source.getAccessibleRole() ==null
												|| (source.getAccessibleAction()!=null && source.getAccessibleAction().equals("Switch")&& source.getAccessibleRole().equals("listItem"))
												|| source.getAccessibleRole().equals(AccessibleConstants.ROLE_FRAME)
												|| source.getAccessibleRole().equals(AccessibleConstants.ROLE_WINDOW)
												|| source.getAccessibleRole().equals(AccessibleConstants.ROLE_CLIENT_AREA)
												|| source.getAccessibleRole().equals(AccessibleConstants.ROLE_PANE)
												|| source.getAccessibleRole().equals(AccessibleConstants.ROLE_APPLICATION)
												|| source.getAccessibleName() == null)  {
											return;
										}else {
											ExplorerViewNode dummyNode = new ExplorerViewNode(source, null);
											if(propView !=null){
												propView.setInput(dummyNode);
												highlightRectangle(dummyNode);
											}
											if(isGlobalTrackingEnabled() && isExpansionEnabled()){
												int pid  = ((MsaaAccessible)source).getPid();
												int selPid = ((MsaaAccessible)getSelectedNode().getUnderlyingComponent()).getPid();
												if( (pid == selPid) || (selectNodeFromPid(pid))){
													showNode(source);
												}

											}
											else if (isExpansionEnabled()){
												showNode(source);
											}
										}
									}else if (amv.getEventType().equals("EVENT_OBJECT_LOCATIONCHANGE")
											&& amv.getAccessibleRole().equals(AccessibleConstants.ROLE_CURSOR)
											&& isActive(MOUSE_CURSOR_TRACKING_KEY)){
										if (!rect.contains(loc.x, loc.y)) {
											MsaaAccessible acc = new MsaaAccessible(loc);
											IAccessibleElement acc2 = acc.testAndConvertToIA2(acc);
											((MsaaAccessible)acc2).setHowFound("At Point:" + loc.toString()+ "; hwnd="+ acc.getWindowHandleAsHex());
											ExplorerViewNode dummyNode = new ExplorerViewNode(acc2, null);
											if(propView !=null){
												propView.setInput(dummyNode);
												highlightRectangle(dummyNode);
											}
											if(isGlobalTrackingEnabled() && isExpansionEnabled()){
						
												int pid = acc.getPid();
												int selPid = ((MsaaAccessible) getSelectedNode().getUnderlyingComponent()).getPid();
												if( (pid == selPid) || (selectNodeFromPid(pid))){
													showNode(acc2);
												}
								
											}
											else if (isExpansionEnabled()){
												showNode(acc2);
											}
										}
									}else if (amv.getEventType().equals("IA2_EVENT_TEXT_CARET_MOVED"))
									{	
										ExplorerViewNode dummyNode = new ExplorerViewNode(amv.getSource(), null);
										if(propView !=null){
											propView.setInput(dummyNode);
											highlightRectangle(dummyNode);
										}	
										if (isGlobalTrackingEnabled() && isExpansionEnabled()) {
											int pid  = ((MsaaAccessible)source).getPid();
											int selPid = ((MsaaAccessible)getSelectedNode().getUnderlyingComponent()).getPid();
											if( (pid == selPid) || (selectNodeFromPid(pid))){
												showNode(source);
											}
											
										}else if(isExpansionEnabled()){
											showNode(source);
										}
										if(source instanceof IA2Accessible){
											expandProperty("IAccessibleText");
										}
									}
									
										
								}catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					
				}
		};
	
		if (timer == null) {
			timer = new Timer();
			timer.schedule(task, TIMER_DELAY);
		} else {
			timer.cancel();
			timer = new Timer();
			timer.schedule(task, TIMER_DELAY);
		}
	}

	protected void expandProperty(String fieldName) {
		PropertiesView view = (PropertiesView) findView(PropertiesView.ID);
		view.setFocus();
		if (view != null) {
			Tree propTree = view.getTreeViewer().getTree();
			TreeItem[] items = propTree.getItems();
			boolean found = false;
			TreeItem item = null;
			for (int i = 0; i < items.length && !found; i++) {
				if (items[i].getText().equals(fieldName)) {
					found = true;
					item = items[i];
				}
			}
			if(!found){
				//look in 2nd level children
				for (int i = 0; i < items.length && !found; i++) {
					for (int j = 0; j < items[i].getItemCount() && !found; j++) {
						TreeItem citem = items[i].getItem(j);

						if (citem.getText().equals(fieldName)) {
							found = true;
							item = citem;
						}
					}
				}
			}
			
			if (found && item != null) {
				propTree.setSelection(item);
				if (item.getData() != null)
					view.getTreeViewer().setExpandedState(item.getData(), true);
			}
		}
	}

	public void initForCurrentSelection () {		
		model = (IRenderableModel) DefaultModelFactory.getInstance()
		.resolveModel(IA2GuiModel.IA2_MODEL);
		try {
			service = AccessibilityServiceManager.getInstance().getAccessibilityService(IA2AccessibilityService.IA2_ACCSERVICE_NAME);
			if(isGlobalTrackingEnabled()){
				service.getWindowService().setActiveWindow(new Integer(0));;
			}else{
				Object sel = getSelectedNode().getUnderlyingComponent();
				service.getWindowService().setActiveWindow(((MsaaAccessible) sel).getWindowHandle());
			}
		} catch (AccessibilityServiceException e) {
			e.printStackTrace();
		}
	}

	public void removeAccessibilityEventListener(String key) {
		TrackingData trackData = (TrackingData) trackingDataMap.get(key);
		if (trackData != null && model != null) {
			if (trackData.isActive()) {
				model.unregisterModelEventListener(
						trackData.getModelListener(), trackData.getEventTypes());
				trackData.setActive(false);
			}
		}
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public void removeKeyboardEventListener() {
		removeAccessibilityEventListener(KYBD_FOCUS_TRACKING_KEY);
	}

	public void removeMouseEventListener() {
		removeAccessibilityEventListener(MOUSE_CURSOR_TRACKING_KEY);

	}

	public void removeCaretEventListener() {
		removeAccessibilityEventListener(CARET_MOTION_TRACKING_KEY);
	}

	private String checkForFirefox (String path) {
		Matcher ff2Matcher = FIREFOX2_BUG_REMEDY.matcher(path);
		String result = path;
		
		if (ff2Matcher.find()) {
			String frameSegment = ff2Matcher.group(1);
			//String windowSegment = ff2Matcher.group(2);
			String docSegment = ff2Matcher.group(3);
			String docName = ff2Matcher.group(4);
			StringBuffer sb2 = new StringBuffer();
			sb2.append(frameSegment);
			sb2.append("/pane[1]/propertyPage[1]");
			sb2.append("/browser, http://www.mozilla.org/keymaster/gatekeeper/there.is.only.xul");
			//sb.append("/internalFrame");
			sb2.append("[@accessibleName=");
			sb2.append('\'');
			sb2.append(docName);
			sb2.append("\']");
			sb2.append(docSegment);
			result = ff2Matcher.replaceFirst(sb2.toString());
		}
		return result;
	}

	public boolean registerKeyboardEventListener() {
		if(!isActive(CARET_MOTION_TRACKING_KEY)){
		 registerAccessibilityEventListener(MsaaAccessibilityEventService.EVENT_OBJECT_FOCUS, kbEventListener, KYBD_FOCUS_TRACKING_KEY);
		}else{
			MessageDialog.openError(
					getSite().getShell(), ExplorerView.TITLE,
					"Keyboard tracking cannot be turned on when Caret tracking is enabled. Please disable Caret tracking and try again.");
			return false;
		}
		return true;
	}

	public boolean registerMouseEventListener() {
		if(!isActive(CARET_MOTION_TRACKING_KEY)){
			registerAccessibilityEventListener(MsaaAccessibilityEventService.EVENT_OBJECT_LOCATIONCHANGE, mouseEventListener, MOUSE_CURSOR_TRACKING_KEY);
		}else{
			MessageDialog.openError(
					getSite().getShell(), ExplorerView.TITLE,
					"Mouse tracking cannot be turned on when Caret tracking is enabled. Please disable Caret tracking and try again.");
			return false;
		}
		return true;
	}

	public boolean registerCaretEventListener() {
		if(!isActive(KYBD_FOCUS_TRACKING_KEY)&& !isActive(MOUSE_CURSOR_TRACKING_KEY)){
			int mType = IA2AccessibilityEventService.IA2_EVENT_TEXT_CARET_MOVED;
			registerAccessibilityEventListener(mType, caretEventListener, CARET_MOTION_TRACKING_KEY);
		}else{
			MessageDialog.openError(
					getSite().getShell(), ExplorerView.TITLE,
					"Caret tracking cannot be turned on when Keyboard/Mouse tracking are enabled. Please disable Keyboard/Mouse tracking and try again.");
			return false;
		}
		return true;
	}

	public void registerAccessibilityEventListener(int accEventType, IModelEventListener accEventListener, String key) {
		initForCurrentSelection();

		TrackingData trackData = null;
		ModelEventType[] eventTypes = null;
		ModelEventType mType = model.getModelEventType(new Integer(accEventType));
		if (mType != null) {
			eventTypes = new ModelEventType[] { mType };
		}
		if (trackingDataMap.containsKey(key)) {
			trackData = (TrackingData) trackingDataMap.get(key);
			trackData.setModelListener(accEventListener);
			trackData.setModelEventTypes(eventTypes);
			trackData.setActive(true);
		}
		model.registerModelEventListener(
						accEventListener, eventTypes,
							new Object[] { new Integer(MsaaAccessibilityEventService.WINEVENT_OUTOFCONTEXT | MsaaAccessibilityEventService.WINEVENT_SKIPOWNPROCESS) });
	}

	public boolean isActive(String key) {
		boolean active = false;
		TrackingData tData = (TrackingData) trackingDataMap.get(key);
		if (tData != null) {
			active = tData.isActive();
		}
		return active;
	}
	
	private void initTracking () {
		IModelEventListener accEventListener = null;
		ModelEventType[] accEventTypes = null;
		TrackingData trackData = null;
		
		model = (IRenderableModel) DefaultModelFactory.getInstance()
		.resolveModel(IA2GuiModel.IA2_MODEL);
		for (String trackKey : TRACKING_DEFAULTS.keySet()) {
			if (trackKey.equals(KYBD_FOCUS_TRACKING_KEY)) {
				accEventListener = kbEventListener;
				accEventTypes = new ModelEventType[]
				    {model.getModelEventType(new Integer(MsaaAccessibilityEventService.EVENT_OBJECT_FOCUS))};
			} else if (trackKey.equals(MOUSE_CURSOR_TRACKING_KEY)) {
					accEventListener = mouseEventListener;
					accEventTypes = new ModelEventType[]
					    {model.getModelEventType(new Integer(MsaaAccessibilityEventService.EVENT_OBJECT_LOCATIONCHANGE))};
			} else if (trackKey.equals(CARET_MOTION_TRACKING_KEY)) {
						accEventListener = caretEventListener;
						accEventTypes = new ModelEventType[]
						    {model.getModelEventType(new Integer(MsaaAccessibilityEventService.EVENT_OBJECT_LOCATIONCHANGE))};
			}
			trackData = new TrackingData(accEventListener, accEventTypes, TRACKING_DEFAULTS.get(trackKey));
			trackingDataMap.put(trackKey, trackData);
		}
	}

	public static  boolean isGlobalTrackingEnabled() {
		return globalTrackingEnabled;
	}

	public void setGlobalTrackingEnabled(boolean globalTrackingEnabled) {
		ExplorerView.globalTrackingEnabled = globalTrackingEnabled;
	}
	
	public void enableGlobalTracking(){
		setGlobalTrackingEnabled(true);
		resetAllListeners();
	}

	public void disableGlobalTracking(){
		setGlobalTrackingEnabled(false);
		resetAllListeners();
	}
	
	public static boolean isExpansionEnabled() {
		return enableExpansionWithTracking;
	}
	
	public void setExpansion(boolean enableExpansion) {
		ExplorerView.enableExpansionWithTracking = enableExpansion;
	}
	public void enableExpansion(){
		setExpansion(true);		
	}

	public void disableExpansion(){
		setExpansion(false);
	}
	public void resetAllListeners(){
		// update tracking
		if (isActive(KYBD_FOCUS_TRACKING_KEY)) {
			removeKeyboardEventListener();
			registerKeyboardEventListener();
		}
		if (isActive(MOUSE_CURSOR_TRACKING_KEY)) {
			removeMouseEventListener();
			registerMouseEventListener();
		}
		if (isActive(CARET_MOTION_TRACKING_KEY)) {
			removeCaretEventListener();
			registerCaretEventListener();
		}
	}
	
	public void removeAllListeners(){
	
		if (isActive(KYBD_FOCUS_TRACKING_KEY)) {
			removeKeyboardEventListener();
		}
		if (isActive(MOUSE_CURSOR_TRACKING_KEY)) {
			removeMouseEventListener();
		}
		if (isActive(CARET_MOTION_TRACKING_KEY)) {
			removeCaretEventListener();
		}
	}
}
