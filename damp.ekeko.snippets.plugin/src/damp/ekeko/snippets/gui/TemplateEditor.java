package damp.ekeko.snippets.gui;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.operations.UndoRedoActionGroup;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import baristaui.util.MarkerUtility;
import clojure.lang.IFn;
import damp.ekeko.JavaProjectModel;
import damp.ekeko.snippets.EkekoSnippetsPlugin;
import damp.ekeko.snippets.data.TemplateGroup;

public class TemplateEditor extends EditorPart {

	public static final String ID = "damp.ekeko.snippets.gui.TemplateEditor"; //$NON-NLS-1$
	//private String viewID;

	private TemplateGroup templateGroup;
	private TemplateGroupViewer templateGroupViewer;
	private TemplateTreeContentProvider contentProvider;
	//private OperatorOperandsViewer operatorOperandsViewer;

	//private BoundDirectivesViewer boundDirectivesViewer;
	//private String lastSelectedWorkspaceTextString;
	private ASTNode lastSelectedWorkspaceASTNode;

	private boolean isDirty = false;

	protected ToolBar toolBar;

	public static IFn FN_SNIPPET_ANCHOR;
	public static IFn FN_SNIPPET_VALUE_ANCHOR_RESOLVED;

	private ToolItem tltmRemove;

	private ToolItem tltmEditBoundDirectives;
	private ToolItem tltmRevealAnchor;
	private IUndoContext templateEditorUndoContext;
	private UndoRedoActionGroup templateEditorHistoryActionGroup;
		

	public TemplateEditor() {
		templateGroup = TemplateGroup.newFromGroupName("Anonymous Template Group");		
	}

	public void setGroup(TemplateGroup group) {
		templateGroup = group;
		refreshWidgets();
	}

	public TemplateGroupViewer getTemplateGroupViewer() {
		return templateGroupViewer;
	}
	
	public TemplateGroup getGroup() {
		return templateGroup;
	}
	
