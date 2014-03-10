package damp.ekeko.snippets.gui;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Table;

import clojure.lang.IFn;
import damp.ekeko.snippets.OperatorOperandBinding;

public class OperandBindingEditingSupport extends EditingSupport {

	public static IFn FN_OPERANDBINDING_EDITOR;
	
	TableViewer operandsTableViewer;
	
	public OperandBindingEditingSupport(TableViewer viewer) {
		super(viewer);
		operandsTableViewer = viewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		Table table = operandsTableViewer.getTable();
		return (CellEditor) FN_OPERANDBINDING_EDITOR.invoke(table, element);
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		return ((OperatorOperandBinding) element).getValue();
	}

	@Override
	protected void setValue(Object element, Object value) {
		((OperatorOperandBinding) element).setValue(value);
		operandsTableViewer.update(element, null);
	}

}
