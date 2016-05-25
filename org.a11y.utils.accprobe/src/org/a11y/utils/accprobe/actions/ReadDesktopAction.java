/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.a11y.utils.accprobe.Activator;
import org.a11y.utils.accprobe.accservice.AccessibilityServiceException;
import org.a11y.utils.accprobe.accservice.AccessibilityServiceManager;
import org.a11y.utils.accprobe.accservice.IAccessibilityService;
import org.a11y.utils.accprobe.accservice.core.IAccessibleElement;
import org.a11y.utils.accprobe.accservice.core.win32.ia2.IA2AccessibilityService;
import org.a11y.utils.accprobe.accservice.core.win32.ia2.IA2Accessible;
import org.a11y.utils.accprobe.accservice.core.win32.msaa.MsaaAccessibilityService;
import org.a11y.utils.accprobe.core.model.InvalidComponentException;
import org.a11y.utils.accprobe.providers.ExplorerViewNode;
import org.a11y.utils.accprobe.views.AbstractViewPart;
import org.a11y.utils.accprobe.views.ExplorerView;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;



/**
 * @see IWorkbenchWindowActionDelegate
 */
public class ReadDesktopAction extends AbstractAction
{
	
	/**
	 * The constructor.
	 */
	public void run(IAction action) {
		this.action = action;
		
		ExplorerView iv = null;
		List<ExplorerViewNode> windows = null;
		view = AbstractViewPart.findView(ExplorerView.ID);
		if (view != null && view instanceof ExplorerView) {
			iv = (ExplorerView) view;
			windows = readDesktop();
			iv.setInput(windows, false);
		}
	}

	public static List<ExplorerViewNode> readDesktop() {
		IAccessibilityService accService = null;
		IAccessibilityService ia2AccService = null;
		
		//create MSAA/IA2 Accessibility services
		try {
			accService = AccessibilityServiceManager.getInstance()
					.getAccessibilityService(MsaaAccessibilityService.MSAA_ACCSERVICE_NAME);
			ia2AccService = AccessibilityServiceManager.getInstance()
			.getAccessibilityService(IA2AccessibilityService.IA2_ACCSERVICE_NAME);
		} catch (AccessibilityServiceException e) {
			throw new RuntimeException("Error getting IAccessibilityService", e);
		}

		//Get Window handles
		Object[] windows = accService.getWindowService().getTopLevelWindows();
		List<IAccessibleElement> accElemList = new ArrayList<IAccessibleElement>();
		int curPid = accService.getWindowService().getCurrentProcessId();
		
		//create accessible elements from window handles
		for (int i = 0; i < windows.length; i++) {
			try {
				int pid = accService.getWindowService().getProcessId(windows[i]);
				if(pid != curPid){
					IAccessibleElement element = accService.createAccessibleElement(windows[i], null);
					IAccessibleElement parent = element.getAccessibleParent();
					if(element.getAccessibleRole().equals("Window")){
						parent = element;
					}
					if (element != null){
						if(IA2Accessible.isIA2Accessible(element)){
							IAccessibleElement element2 = ia2AccService.createAccessibleElement(windows[i], null);
							accElemList.add(element2);
						}else if (parent!=null){
							if (parent.getAccessibleName() != null) {
							// MAS: problem in AccessibleNodePtr trying to fetch all 180+ children f the window
							// that contains the Start Button and Task tray; eliminate this window for now
							accElemList.add(parent);
							}
					}
				}
				}
			} catch (InvalidComponentException e) {
				Logger.getLogger(Activator.PLUGIN_ID).log(Level.WARNING, "Error creating IAccessibleElement", e);
			}
		}
		
		List<ExplorerViewNode> nodeList = new ArrayList<ExplorerViewNode>();
		if (accElemList.size() > 0) {
			Iterator<IAccessibleElement> iter = accElemList.iterator();
			while (iter.hasNext()) {
				IAccessibleElement accElem = (IAccessibleElement) iter.next();
				if (accElem != null) {
					ExplorerViewNode node = new ExplorerViewNode(accElem, null);
					if (node != null) {
						nodeList.add(node);
					}
				}
			}
		}
		
		return nodeList;
	}
}
