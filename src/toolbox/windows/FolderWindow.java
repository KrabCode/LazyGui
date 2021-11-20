package toolbox.windows;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.GlobalState;
import toolbox.MathUtils;
import toolbox.tree.nodes.FolderNode;
import toolbox.tree.Node;

// GUI element that lets the user see nodes incl. folders to click on and alter
public class FolderWindow extends Window {
    public final FolderNode folder;

    public FolderWindow(PVector pos, PVector size, FolderNode folder, boolean closeable) {
        super(pos, size, folder, closeable);
        this.folder = folder;
        folder.window = this;
    }

    float nodeHeight = cell;

    @Override
    protected void drawContent(PGraphics pg) {
        drawFolder(pg);
    }

    public void drawFolder(PGraphics pg) {
        size.y = cell + cell * folder.children.size();
        pg.pushMatrix();
        pg.translate(pos.x, pos.y);
        pg.translate(0, titleBarHeight);
        for (int i = 0; i < folder.children.size(); i++) {
            Node node = folder.children.get(i);
            node.updateNodeCoordinates(pg.screenX(0, 0), pg.screenY(0, 0), size.x, nodeHeight);
            node.drawNode(pg);
            pg.translate(0, nodeHeight);
        }
        pg.popMatrix();
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
            node.mouseReleased(x, y);
        }
        if (isPointInsideContent(x, y)) {
            Node clickedNode = tryFindChildNode(x, y);
            if (clickedNode != null) {
                clickedNode.nodeReleased(x, y);
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
                clickedNode.mouseWheelMovedInsideNode(x, y, dir);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        super.keyPressed(keyEvent);
        float x = GlobalState.app.mouseX;
        float y = GlobalState.app.mouseY;
        if (isPointInsideContent(x, y)) {
            Node clickedNode = tryFindChildNode(x, y);
            if (clickedNode != null) {
                clickedNode.keyPressedInsideNode(keyEvent, x, y);
            }
        }
    }

    private Node tryFindChildNode(float x, float y) {
        for (Node node : folder.children) {
            if (MathUtils.isPointInRect(x, y, node.pos.x, node.pos.y, node.size.x, node.size.y)) {
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
                child.mouseDragged(e, x, y, px, py);
            }
        }
    }
}
