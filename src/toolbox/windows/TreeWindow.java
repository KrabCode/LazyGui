package toolbox.windows;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.ToolboxMath;
import toolbox.tree.Node;
import toolbox.windows.TestWindow;
import toolbox.style.Palette;
import toolbox.window.Window;
import toolbox.window.WindowManager;

import static processing.core.PConstants.*;

public class TreeWindow extends Window {
    PVector contentTranslateOrigin = new PVector(0, cell);
    PVector contentTranslate = contentTranslateOrigin.copy();
    PVector treeDrawingOrigin = new PVector(cell, cell);

    private boolean isDraggedInside = false;
    public Node root = new Node(app, "/", "root");

    public TreeWindow(PApplet app, String path, String title, PVector pos, PVector size, boolean closeable) {
        super(app, path, title, pos, size, closeable);
        root.children.add(new Node(app, "/test/", "test"));
        // TODO make more than 1 recursive level translation work
//        Node hello = new Node(app, "/hello/", "hello");
//        hello.children.add(new Node(app, "/hello/world/", "world"));
//        root.children.add(hello);
        root.children.add(new Node(app, "/stroke/", "stroke"));
        root.children.add(new Node(app, "/count/", "count"));
    }

    protected void drawContent(PGraphics pg) {
        pg.pushMatrix();
        pg.translate(contentTranslate.x, contentTranslate.y);
        drawGrid(pg);
        drawTree(pg);
        pg.popMatrix();
    }

    private void drawTree(PGraphics pg) {
        pg.pushMatrix();
        pg.translate(treeDrawingOrigin.x, treeDrawingOrigin.y);
        drawTreeNodeRecursively(pg, root);
        pg.popMatrix();
    }

    private void drawTreeNodeRecursively(PGraphics pg, Node parent) {
        pg.stroke(Palette.windowTitleTextFill);
        if(parent.children.size() > 0){
            pg.strokeWeight(3);
            pg.line(cell / 2f, cell * 1.5f, cell / 2f, cell * 0.5f + parent.children.size() * cell);
        }
        pg.strokeWeight(1);
        updateDrawNodeHitbox(pg, parent, true);

        pg.textAlign(LEFT, TOP);
        pg.fill(Palette.windowTitleTextFill);
        pg.noStroke();
        pg.text(parent.name, 0, 0);

        if(parent.children.size() > 0){
            pg.pushMatrix();
            pg.translate(cell, 0);
            for (Node child : parent.children) {
                pg.translate(0, cell);
                drawTreeNodeRecursively(pg, child);
            }
            pg.popMatrix();
        }
    }

    private void updateDrawNodeHitbox(PGraphics pg, Node parent, boolean hidden) {
        float paddingX = cell / 2f;
        float x = -paddingX / 2f;
        float y = 0;
        float w = pg.textWidth(parent.name) + paddingX;
        float h = cell;
        parent.screenPos.x = windowPos.x + pg.screenX(x, y);
        parent.screenPos.y = windowPos.y + pg.screenY(x, y);
        parent.screenSize.x = w;
        parent.screenSize.y = h;
        if (!hidden) {
            pg.fill(Palette.windowContentFill);
            pg.stroke(Palette.windowBorderStroke);
            pg.rect(x,y,w,h);
        }
    }

    public void debugHitboxes(PGraphics pg, Node parent) {
        pg.noFill();
        pg.stroke(0.6f,1,1);
        pg.rect(parent.screenPos.x, parent.screenPos.y, parent.screenSize.x ,parent.screenSize.y);
        for(Node child : parent.children){
            debugHitboxes(pg, child);
        }
    }

    private void tryInteractWithTree(float x, float y) {
        Node hitboxMatch = tryFindHitboxUnderPointRecursively(root, x, y);
        if(hitboxMatch != null){
            WindowManager.createOrUncoverWindow(
                    new TestWindow(app, hitboxMatch.path, hitboxMatch.name,
                            new PVector(windowPos.x + windowSize.x + cell, windowPos.y),
                            new PVector(cell * 6, cell * 6))
            );
        }
    }

    private Node tryFindHitboxUnderPointRecursively(Node parent, float x, float y) {
        if(ToolboxMath.isPointInRect(x, y, parent.screenPos.x, parent.screenPos.y, parent.screenSize.x, parent.screenSize.y)){
            return parent;
        }
        for(Node child : parent.children){
            Node potentialHit = tryFindHitboxUnderPointRecursively(child, x, y);
            if(potentialHit != null){
                return potentialHit;
            }
        }
        return null;
    }

    @Override
    public void mousePressed(MouseEvent e, float x, float y) {
        super.mousePressed(e, x, y);
    }

    @Override
    public void mouseReleased(MouseEvent e, float x, float y) {
        super.mouseReleased(e, x, y);
        if (!isDraggedInside && isPointInsideContent(x, y)) {
            tryInteractWithTree(x, y);
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
        if (keyEvent.getKeyChar() == 'r') {
            contentTranslate = contentTranslateOrigin.copy();
        }
    }
}
