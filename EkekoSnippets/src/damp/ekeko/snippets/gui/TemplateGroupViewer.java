package damp.ekeko.snippets.gui;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import damp.ekeko.snippets.data.TemplateGroup;
import damp.ekeko.snippets.gui.viewer.SnippetPrettyPrinter;

public class  TemplateGroupViewer extends Composite {

	private TextViewer textViewerSnippet;
	private TreeViewer snippetTreeViewer;
	private TreeViewerColumn snippetKindCol;
	private TreeViewerColumn snippetPropCol;
	private TreeViewerColumn snippetNodeCol;
	
	private List<TemplateGroupViewerNodeSelectionListener> nodeSelectionListeners; 

	private Object cljGroup, cljTemplate, cljNode;

	public TemplateGroupViewer(Composite parent, int style) {
		super(parent, SWT.NONE);
		
		nodeSelectionListeners = new LinkedList<TemplateGroupViewerNodeSelectionListener>();
		
		//Composite composite = new Composite(parent, SWT.NONE);
		Composite composite = this;
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		composite.setLayout(gridLayout);
		textViewerSnippet = new TextViewer(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		StyledText styledText = textViewerSnippet.getTextWidget();
		GridData gd_styledText = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		gd_styledText.heightHint = 100;
		styledText.setLayoutData(gd_styledText);
		textViewerSnippet.setEditable(false);

		snippetTreeViewer = new TreeViewer(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		snippetTreeViewer.setAutoExpandLevel(2);
		Tree treeSnippet = snippetTreeViewer.getTree();
		treeSnippet.setHeaderVisible(true);
		treeSnippet.setLinesVisible(true);
		treeSnippet.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		snippetNodeCol = new TreeViewerColumn(snippetTreeViewer, SWT.NONE);
		TreeColumn trclmnNode = snippetNodeCol.getColumn();
		trclmnNode.setWidth(150);
		trclmnNode.setText("Node");

		snippetKindCol = new TreeViewerColumn(snippetTreeViewer, SWT.NONE);
		TreeColumn snippetKindColCol = snippetKindCol.getColumn();
		snippetKindColCol.setWidth(150);
		snippetKindColCol.setText("Node kind");

		snippetPropCol = new TreeViewerColumn(snippetTreeViewer, SWT.NONE);
		TreeColumn trclmnProperty = snippetPropCol.getColumn();
		trclmnProperty.setWidth(150);
		trclmnProperty.setText("Location");

		snippetTreeViewer.setContentProvider(new TemplateTreeContentProvider());
		
		treeSnippet.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				onNodeSelectionInternal();
			}
		});		

		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				//dispose of colors etc
			}
	     });
		
		parent.layout();
	}
	
	private void onNodeSelectionInternal() {
		updateTextFields();
		TemplateGroupViewerNodeSelectionEvent event = new TemplateGroupViewerNodeSelectionEvent(this, cljGroup, cljTemplate, cljNode);
		for(TemplateGroupViewerNodeSelectionListener listener : nodeSelectionListeners) {
			listener.nodeSelected(event);
		}
	}
	
	public boolean addNodeSelectionListener(TemplateGroupViewerNodeSelectionListener listener) {
		return nodeSelectionListeners.add(listener);
	}
	
	public boolean removeNodeSelectionListener(TemplateGroupViewerNodeSelectionListener listener) {
		return nodeSelectionListeners.remove(listener);
	}
	
	public void refresh() {
		updateTextFields();
	}
		
	public Object getSelectedSnippetNode() {
		IStructuredSelection selection = (IStructuredSelection) snippetTreeViewer.getSelection();
		cljNode = selection.getFirstElement();
		return cljNode;
	}

	public Object getSelectedSnippet() {
		cljTemplate =  TemplateGroup.FN_SNIPPETGROUP_SNIPPET_FOR_NODE.invoke(cljGroup, getSelectedSnippetNode());
		return cljTemplate;
	}

	private void updateTextFields() {
		Object selectedSnippet = getSelectedSnippet();
		if(selectedSnippet == null) {
			textViewerSnippet.getTextWidget().setText("");
			return;
		}			
		SnippetPrettyPrinter prettyprinter = new SnippetPrettyPrinter(TemplateGroup.newFromClojureGroup(cljGroup));
		prettyprinter.setHighlightNode(getSelectedSnippetNode());
		textViewerSnippet.getTextWidget().setText(prettyprinter.prettyPrintSnippet(selectedSnippet));
		for(StyleRange range : prettyprinter.getStyleRanges())
			textViewerSnippet.getTextWidget().setStyleRange(range);
	}
	
	public void setInput(Object cljGroup, Object cljTemplate, Object cljNode) {
		this.cljGroup = cljGroup;
		this.cljTemplate = cljTemplate;
		this.cljNode = cljNode;
		snippetNodeCol.setLabelProvider(new TemplateTreeLabelProviders.NodeColumnLabelProvider(cljGroup));		
		snippetPropCol.setLabelProvider(new TemplateTreeLabelProviders.PropertyColumnLabelProvider(cljGroup));
		snippetKindCol.setLabelProvider(new TemplateTreeLabelProviders.KindColumnLabelProvider(cljGroup));
		snippetTreeViewer.setInput(cljGroup);
		if(cljNode != null)
			snippetTreeViewer.setSelection(new StructuredSelection(cljNode));
		updateTextFields();
	}
	
}

		
	