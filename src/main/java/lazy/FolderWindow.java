package lazy;


import processing.core.PGraphics;

import static lazy.State.cell;


/**
 * A FolderWindow is the only visible GUI element
 * it lets the user see its child nodes including folders and interact with them
 */
class FolderWindow extends Window {
    final FolderNode folder;

    FolderWindow(float posX, float posY, FolderNode folder, boolean closeable) {
        super(posX, posY, folder, closeable);
        this.folder = folder;
        folder.window = this;
    }

    @Override
    protected void drawContent(PGraphics pg) {
        drawInlineFolderChildren(pg);
    }

    void drawInlineFolderChildren(PGraphics pg) {
        float intendedWindowWidthInCells = folder.idealWindowWidth;;
        windowSizeY = cell + heightSumOfChildNodes();
        windowSizeX = cell * intendedWindowWidthInCells;
        pg.pushMatrix();
        pg.translate(posX, posY);
        pg.translate(0, cell);
        float y = cell;
        for (int i = 0; i < folder.children.size(); i++) {
            AbstractNode node = folder.children.get(i);
            float nodeHeight = cell * node.rowHeightInCells;
            node.updateInlineNodeCoordinates(posX, posY + y, windowSizeX, nodeHeight);
            pg.pushMatrix();
            pg.pushStyle();
            node.updateDrawInlineNode(pg);
            pg.popStyle();
            pg.popMatrix();
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
    public void mousePressed(LazyMouseEvent e) {
        super.mousePressed(e);
        if (isPointInsideTitleBar(e.getX(), e.getY())) {
            return;
        }
        if (isPointInsideContent(e.getX(), e.getY())) {
            AbstractNode node = tryFindChildNodeAt(e.getX(), e.getY());
            if (node != null && node.isParentWindowVisible()) {
                node.mousePressedOverNode(e.getX(), e.getY());
            }
        }
    }

    @Override
    public void mouseMoved(LazyMouseEvent e) {
        super.mouseMoved(e);
        if (isPointInsideTitleBar(e.getX(), e.getY())) {
            e.setConsumed(true);
            owner.setIsMouseOverThisNodeOnly();
        } else if (isPointInsideContent(e.getX(), e.getY())) {
            AbstractNode node = tryFindChildNodeAt(e.getX(), e.getY());
            if (node != null && node.isParentWindowVisible()) {
                node.setIsMouseOverThisNodeOnly();
                e.setConsumed(true);
            }
        } else {
            NodeTree.setAllOtherNodesMouseOverToFalse(null);
        }
    }

    @Override
    public void mouseReleased(LazyMouseEvent e) {
        super.mouseReleased(e);
        for (AbstractNode node : folder.children) {
            node.mouseReleasedAnywhere(e);
        }
        if (isPointInsideContent(e.getX(), e.getY())) {
            AbstractNode clickedNode = tryFindChildNodeAt(e.getX(), e.getY());
            if (clickedNode != null && clickedNode.isParentWindowVisible()) {
                clickedNode.mouseReleasedOverNode(e.getX(), e.getY());
                e.setConsumed(true);
            }
        }
    }

    @Override
    public void mouseWheelMoved(LazyMouseEvent e) {
        super.mouseWheelMoved(e);
        if (isPointInsideTitleBar(e.getX(), e.getY())) {
            return;
        }
        if (isPointInsideContent(e.getX(), e.getY())) {
            AbstractNode clickedNode = tryFindChildNodeAt(e.getX(), e.getY());
            if (clickedNode != null && clickedNode.isParentWindowVisible()) {
                clickedNode.mouseWheelMovedOverNode(e.getX(), e.getY(), e.getRotation());
            }
        }
    }

    @Override
    public void keyPressed(LazyKeyEvent keyEvent) {
        super.keyPressed(keyEvent);
        float x = State.app.mouseX;
        float y = State.app.mouseY;
        AbstractNode nodeUnderMouse = tryFindChildNodeAt(x, y);
        if (nodeUnderMouse != null && nodeUnderMouse.isParentWindowVisible()) {
            if (isPointInsideContent(x, y)) {
                nodeUnderMouse.keyPressedOverNode(keyEvent, x, y);
            }
        }
        for (AbstractNode anyNode : folder.children) {
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
    public void mouseDragged(LazyMouseEvent e) {
        super.mouseDragged(e);
        for (AbstractNode child : folder.children) {
            if (child.isDragged && child.isParentWindowVisible()) {
                child.mouseDragNodeContinue(e);
            }
        }
    }

}
