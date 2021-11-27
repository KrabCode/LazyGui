package toolbox.windows;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.global.State;
import toolbox.global.Utils;
import toolbox.tree.rows.*;
import toolbox.tree.rows.Row;
import toolbox.tree.rows.ToolbarRow;

import static processing.core.PApplet.println;

/**
 * A FolderWindow is the only visible GUI element
 * lets the user see its child nodes including folders and interact with them
 * to either change a node value or open a new FolderWindow
*/
public class FolderWindow extends Window {
    public final FolderRow folder;

    public FolderWindow(PVector pos, FolderRow folder, boolean closeable) {
        super(pos, folder, closeable);
        this.folder = folder;
        folder.window = this;
    }

    @Override
    protected void drawContent(PGraphics pg) {
        drawFolder(pg);
    }

    public void drawFolder(PGraphics pg) {
        size.y = cell + heightSumOfChildNodes();
        pg.pushMatrix();
        pg.translate(pos.x, pos.y);
        pg.translate(0, titleBarHeight);
        float y = titleBarHeight;
        for (int i = 0; i < folder.children.size(); i++) {
            Row row = folder.children.get(i);
            float nodeHeight = cell * row.rowCount;
            row.updateNodeCoordinates(pos.x, pos.y + y, size.x, nodeHeight);
            pg.pushMatrix();
            pg.pushStyle();
            row.drawNode(pg);
            pg.popMatrix();
            pg.popStyle();
            y += nodeHeight;
            pg.translate(0, nodeHeight);
        }
        pg.popMatrix();
    }

    private float heightSumOfChildNodes() {
        float sum = 0;
        for(Row child : folder.children){
            sum += child.rowCount * cell;
        }
        return sum;
    }


    @Override
    public void mousePressed(MouseEvent e, float x, float y) {
        super.mousePressed(e, x, y);
        if (isPointInsideTitleBar(x, y)) {
            return;
        }
        if (isPointInsideContent(x, y)) {
            Row clickedRow = tryFindChildNode(x, y);
            if (clickedRow != null) {
                clickedRow.rowPressed(x, y);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e, float x, float y, float px, float py) {
        super.mouseMoved(e, x, y, px, py);
        for (Row row : folder.children) {
            row.mouseOver = false;
        }
        if (isPointInsideContent(x, y)) {
            Row row = tryFindChildNode(x, y);
            if (row != null) {
                row.mouseOver = true;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e, float x, float y) {
        super.mouseReleased(e, x, y);
        for (Row row : folder.children) {
            row.mouseReleasedAnywhere(x, y);
        }
        if (isPointInsideContent(x, y)) {
            Row clickedRow = tryFindChildNode(x, y);
            if (clickedRow != null) {
                clickedRow.mouseReleasedOverRow(x, y);
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseEvent e, int dir, float x, float y) {
        super.mouseWheelMoved(e, dir, x, y);
        if (isPointInsideTitleBar(x, y)) {
            return;
        }
        if (isPointInsideContent(x, y)) {
            Row clickedRow = tryFindChildNode(x, y);
            if (clickedRow != null) {
                clickedRow.mouseWheelMovedOverRow(x, y, dir);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        super.keyPressed(keyEvent);
        float x = State.app.mouseX;
        float y = State.app.mouseY;
        if (isPointInsideContent(x, y)) {
            Row clickedRow = tryFindChildNode(x, y);
            if (clickedRow != null) {
                clickedRow.keyPressedOverRow(keyEvent, x, y);
            }
        }
    }

    private Row tryFindChildNode(float x, float y) {
        for (Row row : folder.children) {
            if (Utils.isPointInRect(x, y, row.pos.x, row.pos.y, row.size.x, row.size.y)) {
                return row;
            }
        }
        return null;
    }

    @Override
    public void mouseDragged(MouseEvent e, float x, float y, float px, float py) {
        super.mouseDragged(e, x, y, px, py);
        for(Row child : folder.children){
            if(child.isDragged){
                child.mouseDragRowContinue(e, x, y, px, py);
            }
        }
    }

    public void createToolbar() {
        ToolbarRow node = new ToolbarRow(folder.path + "/toolbar", folder);
        folder.children.add(node);
    }
}
