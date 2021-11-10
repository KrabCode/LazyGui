package toolbox.tree;

import com.jogamp.newt.event.MouseEvent;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.list.ListWindow;
import toolbox.style.Palette;
import toolbox.window.Window;
import toolbox.window.WindowManager;

public class TreeWindow extends Window {
    PVector contentTranslate = new PVector();

    private boolean isDraggedInside = false;
    TreeNode root = new TreeNode("");

    public TreeWindow(PApplet app, String path, String title, PVector pos, PVector size, boolean closeable) {
        super(app, path, title, pos, size, closeable);
    }

    protected void drawContent(PGraphics pg) {
        pg.pushMatrix();
        pg.translate(contentTranslate.x, contentTranslate.y);
        drawGrid(pg);
        pg.popMatrix();
    }

    @Override
    public void mousePressed(MouseEvent e, float x, float y) {
        super.mousePressed(e, x, y);
        if (isPointInsideContent(x, y)) {
            isDraggedInside = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e, float x, float y) {
        super.mouseReleased(e, x, y);
        if (!isDraggedInside && isPointInsideContent(x, y)) {
            // TODO draw path tree
            // TODO create/uncover windows based on clickable string position
            WindowManager.createOrUncoverWindow(
                    new ListWindow(app, "/test/", "test",
                            PVector.add(windowPos,
                                    new PVector(windowSize.x + 10, 0)),
                            new PVector(cellSize * 4, cellSize * 4))
            );
            e.setConsumed(true);
        } else if (isDraggedInside) {
            isDraggedInside = false;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e, float x, float y, float px, float py) {
        super.mouseDragged(e, x, y, px, py);
        if (!isDraggedAround) {
            if (isDraggedInside) {
                contentTranslate.x += x - px;
                contentTranslate.y += y - py;
            }
            e.setConsumed(true);
        }
    }
}
