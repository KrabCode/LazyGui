package toolbox.windows;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.global.State;
import toolbox.global.Utils;
import toolbox.tree.nodes.FolderNode;
import toolbox.tree.nodes.Node;
import toolbox.tree.nodes.NodeType;
import toolbox.tree.nodes.ToolbarNode;

import java.awt.*;

import static processing.core.PApplet.println;

/**
 * A FolderWindow is the only visible GUI element
 * lets the user see its child nodes including folders and interact with them
 * to either change a node value or open a new FolderWindow
*/
public class FolderWindow extends Window {
    public final FolderNode folder;

    public FolderWindow(PVector pos, FolderNode folder, boolean closeable) {
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
            Node node = folder.children.get(i);
            float nodeHeight = cell * node.rowCount;
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
        for(Node child : folder.children){
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
            Node clickedNode = tryFindChildNode(x, y);
            if (clickedNode != null) {
                clickedNode.nodePressed(x, y);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e, float x, float y, float px, float py) {
        super.mouseMoved(e, x, y, px, py);
        for (Node node : folder.children) {
            node.mouseOver = false;
        }
        if (isPointInsideContent(x, y)) {
            Node node = tryFindChildNode(x, y);
            if (node != null) {
                node.mouseOver = true;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e, float x, float y) {
        super.mouseReleased(e, x, y);
        for (Node node : folder.children) {
            node.mouseReleasedAnywhere(x, y);
        }
        if (isPointInsideContent(x, y)) {
            Node clickedNode = tryFindChildNode(x, y);
            if (clickedNode != null) {
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
            Node clickedNode = tryFindChildNode(x, y);
            if (clickedNode != null) {
                clickedNode.mouseWheelMovedOverNode(x, y, dir);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        super.keyPressed(keyEvent);
        float x = State.app.mouseX;
        float y = State.app.mouseY;
        if (isPointInsideContent(x, y)) {
            Node clickedNode = tryFindChildNode(x, y);
            if (clickedNode != null) {
                clickedNode.keyPressedOverNode(keyEvent, x, y);
            }
        }
    }

    private Node tryFindChildNode(float x, float y) {
        for (Node node : folder.children) {
            if (Utils.isPointInRect(x, y, node.pos.x, node.pos.y, node.size.x, node.size.y)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public void mouseDragged(MouseEvent e, float x, float y, float px, float py) {
        super.mouseDragged(e, x, y, px, py);
        for(Node child : folder.children){
            if(child.isDragged){
                child.mouseDragNodeContinue(e, x, y, px, py);
            }
        }
    }

    public void createToolbar() {
        ToolbarNode node = new ToolbarNode(folder.path + "/toolbar", folder);
        folder.children.add(node);
    }
}
