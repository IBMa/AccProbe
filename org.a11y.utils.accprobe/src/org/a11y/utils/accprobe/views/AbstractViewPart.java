/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.views;

import java.awt.Container;

import org.a11y.utils.accprobe.Activator;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;


public abstract class AbstractViewPart extends ViewPart {



	protected Activator plugin = Activator.getDefault();
	public static boolean actionSuspended = false;


	public static boolean isActionSuspended() {
		return actionSuspended;
	}

	public static void setActionSuspended(boolean actionSuspensed) {
		AbstractViewPart.actionSuspended = actionSuspensed;
	}

	public void dispose() {
		super.dispose();
	}

	public static IViewPart findView(String id) {
		IViewPart result = null;
		IWorkbench wb = PlatformUI.getWorkbench();
		if (wb != null) {
			IWorkbenchWindow ww = wb.getActiveWorkbenchWindow();
			if (ww != null) {
				IWorkbenchPage wp = ww.getActivePage();
				if (wp != null) {
					result = wp.findView(id);
				}
			}
		}
		return result;
	}

	public static IViewPart activateView(String id) {
		IViewPart result = null;
		IWorkbench wb = PlatformUI.getWorkbench();
		if (wb != null) {
			IWorkbenchWindow ww = wb.getActiveWorkbenchWindow();
			if (ww != null) {
				IWorkbenchPage wp = ww.getActivePage();
				if (wp != null) {
					try {
						result = wp.showView(id);
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return result;
	}

	public void update(Object c) {
		if (c instanceof Control) {
			update((Composite) c);
		} else {
			update((Container) c);
		}
	}

	protected void sizeTable(Table t, Composite parent) {
		Point p = t.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		Display d = parent.getDisplay();
		Rectangle r = d.getBounds();
		Point s = new Point(
				Math.min(r.width - 400, Math.max(p.x, 600)),
				Math.min(r.height - 200, Math.max(p.y, 200)));
		t.setSize(s);
		parent.layout();
	}

	protected IPreferenceStore getPreferenceStore() {
		if (plugin == null) {
			plugin = Activator.getDefault();
		}
		IPreferenceStore store = plugin.getPreferenceStore();
		return store;
	}

	public IStatusLineManager getStatusLineManager() {
		return ((IViewSite) getSite()).getActionBars().getStatusLineManager();
	}

	public void displayStatusMessage(String message) {
		IStatusLineManager slm = getStatusLineManager();
		slm.setMessage(message);
	}

	public void displayStatusMessage(String message, Image image) {
		IStatusLineManager slm = getStatusLineManager();
		slm.setMessage(image, message);
	}

	public void displayErrorMessage(String message) {
		IStatusLineManager slm = getStatusLineManager();
		slm.setErrorMessage(message);
	}

	public void displayErrorMessage(String message, Image image) {
		IStatusLineManager slm = getStatusLineManager();
		slm.setErrorMessage(image, message);
	}

	public void clearMessages() {
		displayErrorMessage(null);
		displayStatusMessage(null);
	}

	protected abstract class BaseSelectionAdapter extends SelectionAdapter {

	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	}

	public void enableToolbarButton(final String toolItemId, final boolean enabled) {
		getSite().getShell().getDisplay().syncExec(new Runnable() {
			public void run () {
				IToolBarManager tbMgr = getViewSite().getActionBars().getToolBarManager();
				IContributionItem item = tbMgr.find(toolItemId);
				if (item instanceof ActionContributionItem) {
					ActionContributionItem aci = (ActionContributionItem) item;
					aci.getAction().setEnabled(enabled);
					item.update(IAction.ENABLED);
				}
			}
		});
	}
	
	protected void createContextMenu(StructuredViewer viewer) {
		final MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				menuMgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});
		
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	protected String getViewId() {
		return getClass().getName();
	}
	
	protected abstract void setInput (Object input);
	
}
