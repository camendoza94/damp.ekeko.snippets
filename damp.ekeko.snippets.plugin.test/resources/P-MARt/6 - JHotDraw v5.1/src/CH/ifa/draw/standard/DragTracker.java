/*
 * @(#)DragTracker.java 5.1
 *
 */

package CH.ifa.draw.standard;

import java.awt.event.MouseEvent;

import CH.ifa.draw.framework.DrawingView;
import CH.ifa.draw.framework.Figure;
import CH.ifa.draw.framework.FigureEnumeration;

/**
 * DragTracker implements the dragging of the clicked
 * figure.
 *
 * @see SelectionTool
 */
public class DragTracker extends AbstractTool {

	private Figure  fAnchorFigure;
	private int     fLastX, fLastY;      // previous mouse position
	private boolean fMoved = false;

	public DragTracker(DrawingView view, Figure anchor) {
		super(view);
		fAnchorFigure = anchor;
	}

	public void mouseDown(MouseEvent e, int x, int y) {
		super.mouseDown(e, x, y);
		fLastX = x;
		fLastY = y;

		if (e.isShiftDown()) {
		   view().toggleSelection(fAnchorFigure);
		   fAnchorFigure = null;
		} else if (!view().selection().contains(fAnchorFigure)) {
			view().clearSelection();
			view().addToSelection(fAnchorFigure);
		}
	}

	public void mouseDrag(MouseEvent e, int x, int y) {
		super.mouseDrag(e, x, y);
		fMoved = (Math.abs(x - fAnchorX) > 4) || (Math.abs(y - fAnchorY) > 4);

		if (fMoved) {
			FigureEnumeration figures = view().selectionElements();
			while (figures.hasMoreElements())
				figures.nextFigure().moveBy(x - fLastX, y - fLastY);
		}
		fLastX = x;
		fLastY = y;
	}
}
