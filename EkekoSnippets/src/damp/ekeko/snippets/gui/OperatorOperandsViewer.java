package damp.ekeko.snippets.gui;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import damp.ekeko.snippets.data.SnippetOperator;

public class OperatorOperandsViewer extends Composite {

	private TreeViewer operatorTreeViewer;
	private Tree operatorTree;
	private TableViewer operandsTableViewer;
	private Table operandsTable;

	
	private Object cljGroup,  cljSnippet,  cljSelectedSnippetNode;
	
	public OperatorOperandsViewer(Composite parent, int style) {
		super(parent, style);
		
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		this.setLayout(gridLayout);

		operatorTreeViewer = new TreeViewer(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		operatorTreeViewer.setAutoExpandLevel(3);
		operatorTree = operatorTreeViewer.getTree();
		GridData gd_operatorTree = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		gd_operatorTree.heightHint = 191;
		operatorTree.setLayoutData(gd_operatorTree);
		
		TreeViewerColumn operatorNameColumn = new TreeViewerColumn(operatorTreeViewer, SWT.NONE);
		TreeColumn trclmnOperator = operatorNameColumn.getColumn();
		trclmnOperator.setWidth(150);
		trclmnOperator.setText("Operator");

		//ColumnViewerToolTipSupport.enableFor(operatorTreeViewer, ToolTip.NO_RECREATE);
		
		operatorNameColumn.setLabelProvider(new OperatorTreeLabelProvider());
		operatorTreeViewer.setContentProvider(new OperatorTreeContentProvider());


		operatorTree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				updateWidgets();
			}
		});		

		/*
		TextViewer textViewer = new TextViewer(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		textOpInfo = textViewer.getTextWidget();
		textOpInfo.setEditable(false);
		GridData gd_textOpInfo = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		gd_textOpInfo.heightHint = 50;
		textOpInfo.setLayoutData(gd_textOpInfo);
		*/
		
		
		
		operandsTableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
		operandsTable = operandsTableViewer.getTable();
		operandsTable.setLinesVisible(true);
		operandsTable.setHeaderVisible(true);
		GridData gd_tableOpArgs = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_tableOpArgs.heightHint = 31;
		operandsTable.setLayoutData(gd_tableOpArgs);
		
		operandsTableViewer.setContentProvider(new ArrayContentProvider());
		
		//operandsTableDecorator = new OperandsTableDecorator(operandsTable);
		
		TableViewerColumn operandDescriptionCol = new TableViewerColumn(operandsTableViewer, SWT.NONE);
		TableColumn operandDescriptionColCol = operandDescriptionCol.getColumn();
		operandDescriptionColCol.setWidth(150);
		operandDescriptionColCol.setText("Operand");
		operandDescriptionCol.setLabelProvider(new OperandBindingLabelProviderDescription());
		
		
		
		TableViewerColumn operandValueCol = new TableViewerColumn(operandsTableViewer, SWT.NONE);
		TableColumn operandValueColCol = operandValueCol.getColumn();
		operandValueColCol.setWidth(150);
		operandValueColCol.setText("Value");
		operandValueCol.setLabelProvider(new OperandBindingLabelProviderValue());

		operandValueCol.setEditingSupport(new OperandBindingEditingSupport(operandsTableViewer));

	}
	
	public Object getSelectedOperator() {
        IStructuredSelection selection = (IStructuredSelection) operatorTreeViewer.getSelection();
        return selection.getFirstElement();
	}
	
	public Object getOperands() {
		return operandsTableViewer.getInput();
	}

	
	
	public void setInput(Object cljGroup, Object cljSnippet, Object cljSelectedSnippetNode) {
		this.cljGroup = cljGroup;
		this.cljSnippet = cljSnippet;
		this.cljSelectedSnippetNode = cljSelectedSnippetNode;
		updateWidgets();
	}
	
	private void updateWidgets() {
		operatorTreeViewer.setInput(cljSelectedSnippetNode);
		Object selectedOperator = getSelectedOperator();
		if(selectedOperator == null || !SnippetOperator.isOperator(selectedOperator)) {
			operandsTableViewer.setInput(null);
			return;	
		}
		
		//textOpInfo.setText(SnippetOperator.getDescription(selectedOperator));
		operandsTableViewer.setInput(SnippetOperator.getOperands(cljGroup, cljSnippet, cljSelectedSnippetNode, selectedOperator));

	}
		


}