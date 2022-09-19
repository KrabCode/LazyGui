package lazy.windows;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import lazy.NodeTree;
import lazy.State;
import lazy.Utils;
import lazy.windows.nodes.*;


/**
 * A FolderWindow is the only visible GUI element
 * it lets the user see its child nodes including folders and interact with them
 */
public class FolderWindow extends Window {
    public final NodeFolder folder;

    public FolderWindow(float posX, float posY, NodeFolder folder, boolean closeable) {
        super(posX, posY, folder, closeable);
        this.folder = folder;
        folder.window = this;
    }

    public FolderWindow(float posX, float posY, NodeFolder folder, boolean closeable, float intendedWindowWidth) {
        super(posX, posY, folder, closeable);
        this.folder = folder;
        folder.window = this;
        if(intendedWindowWidth > 0){
            windowSizeX = intendedWindowWidth;
        }
    }

    @Override
    protected void drawContent(PGraphics pg) {
        drawFolder(pg);
    }

    public void drawFolder(PGraphics pg) {
        windowSizeY = cell + heightSumOfChildNodes();
        pg.pushMatrix();
        pg.translate(posX -0.5f, posY);
        pg.translate(0, titleBarHeight);
        float y = titleBarHeight;
        for (int i = 0; i < folder.children.size(); i++) {
            AbstractNode node = folder.children.get(i);
            float nodeHeight = cell * node.rowHeightInCells;
            node.updateInlineNodeCoordinates(posX, posY + y, windowSizeX, nodeHeight);
            pg.pushMatrix();
            pg.pushStyle();
            node.updateDrawInlineNode(pg);
            pg.popMatrix();
            pg.popStyle();
            y += nodeHeight;
            pg.translate(0, nodeHeight);
        }
        pg.popMatrix();
    }

    private float heightSumOfChildNodes() {
        float sum = 0;
        for (AbstractNode child : folder.children) {
            sum += child.rowHeightInCells * cell;
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
            AbstractNode node = tryFindChildNodeAt(x, y);
            if (node != null && node.isParentWindowVisible()) {
                node.mousePressedOverNode(x, y);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e, float x, float y, float px, float py) {
        super.mouseMoved(e, x, y, px, py);
        if(isPointInsideTitleBar(x,y)){
            e.setConsumed(true);
            NodeTree.setAllOtherNodesMouseOver(null, false);
            return;
        }
        if (isPointInsideContent(x, y)) {
            AbstractNode node = tryFindChildNodeAt(x, y);
            if (node != null && node.isParentWindowVisible()) {
                node.isMouseOverNode = true;
                NodeTree.setAllOtherNodesMouseOver(node, false);
                e.setConsumed(true);
            }
        } else {
            for(AbstractNode child : folder.children){
                child.isMouseOverNode = false;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e, float x, float y) {
        super.mouseReleased(e, x, y);
        for (AbstractNode node : folder.children) {
            node.mouseReleasedAnywhere(e, x, y);
        }
        if (isPointInsideContent(x, y)) {
            AbstractNode clickedNode = tryFindChildNodeAt(x, y);
            if (clickedNode != null && clickedNode.isParentWindowVisible()) {
                clickedNode.mouseReleasedOverNode(x, y);
                e.setConsumed(true);
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
            AbstractNode clickedNode = tryFindChildNodeAt(x, y);
            if (clickedNode != null && clickedNode.isParentWindowVisible()) {
                clickedNode.mouseWheelMovedOverNode(x, y, dir);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        super.keyPressed(keyEvent);
        float x = State.app.mouseX;
        float y = State.app.mouseY;
        AbstractNode nodeUnderMouse = tryFindChildNodeAt(x, y);
        if (nodeUnderMouse != null && nodeUnderMouse.isParentWindowVisible()) {
            if (isPointInsideContent(x, y)) {
                nodeUnderMouse.keyPressedOverNode(keyEvent, x, y);
            }
        }
        for(AbstractNode anyNode : folder.children){
            anyNode.keyPressedOutOfNode(keyEvent, x, y);
        }
    }

    private AbstractNode tryFindChildNodeAt(float x, float y) {
        for (AbstractNode node : folder.children) {
            if (Utils.isPointInRect(x, y, node.pos.x, node.pos.y, node.size.x, node.size.y)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public void mouseDragged(MouseEvent e, float x, float y, float px, float py) {
        super.mouseDragged(e, x, y, px, py);
        for (AbstractNode child : folder.children) {
            if (child.isDragged && child.isParentWindowVisible()) {
                child.mouseDragNodeContinue(e, x, y, px, py);
            }
        }
    }

}
