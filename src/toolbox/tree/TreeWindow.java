package toolbox.tree;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.window.Window;

public class TreeWindow extends Window {
    PVector contentTranslateOrigin = new PVector(0, cell);
    PVector contentTranslate = contentTranslateOrigin.copy();

    private boolean isDraggedInside = false;
    TreeNode root = new TreeNode("");

    public TreeWindow(PApplet app, String path, String title, PVector pos, PVector size, boolean closeable) {
        super(app, path, title, pos, size, closeable);
    }

    protected void drawContent(PGraphics pg) {
        pg.pushMatrix();
        pg.translate(contentTranslate.x, contentTranslate.y);
        drawGrid(pg);
        drawTree(pg);
        pg.popMatrix();
    }

    private void drawTree(PGraphics pg) {
        drawNodeRecursively(pg, root);
    }

    private void drawNodeRecursively(PGraphics pg, TreeNode parent){

//        drawNodeRecursively(pg, child);
    }

    private void tryInteractWithTree() {

    }

    @Override
    public void mousePressed(MouseEvent e, float x, float y) {
        super.mousePressed(e, x, y);
    }

    @Override
    public void mouseReleased(MouseEvent e, float x, float y) {
        super.mouseReleased(e, x, y);
        if (!isDraggedInside && isPointInsideContent(x, y)) {
            tryInteractWithTree();
            e.setConsumed(true);
        } else if (isDraggedInside) {
            isDraggedInside = false;
            e.setConsumed(true);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e, float x, float y, float px, float py) {
        super.mouseDragged(e, x, y, px, py);
        if (!isDraggedAround) {
            if (isDraggedInside) {
                contentTranslate.x += x - px;
                contentTranslate.y += y - py;
            } else if (isPointInsideContent(x, y)) {
                isDraggedInside = true;
            }
            e.setConsumed(true);
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        super.keyPressed(keyEvent);
        if(keyEvent.getKeyChar() == 'r'){
            contentTranslate = contentTranslateOrigin.copy();
        }
    }
}
