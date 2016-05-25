/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.actions;

import java.lang.reflect.InvocationTargetException;

import org.a11y.utils.accprobe.Activator;
import org.a11y.utils.accprobe.views.AbstractViewPart;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;


/**
 * @see IWorkbenchWindowActionDelegate
 */
public abstract class AbstractAction
	implements IWorkbenchWindowActionDelegate, IViewActionDelegate,
	IObjectActionDelegate
{

	protected Activator plugin = Activator.getDefault();

	public Activator getPlugin () {
		return plugin;
	}

	/**
	 * The constructor.
	 */
	public AbstractAction () {
		store = Activator.getDefault().getPreferenceStore();
	}

	protected IViewPart view;

	protected IWorkbenchWindow window;

	protected IPreferenceStore store;

	/**
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init (IWorkbenchWindow window) {
		this.window = window;
	}

	protected IAction action;

	protected ISelection selection;

	/**
	 * Selection in the workbench has been changed. We can change the state of
	 * the 'real' action here if we want, but this can only happen after the
	 * delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged (IAction action, ISelection selection) {
		this.action = action;
		this.selection = selection;
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose () {
		action = null;
		selection = null;
		window = null;
	}

	protected void handleExecuteInContextError (Error e) {
		e.printStackTrace();
	}

	protected void handleExecuteInContextException (Exception e) {
		e.printStackTrace();
	}

	protected interface XRunnable
	{

		void run (IProgressMonitor monitor) throws Exception;
	}

	// *** fix exception propogation ***
	protected void execute (final XRunnable runner) {
		try {
			final Display d = Display.getCurrent();
			if (d != null) {
				IProgressService ps = PlatformUI.getWorkbench().getProgressService();
				ps.busyCursorWhile(new IRunnableWithProgress() {

					public void run (final IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
						d.asyncExec(new Runnable() {

							public void run () {
								try {
									runner.run(monitor);
								}catch (Exception e) {
									handleExecuteInContextException(e);
								}catch (Error e) {
									handleExecuteInContextError(e);
								}
							}
						});
					}
				});
			}
		}catch (Exception e) {
			handleExecuteInContextException(e);
		}
	}

	public void init (IViewPart part) {
		this.view = part;
	}

	protected IWorkbenchPart activePart;

	public void setActivePart (IAction action, IWorkbenchPart part) {
		activePart = part;
	}

	public void toggleMenuAction (AbstractViewPart view, String menuItemId,
									boolean checked) {
		IMenuManager menuMgr = view.getViewSite().getActionBars().getMenuManager();
		if (menuMgr != null) {
			IContributionItem item = menuMgr.find(menuItemId);
			if (item instanceof ActionContributionItem) {
				ActionContributionItem aci = (ActionContributionItem) item;
				aci.getAction().setChecked(checked);
				item.update(IAction.CHECKED);
			}
		}
	}
}
