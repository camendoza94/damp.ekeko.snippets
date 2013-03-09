package damp.ekeko.snippets.gui;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;

import clojure.lang.PersistentVector;

import damp.ekeko.snippets.SnippetGroup;
import damp.ekeko.snippets.SnippetGroupTreeContentProvider;
import damp.ekeko.snippets.SnippetGroupTreeLabelProviders;
import damp.ekeko.snippets.SnippetOperator;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class SnippetView extends ViewPart {

	public static final String ID = "damp.ekeko.snippets.gui.SnippetView"; //$NON-NLS-1$
	private String viewID;

	private Action actAddSnippet;
	private Action actRunQuery;
	private StyledText textSnippet;
	private StyledText textCondition;
	private TreeViewer treeViewerSnippet;
	private TreeViewer treeViewerOperator;
	private TableViewer tableViewerNode;
	
	private SnippetGroup snippetGroup;
	private SnippetGroupTreeContentProvider contentProvider;

	public SnippetView() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		snippetGroup = new SnippetGroup("Group");

		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Group group_1 = new Group(container, SWT.NONE);
		group_1.setLayout(new GridLayout(1, false));
		
		Label lblSnippet = new Label(group_1, SWT.NONE);
		GridData gd_lblSnippet = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_lblSnippet.heightHint = 23;
		lblSnippet.setLayoutData(gd_lblSnippet);
		lblSnippet.setText("Snippet");
		
		TextViewer textViewerSnippet = new TextViewer(group_1, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		textViewerSnippet.setEditable(false);
		textSnippet = textViewerSnippet.getTextWidget();
		textSnippet.setDoubleClickEnabled(false);
		textSnippet.setEditable(false);
		GridData gd_textSnippet = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_textSnippet.heightHint = 95;
		textSnippet.setLayoutData(gd_textSnippet);
		
		TextViewer textViewerCondition = new TextViewer(group_1, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		textViewerCondition.setEditable(false);
		textCondition = textViewerCondition.getTextWidget();
		textCondition.setDoubleClickEnabled(false);
		textCondition.setEditable(false);
		textCondition.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Group group_2 = new Group(container, SWT.NONE);
		group_2.setLayout(new GridLayout(1, false));
		
		ToolBar toolBar = new ToolBar(group_2, SWT.FLAT | SWT.RIGHT);
		toolBar.setOrientation(SWT.RIGHT_TO_LEFT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		ToolItem tltmRemove = new ToolItem(toolBar, SWT.NONE);
		tltmRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeSnippet();
			}
		});
		tltmRemove.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/obj16/delete_obj.gif"));
		tltmRemove.setToolTipText("Remove Snippet");
		
		ToolItem tltmAdd = new ToolItem(toolBar, SWT.NONE);
		tltmAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addSnippet();
			}
		});
		tltmAdd.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/obj16/add_obj.gif"));
		tltmAdd.setToolTipText("Add Snippet");
		
		ToolItem tltmView = new ToolItem(toolBar, SWT.NONE);
		tltmView.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewSnippet();
			}
		});
		tltmView.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/obj16/keygroups_obj.gif"));
		tltmView.setToolTipText("View Snippet");
		
		ToolItem tltmRunQuery = new ToolItem(toolBar, SWT.NONE);
		tltmRunQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				runQuery();
			}
		});
		tltmRunQuery.setImage(ResourceManager.getPluginImage("org.eclipse.pde.ui", "/icons/obj16/profile_exc.gif"));
		tltmRunQuery.setToolTipText("Run Query");
		
		ToolItem tltmCondition = new ToolItem(toolBar, SWT.NONE);
		tltmCondition.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addLogicCondition();
			}
		});
		tltmCondition.setImage(ResourceManager.getPluginImage("org.eclipse.pde.ui", "/icons/obj16/processinginst.gif"));
		tltmCondition.setToolTipText("Add Logic Condition");
		
		treeViewerSnippet = new TreeViewer(group_2, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Tree treeSnippet = treeViewerSnippet.getTree();
		treeSnippet.setLinesVisible(true);
		treeSnippet.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TreeViewerColumn snippetNodeCol = new TreeViewerColumn(treeViewerSnippet, SWT.NONE);
		TreeColumn trclmnNode = snippetNodeCol.getColumn();
		trclmnNode.setWidth(150);
		trclmnNode.setText("Node");
		
		TreeViewerColumn snippetVarCol = new TreeViewerColumn(treeViewerSnippet, SWT.NONE);
		TreeColumn trclmnLogicVariable = snippetVarCol.getColumn();
		trclmnLogicVariable.setWidth(150);
		trclmnLogicVariable.setText("Logic Variable");
		
		contentProvider = new SnippetGroupTreeContentProvider();
		treeViewerSnippet.setContentProvider(getContentProvider());
		snippetNodeCol.setLabelProvider(new SnippetGroupTreeLabelProviders.NodeColumnLabelProvider(this));		
		snippetVarCol.setLabelProvider(new SnippetGroupTreeLabelProviders.VariableColumnLabelProvider(this));

		treeSnippet.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
		        onSnippetSelection();
			}
		});		
		
		treeSnippet.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				viewSnippet();
			}
		});

		Group group_3 = new Group(container, SWT.NONE);
		group_3.setLayout(new GridLayout(1, false));
		
		Label lblOperator = new Label(group_3, SWT.NONE);
		GridData gd_lblOperator = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_lblOperator.heightHint = 22;
		lblOperator.setLayoutData(gd_lblOperator);
		lblOperator.setText("Operator");
		
		treeViewerOperator = new TreeViewer(group_3, SWT.BORDER  | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Tree treeOperator = treeViewerOperator.getTree();
		treeOperator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TreeColumn trclmnOperator = new TreeColumn(treeOperator, SWT.NONE);
		trclmnOperator.setWidth(300);
		trclmnOperator.setText("Operator");
		showOperators();
		
		treeOperator.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
		        onOperatorSelection();
			}
		});		

		Group group_4 = new Group(container, SWT.NONE);
		group_4.setLayout(new GridLayout(1, false));
		
		Label lblNewLabel = new Label(group_4, SWT.NONE);
		GridData gd_lblNewLabel = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_lblNewLabel.heightHint = 22;
		lblNewLabel.setLayoutData(gd_lblNewLabel);
		
		tableViewerNode = new TableViewer(group_4, SWT.BORDER | SWT.FULL_SELECTION);
		Table tableNode = tableViewerNode.getTable();
		tableNode.setLinesVisible(true);
		tableNode.setHeaderVisible(true);
		tableNode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewerNode, SWT.NONE);
		TableColumn tblclmnType = tableViewerColumn.getColumn();
		tblclmnType.setWidth(150);
		tblclmnType.setText("Type");
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewerNode, SWT.NONE);
		TableColumn tblclmnNode = tableViewerColumn_1.getColumn();
		tblclmnNode.setWidth(150);
		tblclmnNode.setText("Node");

	    tableNode.addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {
	    		onNodeSelection();
	    	}
	    });

	    createActions();
		initializeToolBar();
		initializeMenu();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
		{
			actAddSnippet = new Action("Add Snippet") {				public void run() {
					addSnippet();
				}
			};
			actAddSnippet.setImageDescriptor(ResourceManager.getPluginImageDescriptor("org.eclipse.ui", "/icons/full/obj16/add_obj.gif"));
			actAddSnippet.setToolTipText("Add Snippet");
		}
		{
			actRunQuery = new Action("Run Query") {				public void run() {
					runQuery();
				}
			};
			actRunQuery.setImageDescriptor(ResourceManager.getPluginImageDescriptor("org.eclipse.pde.ui", "/icons/obj16/profile_exc.gif"));
			actRunQuery.setToolTipText("Run Query");
		}
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
		toolbarManager.add(actAddSnippet);
		toolbarManager.add(actRunQuery);
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars()
				.getMenuManager();
		menuManager.add(actAddSnippet);
		menuManager.add(actRunQuery);
	}

	@Override
	public void setFocus() {
		// Set the focus
	}
	
	String getSelectedTextFromActiveEditor() {
		ITextEditor editor =  (ITextEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		ITextSelection selection = (ITextSelection) editor.getSelectionProvider().getSelection();	
		return selection.getText();
	}
		 
	public String getSelectedOperator() {
        return treeViewerOperator.getTree().getSelection()[0].getText();
	}
	
	public Object getSelectedNode() {
		return tableViewerNode.getTable().getSelection()[0].getData();
	}
	
	public Object getSelectedSnippet() {
        //String lvar = treeViewerSnippet.getTree().getSelection()[1].getText();
		Object data = treeViewerSnippet.getTree().getSelection()[0].getData();
		System.out.println(data);
        return data;
	}
	
	public SnippetGroupTreeContentProvider getContentProvider() {
		return contentProvider;
	}

	public void setViewID(String secondaryId) {
		this.viewID = secondaryId;
	}

	//-----------------------------------------------
	//LOGIC PART
	//all logic part for this View are written below
	
	public void addSnippet() {
		System.out.println("Add Snippet");
		String code = getSelectedTextFromActiveEditor();
		System.out.println(code);
		snippetGroup.addSnippetCode(code);
		textSnippet.setText(snippetGroup.toString());
		treeViewerSnippet.setInput(snippetGroup.getGroup());
	}
	
	public void viewSnippet() {
		System.out.println("View Snippet");
		snippetGroup.viewSnippet(getSelectedSnippet());
	}

	public void removeSnippet() {
		System.out.println("Remove Snippet");
	}

	public void runQuery() {
		System.out.println("Run Query");
		snippetGroup.runQuery(getSelectedSnippet());
	}

	public void addLogicCondition() {
		System.out.println("Add Logic Condition");
		//Object node = snippetGroup.getRoot(getSelectedSnippet());
		applyOperator(getSelectedSnippet(), "add-logic-conditions", "");
	}

	public void showOperators() {
		SnippetOperator.setInput(treeViewerOperator);
		treeViewerOperator.getTree().getItems()[0].setExpanded(true);	
	}
	
	public void onSnippetSelection() {
		textSnippet.setText(snippetGroup.toString(getSelectedSnippet()));
	} 
	
	public void onOperatorSelection() {
        snippetGroup.setInputPossibleNodes(getSelectedSnippet(), tableViewerNode, getSelectedOperator());
	} 
	
	public void onNodeSelection() {
		PersistentVector selectedNode = (PersistentVector) getSelectedNode();
		applyOperator(selectedNode.get(0), getSelectedOperator(), "");
	} 
	
	public void applyOperator(Object selectedNode, String selectedOperator, String input) {
		String argsInfo = SnippetOperator.getOperatorArgumentsInformation(selectedOperator);
		String confirmation = "Apply Operator " + selectedOperator + 
				" to Node " + SnippetGroup.getTypeValue(selectedNode) + "\n" +
				selectedNode + "\n" + argsInfo;
		boolean ok = false;
		
		if (argsInfo.isEmpty()) {
			ok = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
				"Apply Operator", confirmation);
		} else {
			InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(),
				"Apply Operator", confirmation, "", null);
			ok = (dlg.open() == Window.OK);
			input = dlg.getValue();
		}
		
		if (ok) {
			String[] args = input.split(":");
			snippetGroup.applyOperator(selectedOperator, selectedNode, args);
			textSnippet.setText(snippetGroup.toString(getSelectedSnippet()));
		}

	}
}
