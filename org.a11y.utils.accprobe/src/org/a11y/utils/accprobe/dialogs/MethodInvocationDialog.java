/*******************************************************************************
* Copyright (c) 2004, 2010 IBM Corporation.

* Contributors:
*  IBM Corporation - initial API and implementation
*******************************************************************************/ 

package org.a11y.utils.accprobe.dialogs;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.a11y.utils.accprobe.GuiUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class MethodInvocationDialog extends Dialog
{

	public static final String DIALOG_TITLE = "Invoke Method";

	public static final String ID = "org.a11y.utils.accprobe.dialogs.MethodInvocationDialog";

	public static final String METHOD_KEY = ID + ".method";

	public static final String PARAMTYPE_KEY = ID + ".paramType";

	public static final int CLEAR_BUTTON_ID = 100;

	public static final int INVOKE_BUTTON_ID = 101;

	public static final int CLOSE_BUTTON_ID = 102;

	private ArrayList<Method> _methodList = new ArrayList<Method>();

	private Combo _methodSelectionList;

	private StackLayout _stackLayout;

	private Text _resultField;

	private Button _invokeButton;

	private Composite _paramInput, _noParamsComposite;

	private Object _target;
	private Object _mName =null;


	private Hashtable<String, String[]> inParamStrings = new Hashtable<String, String[]>();

	public MethodInvocationDialog (Shell parentShell, Object target, String name) {
		super(parentShell);
		setShellStyle(getShellStyle()|SWT.RESIZE |SWT.MAX | SWT.MIN);
		_target = target;
		_mName =name;
		//set parameter constants
		inParamStrings.put("getRelation", new String[] {"relationIndex"});
		inParamStrings.put("scrollTo", new String[] {"scrollType"});
		inParamStrings.put("scrollToPoint", new String[]{"coordinateType", "x", "y"});
		inParamStrings.put("doAction", new String[] {"actionIndex"});
		inParamStrings.put("getKeyBinding", new String[] {"actionIndex","nMaxBindings"});
		inParamStrings.put("copyText", new String[] {"startOffset", "endOffset"});
		inParamStrings.put("deleteText", new String[] {"startOffset", "endOffset"});
		inParamStrings.put("insertText", new String[] {"offset", "text"});
		inParamStrings.put("cutText", new String[] {"startOffset", "endOffset"});
		inParamStrings.put("pasteText", new String[] {"offset"});
		inParamStrings.put("replaceText", new String[] {"startOffset", "endOffset", "text"});
		inParamStrings.put("setAttributes", new String[] {"startOffset", "endOffset", "attributes"});
		inParamStrings.put("getAnchor", new String[] {"index"});
		inParamStrings.put("getAnchorTarget", new String[] {"index" });
		inParamStrings.put("getHyperlink", new String[] {"index"});
		inParamStrings.put("getHyperlinkIndex", new String[] {"charIndex"});
		inParamStrings.put("getAccessibleAt", new String[] {"row","column"});
		inParamStrings.put("getChildIndex", new String[] {"rowIndex","columnIndex"});
		inParamStrings.put("getColumnDescription", new String[]{"column"});
		inParamStrings.put("getColumnExtentAt", new String[] {"row","column" });
		inParamStrings.put("getColumnIndex", new String[] {"cellIndex"});
		inParamStrings.put("getRowColumnExtentsAtIndex", new String[] {"index"});
		inParamStrings.put("getRowDescription", new String[] {"row"});
		inParamStrings.put("getRowExtentAt", new String[] {"row","column"});
		inParamStrings.put("getRowIndex", new String[] {"cellIndex"});
		inParamStrings.put("isColumnSelected", new String[]{"column"});
		inParamStrings.put("isRowSelected", new String[] {"row"});
		inParamStrings.put("selectRow", new String[] {"row"});
		inParamStrings.put("selectColumn", new String[] {"column"});
		inParamStrings.put("unselectRow", new String[] {"row"});
		inParamStrings.put("unselectColumn", new String[]{"column"});
		inParamStrings.put("addSelection", new String[] {"startOffset","endOffset"});
		inParamStrings.put("getAttributes", new String[] {"offset" });
		inParamStrings.put("getCharacterExtents", new String[] {"offset","coordType"});
		inParamStrings.put("getOffsetAtPoint", new String[] {"x", "y", "coordType"});
		inParamStrings.put("getSelection",new String[] {"index"});
		inParamStrings.put("getText", new String[] {"startOffset","endOffset"});
		inParamStrings.put("getTextBeforeOffset", new String[] {"offset","boundaryType"});
		inParamStrings.put("getTextAfterOffset", new String[] {"offset","boundaryType"});
		inParamStrings.put("getTextAtOffset", new String[] {"offset","boundaryType"});
		inParamStrings.put("removeSelection", new String[]{"selectionIndex"});
		inParamStrings.put("setCaretOffset", new String[] {"offset"});
		inParamStrings.put("setSelection", new String[]{"selectionIndex", "startOffset", "endOffset"});
		inParamStrings.put("scrollSubstringTo", new String[]{"startIndex", "endIndex", "scrollType"});
		inParamStrings.put("scrollSubstringToPoint", new String[] {"startIndex", "endIndex", "coordinateType", "x", "y"});
		inParamStrings.put("setCurrentValue", new String[]{"vt_type", "value"});
		inParamStrings.put("accChild", new String[]{"index"});
		inParamStrings.put("accSelect", new String[]{"flag", "childId"});
		inParamStrings.put("accDoDefaultAction",new String[]{"childId"});
		inParamStrings.put("accLocation",new String[]{"childId"});
		inParamStrings.put("put_accValue",new String[]{"childId","value"});
				
	}

	public MethodInvocationDialog (IShellProvider provider, Object target) {
		this(provider.getShell(), target , null);
	}

	protected void configureShell (Shell shell) {
		super.configureShell(shell);
		shell.setText(DIALOG_TITLE);
	}

	protected Control createDialogArea (Composite parent) {
		initializeDialogUnits(parent);
		Composite main = (Composite) super.createDialogArea(parent);
		createMethodSelectionSection(main);
		createParameterInputSection(main);
		createResultsSection(main);
		return main;
	}

	protected void createButtonsForButtonBar (Composite parent) {
		createButton(parent, CLEAR_BUTTON_ID, "Clea&r parameters", false);
		_invokeButton = createButton(
			parent, INVOKE_BUTTON_ID, "&Invoke method", true);
		createButton(parent, CLOSE_BUTTON_ID, "&Close", false);
		if (_methodSelectionList.getItemCount() > 0) {
			_methodSelectionList.select(0);
			showParameterInputSection((Method) _methodList.get(0));
		}else {
			_stackLayout.topControl = _noParamsComposite;
		}
	}

	protected void buttonPressed (int buttonId) {
		if (buttonId == CLEAR_BUTTON_ID) {
			doClear();
		}else if (buttonId == INVOKE_BUTTON_ID) {
			doInvoke();
		}else if (buttonId == CLOSE_BUTTON_ID) {
			doClose();
		}
	}

	private void createMethodSelectionSection (Composite main) {
		Composite methSelection = new Composite(main, SWT.NONE);
		methSelection.setLayout(new GridLayout());
		methSelection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label methodPrompt = new Label(methSelection, SWT.LEFT);
		methodPrompt.setText("Select method to invoke:");
		_methodSelectionList = new Combo(methSelection, SWT.DROP_DOWN
				| SWT.READ_ONLY);
		GridData gd = new GridData();
		gd.widthHint = 400;
		_methodSelectionList.setLayoutData(gd);
		_methodSelectionList.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected (SelectionEvent event) {
				int selection = _methodSelectionList.getSelectionIndex();
				if (selection >= 0) {
					showParameterInputSection((Method) _methodList.get(selection));
				}else {
					_stackLayout.topControl = _noParamsComposite;
				}
				_paramInput.layout();
			}
		});
		
		Class<?> targetType = _target.getClass();
		
		if(_mName==null){
			// need to weed out the getters
			PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(targetType);
			Set<Method> readMethods = new HashSet<Method>();
			for (int d = 0; d < descriptors.length; ++d) {
				Method readMethod = descriptors[d].getReadMethod();
				if (readMethod != null) {
					readMethods.add(readMethod);
				}
			}
			// may want to parameterize whether all or just declared methods are shown
			// may want to reveal methods based on modifiers
			Method[] methods = targetType.getMethods();
			for (int m = 0; m < methods.length; ++m) {
				Method method = methods[m];
				int modifiers = method.getModifiers();
				if (method.getDeclaringClass()!=Object.class && !readMethods.contains(method) && Modifier.isPublic(modifiers)) {
					_methodList.add(method);
					_methodSelectionList.add(method.getName());
				}
			}
		}else{//NOT A SIMPLE PROPERTY
			Method[] methods = targetType.getMethods();
			for (int m = 0; m < methods.length; ++m) {
				Method method = methods[m];
				String methodName= "get"+ _mName;
				if (method.getName().equalsIgnoreCase(_mName.toString())||
					method.getName().equalsIgnoreCase(methodName) ) {
					_methodList.add(method);
					_methodSelectionList.add(method.getName());
				}
			}
		}
	}

	private void createParameterInputSection (Composite main) {
		Composite paramsSection = new Composite(main, SWT.NONE);
		paramsSection.setLayout(new GridLayout());
		paramsSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label paramsPrompt = new Label(paramsSection, SWT.LEFT);
		paramsPrompt.setText("Enter values for parameters:");
		_paramInput = new Composite(paramsSection, SWT.NONE);
		_stackLayout = new StackLayout();
		_paramInput.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridData gd = new GridData();
		gd.heightHint = 400;
		_paramInput.setLayoutData(gd);
		_paramInput.setLayout(_stackLayout);
		_noParamsComposite = new Composite(_paramInput, SWT.NONE);
		_noParamsComposite.setLayout(new GridLayout());
		_noParamsComposite.setData(METHOD_KEY, "");
		Text t = new Text(_noParamsComposite, SWT.READ_ONLY);
		t.setText("This method takes no parameters");
		t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		_paramInput.layout();
	}

	private void showParameterInputSection (Method method) {
		Control[] comps = _paramInput.getChildren();
		Composite paramInputPanel = null;
		for (int c = 0; paramInputPanel == null && c < comps.length; ++c) {
			if (comps[c] instanceof Composite
					&& comps[c].getData(METHOD_KEY).equals(method)) {
				paramInputPanel = (Composite) comps[c];
			}
		}
		if (paramInputPanel == null && method.getParameterTypes().length == 0) {
			paramInputPanel = _noParamsComposite;
			paramInputPanel.setData(METHOD_KEY, method);
		}else if (paramInputPanel == null) {
			paramInputPanel = new Composite(_paramInput, SWT.NONE);
			paramInputPanel.setLayout(new GridLayout(2, false));
			paramInputPanel.setData(METHOD_KEY, method);
			Class<?>[] paramTypes = method.getParameterTypes();
			String[] paramNames = inParamStrings.get(method.getName());
			for (int t = 0; t < paramTypes.length; ++t) {
				Label label = new Label(paramInputPanel, SWT.RIGHT);
				if(paramNames!=null && paramNames.length == paramTypes.length ){
					label.setText( paramTypes[t].getSimpleName()+" "+ paramNames[t]);
				}else{
					label.setText( "param " + t + " " + paramTypes[t].getSimpleName());
				}

				label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
				Text text;
				if (!GuiUtils.isPrimitive(paramTypes[t])
						&& !(paramTypes[t].isArray() && GuiUtils.isPrimitive(paramTypes[t].getComponentType()))) {
					text = new Text(paramInputPanel, SWT.LEFT | SWT.READ_ONLY | SWT.SINGLE | SWT.BORDER);
					text.setText("Unsupported type");
					_invokeButton.setEnabled(false);
				}else {
					text = new Text(paramInputPanel, SWT.LEFT | SWT.SINGLE | SWT.BORDER);
					_invokeButton.setEnabled(true);
				}
				GridData gd = new GridData(GridData.FILL_HORIZONTAL);
				gd.widthHint = 400;
				text.setLayoutData(gd);
				text.setData(PARAMTYPE_KEY, paramTypes[t]);
			}
		}
		_stackLayout.topControl = paramInputPanel;
	}

	private void createResultsSection (Composite main) {
		Composite results = new Composite(main, SWT.NONE);
		results.setLayout(new GridLayout(2, false));
		results.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label resultsLabel = new Label(results, SWT.LEFT);
		resultsLabel.setText("Results:");
		_resultField = new Text(results, SWT.READ_ONLY);
		_resultField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void doClear () {
		Control[] paramInputs = ((Composite) _stackLayout.topControl).getChildren();
		for (int p = 0; p < paramInputs.length; ++p) {
			if (paramInputs[p] instanceof Text) {
				((Text) paramInputs[p]).setText("");
			}
		}
	}

	private void doInvoke () {
		Composite paramInputPanel = (Composite) _stackLayout.topControl;
		Control[] paramInputs = paramInputPanel.getChildren();
		ArrayList<Object> paramList = new ArrayList<Object>();
		for (int p = 0; p < paramInputs.length; ++p) {
			if (paramInputs[p] instanceof Text) {
				String text = ((Text) paramInputs[p]).getText();
				Class<?> paramType = (Class<?>) paramInputs[p].getData(PARAMTYPE_KEY);
				if (paramType != null) {
					paramList.add(parseParam(text, paramType));
				}
			}
		}
		Method method = (Method) paramInputPanel.getData(METHOD_KEY);
		if (method != null) {
			try {
				Object result = "<Target is null>";
				if (_target != null && paramList != null) {
					result = method.invoke(_target, paramList.toArray());
					if(result == null){
						result = "NULL";
					}
				}
				StringBuffer sb = new StringBuffer(method.getName());
				Iterator<Object> iter = paramList.iterator();
				sb.append('(');
				if (!iter.hasNext()) {
					sb.append(')');
				}
				while (iter.hasNext()) {
					sb.append(iter.next().toString());
					sb.append(iter.hasNext() ? ", " : ")");
				}
				sb.append(" = ");
				sb.append(method.getReturnType() == Void.TYPE ? "<No return value>"
						: result.toString());
				_resultField.setText(sb.toString());
			}catch (Exception e) {
				_resultField.setText(e.toString());
			}
			_resultField.setFocus();
		}
	}

	private void doClose () {
		super.okPressed();
	}

	private Object parseParam (String text, Class<?> type) {
		Object param = null;
		if (type.equals(Integer.TYPE)) {
			param = Integer.valueOf(text);
		}else if (type.equals(Double.TYPE)) {
			param = Double.valueOf(text);
		}else if (type.equals(Float.TYPE)) {
			param = Float.valueOf(text);
		}else if (type.equals(Long.TYPE)) {
			param = Long.parseLong(text);
		}else if (type.equals(Character.TYPE)) {
			param = Character.valueOf(text.charAt(0));
		}else if (type.equals(Boolean.TYPE)) {
			param = new Boolean(text.equalsIgnoreCase("true"));
		}else if (type.getName().equals("java.lang.String")){
			param = new String(text);
		}else if(type.isArray()) {
			String[] params = text.split(",");
			ArrayList<Object> res = new ArrayList<Object>();
			for (int pp = 0; pp < params.length; ++pp) {
				res.add(parseParam(params[pp].trim(), type.getComponentType()));
			}
			param = res.toArray();
		}
		return param;
	}
}
