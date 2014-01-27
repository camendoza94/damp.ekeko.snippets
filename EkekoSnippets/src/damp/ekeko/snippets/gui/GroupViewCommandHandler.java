package damp.ekeko.snippets.gui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class GroupViewCommandHandler extends AbstractHandler {
	public GroupViewCommandHandler() {
	}


	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		try {
			window.getActivePage().showView("damp.ekeko.snippets.gui.GroupView");
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}
}
