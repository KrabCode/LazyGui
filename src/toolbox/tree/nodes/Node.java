package toolbox.tree.nodes;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.global.State;
import toolbox.global.Palette;

import static processing.core.PApplet.*;

/**
 *
 * A node in the GUI Tree representing either a folder of nodes or a user-queryable value
 */
public abstract class Node {
    public final NodeType type;
    public final FolderNode parent;
    public final String path;
    public final String name;
    public final float cell = State.cell;
    public PVector pos = new PVector();
    public PVector size = new PVector();
    public int rowCount = 1;
    protected PVector dragStartPos = new PVector();
    public boolean isDragged = false;
    public boolean mouseOver = false;

    protected boolean displayInlineName = true;

    public Node(NodeType type, String path, FolderNode parentFolder) {
        this.path = path;
        this.name = getNameFromPath(path);
        this.type = type;
        this.parent = parentFolder;
    }

    private String getNameFromPath(String path) {
        if ("".equals(path)) {
            return "root";
        }
        String[] split = path.split("/");
        if (split.length == 0) {
            return "";
        }
        return split[split.length - 1];
    }

    /**
     * The node must know its absolute position and size, so it can respond to user input events
     * @param x absolute screen x
     * @param y absolute screen y
     * @param w absolute screen width
     * @param h absolute screen height
     */
    public void updateNodeCoordinates(float x, float y, float w, float h) {
        pos.x = x;
        pos.y = y;
        size.x = w;
        size.y = h;
    }


    /**
     * The node knows its absolute position but here it is already translated to it for more readable relative drawing code
     * @param pg PGraphics to draw to
     */
    public void drawNode(PGraphics pg) {
        pg.pushStyle();
        pg.pushMatrix();
        if(mouseOver){
            pg.noStroke();
            pg.fill(Palette.focusBackground);
            pg.rect(0,0,size.x,size.y);
        }
        pg.pushMatrix();
        pg.pushStyle();
        updateDrawInlineNode(pg);
        pg.popMatrix();
        pg.popStyle();
        if(displayInlineName){
            drawLeftText(pg, name);
        }
        pg.popMatrix();
        pg.popStyle();
    }

    protected abstract void updateDrawInlineNode(PGraphics pg);

    protected void validatePrecision() {

    }

    protected void strokeForegroundBasedOnMouseOver(PGraphics pg) {
        if (mouseOver) {
            pg.stroke(Palette.focusForeground);
        } else {
            pg.stroke(Palette.normalForeground);
        }
    }

    protected void fillForegroundBasedOnMouseOver(PGraphics pg) {
        if(mouseOver){
            pg.fill(Palette.focusForeground);
        } else {
            pg.fill(Palette.normalForeground);
        }
    }

    public void drawLeftText(PGraphics pg, String text) {
        fillForegroundBasedOnMouseOver(pg);
        pg.textAlign(LEFT, CENTER);
        pg.text(text, State.textMarginX, size.y - State.font.getSize() * 0.6f);

    }

    public void drawRightText(PGraphics pg, String text) {
        fillForegroundBasedOnMouseOver(pg);
        pg.textAlign(RIGHT, CENTER);
        float textMarginX = 5;
        pg.text(text,
                size.x - textMarginX,
                size.y * 0.5f
        );
    }

    private boolean isFocused(){
        return parent.window.isFocused();
    }

    private boolean isFocusedAndMouseOver() {
        return parent.window.isFocused() && mouseOver;
    }

    public void nodePressed(float x, float y) {
        isDragged = true;
        dragStartPos.x = x;
        dragStartPos.y = y;
    }

    public void mouseReleasedAnywhere(float x, float y) {
        isDragged = false;
        State.app.cursor();
    }

    public void keyPressedOverNode(KeyEvent e, float x, float y) {

    }

    public void mouseReleasedOverNode(float x, float y) {

    }

    public void mouseWheelMovedOverNode(float x, float y, int dir) {

    }

    public void mouseDragNodeContinue(MouseEvent e, float x, float y, float px, float py) {
        State.app.noCursor();
        State.robot.mouseMove(State.window.getX() + floor(dragStartPos.x), State.window.getY() + floor(dragStartPos.y));
    }
}
