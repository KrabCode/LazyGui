package lazy.windows.nodes;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import processing.core.PVector;
import lazy.LazyGui;
import lazy.State;
import lazy.Utils;
import lazy.themes.ThemeStore;
import lazy.themes.ThemeColorType;

import static processing.core.PApplet.*;

/**
 *
 * A node in the GUI Tree representing one or more of the following
 *  - a folder of other nodes
 *  - a transient preview of some value
 *  - a directly adjustable value that is returned to the user
 */
public abstract class AbstractNode {
    @Expose
    public String className = this.getClass().getSimpleName();
    @Expose
    public String path;
    @Expose
    public NodeType type;

    public NodeFolder parent;
    public PVector pos = new PVector();
    public PVector size = new PVector();

    public String name;
    public final float cell = State.cell;
    public int rowHeightInCells = 1;

    protected  PVector dragStartPos = new PVector();
    public boolean isDragged = false;
    public boolean isMouseOverNode = false;

    protected boolean displayInlineName = true;


    public AbstractNode(NodeType type, String path, NodeFolder parentFolder) {
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
    public void updateInlineNodeCoordinates(float x, float y, float w, float h) {
        pos.x = x;
        pos.y = y;
        size.x = w;
        size.y = h;
    }


    public void updateDrawInlineNode(PGraphics pg) {
        // the node knows its absolute position but here it is already translated to it for more readable relative drawing code
        if(isMouseOverNode){
            highlightNodeOnMouseOver(pg);
        }
        pg.pushMatrix();
        pg.pushStyle();
        updateDrawInlineNodeInner(pg);
        pg.popMatrix();
        pg.popStyle();
        if(displayInlineName){
            fillForegroundBasedOnMouseOver(pg);
            drawLeftText(pg, name);
        }
    }

    protected void highlightNodeOnMouseOver(PGraphics pg) {
        pg.noStroke();
        pg.fill(ThemeStore.getColor(ThemeColorType.FOCUS_BACKGROUND));
        pg.rect(0,0,size.x,size.y);
    }

    protected abstract void updateDrawInlineNodeInner(PGraphics pg);

    protected void validatePrecision() {

    }

    protected void strokeForegroundBasedOnMouseOver(PGraphics pg) {
        if (isMouseOverNode) {
            pg.stroke(ThemeStore.getColor(ThemeColorType.FOCUS_FOREGROUND));
        } else {
            pg.stroke(ThemeStore.getColor(ThemeColorType.NORMAL_FOREGROUND));
        }
    }

    protected void fillForegroundBasedOnMouseOver(PGraphics pg) {
        if(isMouseOverNode){
            pg.fill(ThemeStore.getColor(ThemeColorType.FOCUS_FOREGROUND));
        } else {
            pg.fill(ThemeStore.getColor(ThemeColorType.NORMAL_FOREGROUND));
        }
    }

    protected void strokeBackgroundBasedOnMouseOver(PGraphics pg) {
        if (isMouseOverNode) {
            pg.stroke(ThemeStore.getColor(ThemeColorType.FOCUS_BACKGROUND));
        } else {
            pg.stroke(ThemeStore.getColor(ThemeColorType.NORMAL_BACKGROUND));
        }
    }

    protected void fillBackgroundBasedOnMouseOver(PGraphics pg) {
        if(isMouseOverNode){
            pg.fill(ThemeStore.getColor(ThemeColorType.FOCUS_BACKGROUND));
        } else {
            pg.fill(ThemeStore.getColor(ThemeColorType.NORMAL_BACKGROUND));
        }
    }


    public void drawLeftText(PGraphics pg, String text) {
        String trimmedText = Utils.getTrimmedTextToFitOneLine(pg, text, size.x - cell);
        pg.textAlign(LEFT, CENTER);
        pg.text(trimmedText, State.textMarginX, size.y - State.textMarginY);
    }

    public void drawRightText(PGraphics pg, String text) {
        pg.textAlign(RIGHT, CENTER);
        pg.text(text,
                size.x - State.textMarginX,
                size.y - State.textMarginY
        );
    }

    protected void drawRightToggleHandle(PGraphics pg, boolean valueBoolean) {
        float rectWidth = cell * 0.3f;
        float rectHeight = cell * 0.3f;


        pg.rectMode(CENTER);
        pg.translate(size.x - cell * 0.5f, size.y * 0.5f);
        if(isMouseOverNode){
            pg.stroke(ThemeStore.getColor(ThemeColorType.FOCUS_FOREGROUND));
        }else{
            pg.stroke(ThemeStore.getColor(ThemeColorType.NORMAL_FOREGROUND));
        }
        if(valueBoolean){
            pg.fill(ThemeStore.getColor(ThemeColorType.NORMAL_BACKGROUND));
            pg.rect(-rectWidth*0.5f,0, rectWidth, rectHeight);
            pg.fill(ThemeStore.getColor(ThemeColorType.FOCUS_FOREGROUND));
        }else{
            pg.fill(ThemeStore.getColor(ThemeColorType.NORMAL_FOREGROUND));
            pg.rect(-rectWidth*0.5f,0, rectWidth, rectHeight);
            pg.fill(ThemeStore.getColor(ThemeColorType.NORMAL_BACKGROUND));
        }
        pg.rect(rectWidth*0.5f,0, rectWidth, rectHeight);
    }

    protected void drawRightButton(PGraphics pg) {
        pg.noFill();
        pg.translate(size.x - cell *0.5f, cell * 0.5f);
        fillBackgroundBasedOnMouseOver(pg);
        pg.stroke(ThemeStore.getColor(ThemeColorType.NORMAL_FOREGROUND));
        pg.rectMode(CENTER);
        pg.rect(0,0, cell * 0.6f, cell*0.5f+1);
        pg.stroke(ThemeStore.getColor(isDragged ? ThemeColorType.FOCUS_FOREGROUND : ThemeColorType.NORMAL_FOREGROUND));
        if(isMouseOverNode){
            if (isDragged){
                pg.fill(ThemeStore.getColor(ThemeColorType.FOCUS_FOREGROUND));
            }else{
                pg.fill(ThemeStore.getColor(ThemeColorType.NORMAL_FOREGROUND));
            }
        }
        pg.rect(0,0,cell * 0.2f, cell*0.2f);
    }

    private boolean isFocused(){
        return parent.window.isFocused();
    }

    private boolean isFocusedAndMouseOver() {
        return parent.window.isFocused() && isMouseOverNode;
    }

    public void mousePressedOverNode(float x, float y) {
        isDragged = true;
        dragStartPos.x = x;
        dragStartPos.y = y;
    }

    public void mouseReleasedAnywhere(MouseEvent e, float x, float y) {
        if(isDragged){
            e.setConsumed(true);
            State.onUndoableActionEnded();
        }
        isDragged = false;
        State.app.cursor();
    }

    public void keyPressedOverNode(KeyEvent e, float x, float y) {

    }

    public void keyPressedOutOfNode(KeyEvent keyEvent, float x, float y) {

    }

    public void mouseReleasedOverNode(float x, float y) {

    }

    public void mouseWheelMovedOverNode(float x, float y, int dir) {

    }

    public void mouseDragNodeContinue(MouseEvent e, float x, float y, float px, float py) {
//        State.app.noCursor();
//        State.robot.mouseMove(State.window.getX() + floor(dragStartPos.x), State.window.getY() + floor(dragStartPos.y));
    }

    public boolean isParentWindowVisible(){
        if(parent == null || parent.window == null){
            return !LazyGui.isGuiHidden;
        }
        return !parent.window.closed;
    }

    // used by value nodes to load state from json
    public void overwriteState(JsonElement loadedNode){

    }

    public String getPrintableValue(){
        return "";
    }
}
