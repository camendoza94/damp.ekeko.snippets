package damp.ekeko.snippets.gui;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import clojure.lang.RT;

public class TemplateViewTreeContentProvider implements ITreeContentProvider {

	private Object group;
	private TreeViewer viewer;
	
	
	private static String ns_gui = "damp.ekeko.snippets.gui";
	
	
	public TemplateViewTreeContentProvider() {
	}
	
	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TreeViewer) viewer;
		group = newInput;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement == null)
			return null;
		return (Object[]) RT.var(ns_gui, "templateviewtreecontentprovider-elements").invoke(getGroup(), inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement == null)
			return null;
		return (Object[]) RT.var(ns_gui, "templateviewtreecontentprovider-children").invoke(getGroup(), parentElement);
	}

	@Override
	public Object getParent(Object element) {
		if (element == null)
			return null;
		return RT.var(ns_gui, "templateviewtreecontentprovider-parent").invoke(getGroup(), element);
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element == null)
			return false;
		return getChildren(element).length > 0;

	}

	public Object getGroup() {
		return group;
	}


}