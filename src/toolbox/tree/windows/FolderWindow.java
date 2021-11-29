package toolbox.tree.windows;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.global.State;
import toolbox.global.Utils;
import toolbox.tree.rows.*;
import toolbox.tree.rows.AbstractRow;
import toolbox.tree.rows.ToolbarRow;

import static processing.core.PApplet.println;

/**
 * A FolderWindow is the only visible GUI element
 * lets the user see its child rows including folders and interact with them
 * to either change a row value or open a new FolderWindow
*/
public class FolderWindow extends Window {
    public final FolderRow parentFolder;

    public FolderWindow(PVector pos, FolderRow parentFolder, boolean closeable) {
        super(pos, parentFolder, closeable);
        this.parentFolder = parentFolder;
        parentFolder.window = this;
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
        for (int i = 0; i < parentFolder.children.size(); i++) {
            AbstractRow row = parentFolder.children.get(i);
            float rowHeight = cell * row.rowCount;
            row.updateNodeCoordinates(pos.x, pos.y + y, size.x, rowHeight);
            pg.pushMatrix();
            pg.pushStyle();
            row.drawNode(pg);
            pg.popMatrix();
            pg.popStyle();
            y += rowHeight;
            pg.translate(0, rowHeight);
        }
        pg.popMatrix();
    }

    private float heightSumOfChildNodes() {
        float sum = 0;
        for(AbstractRow child : parentFolder.children){
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
            AbstractRow row = tryFindChildNode(x, y);
            if (row != null && !row.isParentWindowHidden()) {
                row.rowPressed(x, y);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e, float x, float y, float px, float py) {
        super.mouseMoved(e, x, y, px, py);
        for (AbstractRow row : parentFolder.children) {
            row.isMouseOverRow = false;
        }
        if (isPointInsideContent(x, y)) {
            AbstractRow row = tryFindChildNode(x, y);
            if (row != null && !row.isParentWindowHidden()) {
                row.isMouseOverRow = true;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e, float x, float y) {
        super.mouseReleased(e, x, y);
        for (AbstractRow row : parentFolder.children) {
            row.mouseReleasedAnywhere(x, y);
        }
        if (isPointInsideContent(x, y)) {
            AbstractRow clickedRow = tryFindChildNode(x, y);
            if (clickedRow != null && !parentRow.isParentWindowHidden()) {
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
            AbstractRow clickedRow = tryFindChildNode(x, y);
            if (clickedRow != null  && !parentRow.isParentWindowHidden()) {
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
            AbstractRow clickedRow = tryFindChildNode(x, y);
            if (clickedRow != null && !parentRow.isParentWindowHidden()) {
                clickedRow.keyPressedOverRow(keyEvent, x, y);
            }
        }
    }

    private AbstractRow tryFindChildNode(float x, float y) {
        for (AbstractRow row : parentFolder.children) {
            if (Utils.isPointInRect(x, y, row.pos.x, row.pos.y, row.size.x, row.size.y)) {
                return row;
            }
        }
        return null;
    }

    @Override
    public void mouseDragged(MouseEvent e, float x, float y, float px, float py) {
        super.mouseDragged(e, x, y, px, py);
        for(AbstractRow child : parentFolder.children){
            if(child.isDragged && !child.isParentWindowHidden()){
                child.mouseDragRowContinue(e, x, y, px, py);
            }
        }
    }

    public void createToolbar() {
        ToolbarRow row = new ToolbarRow(parentFolder.path + "/toolbar", parentFolder);
        parentFolder.children.add(row);
    }
}