	public IUndoContext getUndoContext() {
		return templateEditorUndoContext;
	}
	

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {

		parent.setLayout(new GridLayout(1, false));

		toolBar = new ToolBar(parent, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

		ToolItem tltmAdd = new ToolItem(toolBar, SWT.NONE);
		tltmAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addSnippet();
			}
		});

		tltmAdd.setImage(EkekoSnippetsPlugin.IMG_ADD);
		tltmAdd.setToolTipText("Add template");
		
		ToolItem tltmAddFromText = new ToolItem(toolBar, SWT.NONE);
		tltmAddFromText.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					String data[] = {(String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor)};
					System.out.println(data[0]);
					Object nodes = TemplateGroup.parseStringsToNodes(data);
					ASTNode node = ((List<ASTNode>) nodes).get(0); 
					templateGroup.addSnippetCode(node);
					templateGroupViewer.clearSelection();
					refreshWidgets();
					becomeDirty();
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
			}
		});

		tltmAddFromText.setImage(EkekoSnippetsPlugin.IMG_ADD);
		tltmAddFromText.setToolTipText("Add template from clipboard");

		tltmRemove = new ToolItem(toolBar, SWT.NONE);
		tltmRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeSnippet();
			}
		});
		tltmRemove.setImage(EkekoSnippetsPlugin.IMG_DELETE);
		tltmRemove.setDisabledImage(EkekoSnippetsPlugin.IMG_DELETE_DISABLED);
		tltmRemove.setToolTipText("Delete template");
		tltmRemove.setEnabled(true);


		
		
		tltmEditBoundDirectives = new ToolItem(toolBar, SWT.NONE);
		tltmEditBoundDirectives.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onEditBoundDirectives(templateGroup, templateGroupViewer.getSelectedSnippet(), templateGroupViewer.getSelectedSnippetNode());
			}
		});
		tltmEditBoundDirectives.setImage(EkekoSnippetsPlugin.IMG_TEMPLATE_EDIT);
		tltmEditBoundDirectives.setToolTipText("Edit directives of template element");
		tltmEditBoundDirectives.setEnabled(false);

		


		tltmRevealAnchor = new ToolItem(toolBar, SWT.NONE);
		tltmRevealAnchor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				revealProjectAnchor();
			}
		});
		tltmRevealAnchor.setImage(EkekoSnippetsPlugin.IMG_ANCHOR);
		tltmRevealAnchor.setToolTipText("Reveal project anchor");
		tltmRevealAnchor.setEnabled(false);

		templateGroupViewer = new TemplateGroupViewer(parent, SWT.NONE);
		templateGroupViewer.setParentTemplateEditor(this);		GridData gd_templateGroupViewer = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);		gd_templateGroupViewer.heightHint = 400;		templateGroupViewer.setLayoutData(gd_templateGroupViewer);
		templateGroupViewer.setInput(templateGroup, null, null);

		templateGroupViewer.addNodeSelectionListener(new TemplateGroupViewerNodeSelectionListener() {	
			@Override
			public void nodeSelected(TemplateGroupViewerNodeSelectionEvent event) {
				onNodeSelected(event);
			}
		});


		/*

		Group snippetOperatorGroup = new Group(container, SWT.NONE);
		snippetOperatorGroup.setLayout(new GridLayout(1, false));

		ToolBar snippetOperatorGroupToolbar = new ToolBar(snippetOperatorGroup, SWT.FLAT | SWT.RIGHT);
		snippetOperatorGroupToolbar.setOrientation(SWT.RIGHT_TO_LEFT);
		snippetOperatorGroupToolbar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

		Label lblOperator = new Label(snippetOperatorGroup, SWT.NONE);
		GridData gd_lblOperator = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_lblOperator.heightHint = 22;
		lblOperator.setLayoutData(gd_lblOperator);
		lblOperator.setText("");

		 */

		/*
		ToolItem tltmApplyOperator = new ToolItem(snippetOperatorGroupToolbar, SWT.NONE);
		tltmApplyOperator.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onApplyOperator();
			}
		});
		tltmApplyOperator.setImage(ResourceManager.getPluginImage("org.eclipse.pde.ui", "/icons/etool16/validate.gif"));
		tltmApplyOperator.setToolTipText("Apply Operator");
		 */




		/*
		ToolItem undoOperator = new ToolItem(snippetOperatorGroupToolbar, SWT.NONE);
		undoOperator.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				undo();
			}
		});
		undoOperator.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/etool16/undo_edit.gif"));
		undoOperator.setToolTipText("Undo operator application");


		ToolItem redoOperator = new ToolItem(snippetOperatorGroupToolbar, SWT.NONE);
		redoOperator.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				redo();
			}
		});
		redoOperator.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/etool16/redo_edit.gif"));
		redoOperator.setToolTipText("Redo operator application");
		 */


		/*
		operatorOperandsViewer = new OperatorOperandsViewer(snippetOperatorGroup, SWT.NONE);				GridData gd_operatorOperandsViewer = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);		gd_operatorOperandsViewer.heightHint = 297;		operatorOperandsViewer.setLayoutData(gd_operatorOperandsViewer);
		 */

		/*
		boundDirectivesViewer = new BoundDirectivesViewer(snippetOperatorGroup, SWT.NONE);
		GridData gd_boundDirectivesViewer = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_boundDirectivesViewer.heightHint = 297;
		boundDirectivesViewer.setLayoutData(gd_boundDirectivesViewer);


		templateGroupViewer.addNodeSelectionListener(new TemplateGroupViewerNodeSelectionListener() {
			@Override
			public void nodeSelected(TemplateGroupViewerNodeSelectionEvent event) {
				//event.getSelectedTemplateGroup();
				//event.getSelectedTemplate();
				//event.getSelectedTemplateNode();
				updateOperators();
				updateBoundDirectives();
			}
		});
		 */


		templateGroupViewer.addNodeDoubleClickListener(new TemplateGroupViewerNodeDoubleClickListener() {
			@Override
			public void nodeDoubleClicked(TemplateGroupViewerNodeSelectionEvent event) {
				onEditBoundDirectives(event.getSelectedTemplateGroup(), event.getSelectedTemplate(), event.getSelectedTemplateNode());
			}
		});






		ISelectionListener sl = new ISelectionListener() {
			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection sel) {
				/*
				if(sel instanceof ITextSelection) {
					ITextSelection lastSelectedText = (ITextSelection) sel;
					if(!lastSelectedText.isEmpty())
						lastSelectedWorkspaceTextString = lastSelectedText.getText();
				}
				 */



				refreshWorkspaceSelection(part, sel);
			}

		};

		//code also listen for JavaEditor selections only (JavaUI.ID_CU_EDITOR),
		//but people might be using different editors (e.g., WindowBuilder)
		getSite().getPage().addSelectionListener(sl);


		//does not work reliably, so reverting
		//getSite().setSelectionProvider(templateGroupViewer);

		try {
			IViewPart view = getSite().getPage().showView(OperatorOperandsView.ID);
			OperatorOperandsView operatorOperandsView = (OperatorOperandsView) view;
			operatorOperandsView.shouldRegisterAsListenerTo(templateGroupViewer);
		} catch (PartInitException e1) {
			e1.printStackTrace();
		}				
	}

	public static ASTNode resolvedProjectAnchorOfTemplateValue(Object template, Object templateValue) {
		if(templateValue == null || template == null)
			return null;
		Object anchor = FN_SNIPPET_VALUE_ANCHOR_RESOLVED.invoke(template, templateValue);
		if(!(anchor instanceof ASTNode)) 
			return null;
		return (ASTNode) anchor;
	}	

	public static Object projectAnchorOfTemplate(Object template) {
		if(template == null)
			return null;
		Object anchor = FN_SNIPPET_ANCHOR.invoke(template);
		return anchor;
	}

	protected void onNodeSelected(TemplateGroupViewerNodeSelectionEvent event) {
		Object selectedTemplate = event.getSelectedTemplate();
		Object anchor = projectAnchorOfTemplate(selectedTemplate);
		if(anchor != null) {
			tltmRevealAnchor.setEnabled(true);
		} else {
			tltmRevealAnchor.setEnabled(false);
		}

		if(event.getSelectedTemplateNode() == null) {
			tltmRemove.setEnabled(false);
			tltmEditBoundDirectives.setEnabled(false);
		} else {
			tltmEditBoundDirectives.setEnabled(true);
			if(event.getSelectedTemplateNode().equals(TemplateGroup.getRootOfSnippet(event.getSelectedTemplate())))
				tltmRemove.setEnabled(true);
			else
				tltmRemove.setEnabled(false);
		}		
	}

	protected void revealProjectAnchor() {
		Object selectedSnippet = templateGroupViewer.getSelectedSnippet();
		Object selectedSnippetNode = templateGroupViewer.getSelectedSnippetNode();
		ASTNode anchorOfSelection = resolvedProjectAnchorOfTemplateValue(selectedSnippet, selectedSnippetNode);
		if(anchorOfSelection != null) { 
			MarkerUtility.getInstance().createMarkerAndGoto(anchorOfSelection);		
		}
	}

	//called by workspace listener
	protected void refreshWorkspaceSelection(IWorkbenchPart part, ISelection sel) {
		if(part instanceof IEditorPart) {
			IEditorPart editorPart = (IEditorPart) part;
			if(!(sel instanceof ITextSelection))
				return;
			ITextSelection selection = (ITextSelection) sel;
			ITypeRoot typeRoot = JavaUI.getEditorInputTypeRoot(editorPart.getEditorInput());
			if(typeRoot == null)
				return;
			ICompilationUnit icu = (ICompilationUnit) typeRoot.getAdapter(ICompilationUnit.class);
			if(icu == null)
				return;
			CompilationUnit cu = JavaProjectModel.parse(icu,null);
			if(cu == null)
				return;
			NodeFinder finder = new NodeFinder(cu, selection.getOffset(), selection.getLength());
			lastSelectedWorkspaceASTNode = finder.getCoveringNode();
		}
	}

	//manually called when view is opened for the first time
	public void setPreviouslyActiveEditor(IEditorPart activeEditor) {
		if(activeEditor instanceof ITextEditor) {
			ITextEditor textEditor = (ITextEditor) activeEditor;
			ITextSelection selection = (ITextSelection) textEditor.getSelectionProvider().getSelection();	
			if(selection != null)
				refreshWorkspaceSelection(textEditor, selection);
		}
	}

	protected void onEditBoundDirectives(TemplateGroup templateGroup, Object selectedTemplate, Object selectedNode) {
		BoundDirectivesEditorDialog dialog = new BoundDirectivesEditorDialog(getSite().getShell(), templateGroup, selectedTemplate, selectedNode);
		Object oldT = templateGroup.getGroup();
		dialog.open();
		Object newT = templateGroup.getGroup();
		assert(!oldT.equals(newT));
		refreshWidgets();
		becomeDirty();
	}


	@Override
	public void setFocus() {
		templateGroupViewer.setFocus();
	}

	public TemplateTreeContentProvider getContentProvider() {
		return contentProvider;
	}

	/*
	public void setViewID(String secondaryId) {
		this.viewID = secondaryId;
	}
	 */

	public void addSnippet() {
		if (lastSelectedWorkspaceASTNode != null) {
			templateGroup.addSnippetCode(lastSelectedWorkspaceASTNode);
			templateGroupViewer.clearSelection();
			refreshWidgets();
		}
		becomeDirty();
	}

	public void viewSnippet() {
		//removed plain snippet viewer
		//snippetGroup.viewSnippet(getSelectedSnippet());
	}

	public void removeSnippet() {
		Object selected = templateGroupViewer.getSelectedSnippet();
		if(selected == null)
			return;
		if(!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Delete template", "Are you sure you want to delete the selected template?")) 
			return;
		templateGroup.removeSnippet(selected);
		templateGroupViewer.clearSelection();
		refreshWidgets();
		becomeDirty();
	}

	/*
	public void viewQuery() {
		String query = templateGroup.getQuery(templateGroupViewer.getSelectedSnippet());
		QueryInspectorDialog dlg = new QueryInspectorDialog(Display.getCurrent().getActiveShell(),
				"Query", query, "\nExecute the Query?", null, null);
		dlg.create();

		if (dlg.open() == Window.OK) 
			runQuery();
	}
	 */

	public void runQuery() {
		templateGroup.runQuery(templateGroupViewer.getSelectedSnippet());
	}

	
	protected void refreshWidgets() {
		templateGroupViewer.clearSelection();
		templateGroupViewer.setInput(templateGroup, null, null);
		templateGroupViewer.updateWidgets();
	}


	@Override
	public void doSave(IProgressMonitor monitor) {
		IEditorInput input = getEditorInput();
		if(!(input instanceof TemplateEditorInput))
			return;
		String absoluteFilePathString;	
		ClojureFileEditorInput teinput = (ClojureFileEditorInput) input;
		if(!teinput.isAssociatedWithPersistentFile()) {
			FileDialog fileDialog = new FileDialog(getSite().getShell(), SWT.SAVE);
			fileDialog.setFilterExtensions(new String[] { "*.ekt" });
			fileDialog.setFilterNames(new String[] { "Ekeko/X template file (*.ekt)" });
			absoluteFilePathString = fileDialog.open();
			if(absoluteFilePathString == null)
				return;   
			teinput.setPathToPersistentFile(absoluteFilePathString);
		} else {
			absoluteFilePathString = teinput.getPathToPersistentFile();
		}
		try {
			TemplateEditorInput.serializeClojureTemplateGroup(templateGroup.getGroup(), absoluteFilePathString);
			isDirty = false;
			firePropertyChange(IEditorPart.PROP_DIRTY); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doSaveAs() {
		IEditorInput input = getEditorInput();
		if(!(input instanceof TemplateEditorInput))
			return;
		TemplateEditorInput newInput = new TemplateEditorInput();
		setInput(newInput); //fires no prop change, only sets field
		try {
			doSave(new NullProgressMonitor()); //serializes to new file
			updateInput(newInput); //deserializes from file
		} catch (Exception e) {
			e.printStackTrace();
			setInput(input);
		}
	}


	private void updateInput(IEditorInput input) throws PartInitException {
		setPartName(input.getName());

		if(input instanceof FileStoreEditorInput || input instanceof FileEditorInput) {
			String pathToFile = "";
			if(input instanceof FileStoreEditorInput) {
				//outside workspace
				FileStoreEditorInput fileInput = (FileStoreEditorInput) input;
				URI uri = fileInput.getURI();
				pathToFile = uri.getPath();
			} else 
				if(input instanceof FileEditorInput) {
					//within workspace
					FileEditorInput fileInput = (FileEditorInput) input;
					IFile ifile = fileInput.getFile();
					pathToFile = ifile.getLocation().toString();
				} else {
					setInput(new TemplateEditorInput());
					return;
				}
			TemplateEditorInput actualInput = new TemplateEditorInput();
			actualInput.setPathToPersistentFile(pathToFile);	
			setInput(actualInput);
			try {
				Object clojureTemplateGroup = TemplateEditorInput.deserializeClojureTemplateGroup(pathToFile);
				this.templateGroup = TemplateGroup.newFromClojureGroup(clojureTemplateGroup);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}

		if(input instanceof TemplateEditorInput) {
			TemplateEditorInput actualInput = (TemplateEditorInput) input;
			if(actualInput.associatedPersistentFileExists()) {
				try {
					Object clojureTemplateGroup = TemplateEditorInput.deserializeClojureTemplateGroup(actualInput.getPathToPersistentFile());
					this.templateGroup = TemplateGroup.newFromClojureGroup(clojureTemplateGroup);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			setInput(input);
			return;
		}

		throw new PartInitException("Unexpected input for TemplateEditor: " + input.toString());


	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		updateInput(input);
	    templateEditorUndoContext = new UndoContext();
		templateEditorHistoryActionGroup = new UndoRedoActionGroup(site, templateEditorUndoContext, true);
		templateEditorHistoryActionGroup.fillActionBars(site.getActionBars());
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	public void becomeDirty() {
		isDirty = true;
		firePropertyChange(IEditorPart.PROP_DIRTY); 
	}

	public void becomeClean() {
		isDirty = false;
		firePropertyChange(IEditorPart.PROP_DIRTY); 
	}
	/*
	@Override
	public void saveState(IMemento memento) {
		TemplateEditorInput input = (TemplateEditorInput) getEditorInput(); 
		IMemento storedTemplate = memento.createChild("Template");
		storedTemplate.putString("TemplateFilePath", input.getPathToPersistentFile());
	}

	@Override
	public void restoreState(IMemento memento) {
		storedTemplate.putString("TemplateFilePath", input.getPathToPersistentFile());
	}
	 */



}
