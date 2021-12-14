package toolbox.windows;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.global.State;
import toolbox.global.Utils;
import toolbox.windows.nodes.*;
import toolbox.windows.nodes.AbstractNode;
import toolbox.windows.nodes.ToolbarNode;

import static processing.core.PApplet.println;

/**
 * A FolderWindow is the only visible GUI element
 * lets the user see its child nodes including folders and interact with them
 * to either change a node value or open a new FolderWindow
 */
public class FolderWindow extends Window {
    public final FolderNode parentFolder;

    public FolderWindow(PVector pos, FolderNode parentFolder, boolean closeable) {
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
            AbstractNode node = parentFolder.children.get(i);
            float nodeHeight = cell * node.heightMultiplier;
            node.updateNodeCoordinates(pos.x, pos.y + y, size.x, nodeHeight);
            pg.pushMatrix();
            pg.pushStyle();
            node.drawNode(pg);
            pg.popMatrix();
            pg.popStyle();
            y += nodeHeight;
            pg.translate(0, nodeHeight);
        }
        pg.popMatrix();
    }

    private float heightSumOfChildNodes() {
        float sum = 0;
        for (AbstractNode child : parentFolder.children) {
            sum += child.heightMultiplier * cell;
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
            AbstractNode node = tryFindChildNode(x, y);
            if (node != null && !node.isParentWindowHidden()) {
                node.nodeClicked(x, y);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e, float x, float y, float px, float py) {
        super.mouseMoved(e, x, y, px, py);
        for (AbstractNode node : parentFolder.children) {
            node.isMouseOverNode = false;
        }
        if (isPointInsideContent(x, y)) {
            AbstractNode node = tryFindChildNode(x, y);
            if (node != null && !node.isParentWindowHidden()) {
                node.isMouseOverNode = true;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e, float x, float y) {
        super.mouseReleased(e, x, y);
        for (AbstractNode node : parentFolder.children) {
            node.mouseReleasedAnywhere(x, y);
        }
        if (isPointInsideContent(x, y)) {
            AbstractNode clickedNode = tryFindChildNode(x, y);
            if (clickedNode != null && !parentNode.isParentWindowHidden()) {
                clickedNode.mouseReleasedOverNode(x, y);
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
            AbstractNode clickedNode = tryFindChildNode(x, y);
            if (clickedNode != null && !parentNode.isParentWindowHidden()) {
                clickedNode.mouseWheelMovedOverNode(x, y, dir);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        super.keyPressed(keyEvent);
        float x = State.app.mouseX;
        float y = State.app.mouseY;
        AbstractNode nodeUnderMouse = tryFindChildNode(x, y);
        if (nodeUnderMouse != null && !parentNode.isParentWindowHidden()) {
            if (isPointInsideContent(x, y)) {
                nodeUnderMouse.keyPressedOverNode(keyEvent, x, y);
            }
        }
        for(AbstractNode anyNode : parentFolder.children){
            anyNode.keyPressedOutOfNode(keyEvent, x, y);
        }
    }

    private AbstractNode tryFindChildNode(float x, float y) {
        for (AbstractNode node : parentFolder.children) {
            if (Utils.isPointInRect(x, y, node.pos.x, node.pos.y, node.size.x, node.size.y)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public void mouseDragged(MouseEvent e, float x, float y, float px, float py) {
        super.mouseDragged(e, x, y, px, py);
        for (AbstractNode child : parentFolder.children) {
            if (child.isDragged && !child.isParentWindowHidden()) {
                child.mouseDragNodeContinue(e, x, y, px, py);
            }
        }
    }

    public void createToolbarNode() {
        ToolbarNode node = new ToolbarNode(parentFolder.path + "/toolbar", parentFolder);
        parentFolder.children.add(node);
    }

    public void createStateListFolderNode() {
        StateListFolderNode stateListFolderNode = new StateListFolderNode(parentFolder.path + "/load", parentFolder);
        parentFolder.children.add(stateListFolderNode);
    }
}
