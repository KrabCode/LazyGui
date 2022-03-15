package toolbox.windows.nodes;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.Gui;
import toolbox.global.State;
import toolbox.global.palettes.PaletteStore;
import toolbox.global.palettes.PaletteColorType;

import static processing.core.PApplet.*;

/**
 *
 * A node in the GUI Tree representing either a folder of other nodes or a more primitive value
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
    public int heightMultiplier = 1;

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
    public void updateNodeCoordinates(float x, float y, float w, float h) {
        pos.x = x;
        pos.y = y;
        size.x = w;
        size.y = h;
    }


    public void drawNode(PGraphics pg) {
        // the node knows its absolute position but here it is already translated to it for more readable relative drawing code
        pg.pushStyle();
        pg.pushMatrix();
        if(isMouseOverNode){
            highlightNodeNodeOnMouseOver(pg);
        }
        pg.pushMatrix();
        pg.pushStyle();
        updateDrawInlineNode(pg);
        pg.popMatrix();
        pg.popStyle();
        if(displayInlineName){
            fillForegroundBasedOnMouseOver(pg);
            drawLeftText(pg, name);
        }
        pg.popMatrix();
        pg.popStyle();
    }

    protected void highlightNodeNodeOnMouseOver(PGraphics pg) {
        pg.noStroke();
        pg.fill(PaletteStore.get(PaletteColorType.FOCUS_BACKGROUND));
        pg.rect(0,0,size.x,size.y);
    }

    protected abstract void updateDrawInlineNode(PGraphics pg);

    protected void validatePrecision() {

    }

    protected void strokeForegroundBasedOnMouseOver(PGraphics pg) {
        if (isMouseOverNode) {
            pg.stroke(PaletteStore.get(PaletteColorType.FOCUS_FOREGROUND));
        } else {
            pg.stroke(PaletteStore.get(PaletteColorType.NORMAL_FOREGROUND));
        }
    }

    protected void fillForegroundBasedOnMouseOver(PGraphics pg) {
        if(isMouseOverNode){
            pg.fill(PaletteStore.get(PaletteColorType.FOCUS_FOREGROUND));
        } else {
            pg.fill(PaletteStore.get(PaletteColorType.NORMAL_FOREGROUND));
        }
    }

    protected void strokeBackgroundBasedOnMouseOver(PGraphics pg) {
        if (isMouseOverNode) {
            pg.stroke(PaletteStore.get(PaletteColorType.FOCUS_BACKGROUND));
        } else {
            pg.stroke(PaletteStore.get(PaletteColorType.NORMAL_BACKGROUND));
        }
    }

    protected void fillBackgroundBasedOnMouseOver(PGraphics pg) {
        if(isMouseOverNode){
            pg.fill(PaletteStore.get(PaletteColorType.FOCUS_BACKGROUND));
        } else {
            pg.fill(PaletteStore.get(PaletteColorType.NORMAL_BACKGROUND));
        }
    }


    public void drawLeftText(PGraphics pg, String text) {
        pg.textAlign(LEFT, CENTER);
        pg.text(text, State.textMarginX, size.y - State.font.getSize() * 0.6f);

    }

    public void drawRightText(PGraphics pg, String text) {
        pg.textAlign(RIGHT, CENTER);
        float textMarginX = 5;
        pg.text(text,
                size.x - textMarginX,
                size.y * 0.5f
        );
    }

    protected void drawToggleHandle(PGraphics pg, boolean valueBoolean) {
        float rectWidth = cell * 0.3f;
        float rectHeight = cell * 0.3f;


        pg.rectMode(CENTER);
        pg.translate(size.x - cell * 0.5f, size.y * 0.5f);
        if(isMouseOverNode){
            pg.stroke(PaletteStore.get(PaletteColorType.FOCUS_FOREGROUND));
        }else{
            pg.stroke(PaletteStore.get(PaletteColorType.NORMAL_FOREGROUND));
        }
        if(valueBoolean){
            pg.fill(PaletteStore.get(PaletteColorType.NORMAL_BACKGROUND));
            pg.rect(-rectWidth*0.5f,0, rectWidth, rectHeight);
            pg.fill(PaletteStore.get(PaletteColorType.FOCUS_FOREGROUND));
            pg.rect(rectWidth*0.5f,0, rectWidth, rectHeight);
        }else{
            pg.fill(PaletteStore.get(PaletteColorType.NORMAL_FOREGROUND));
            pg.rect(-rectWidth*0.5f,0, rectWidth, rectHeight);
            pg.fill(PaletteStore.get(PaletteColorType.NORMAL_BACKGROUND));
            pg.rect(rectWidth*0.5f,0, rectWidth, rectHeight);
        }
    }

    private boolean isFocused(){
        return parent.window.isFocused();
    }

    private boolean isFocusedAndMouseOver() {
        return parent.window.isFocused() && isMouseOverNode;
    }

    public void nodeClicked(float x, float y) {
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

    public void keyPressedOutOfNode(KeyEvent keyEvent, float x, float y) {

    }

    public void mouseReleasedOverNode(float x, float y) {

    }

    public void mouseWheelMovedOverNode(float x, float y, int dir) {

    }

    public void mouseDragNodeContinue(MouseEvent e, float x, float y, float px, float py) {
        State.app.noCursor();
        State.robot.mouseMove(State.window.getX() + floor(dragStartPos.x), State.window.getY() + floor(dragStartPos.y));
    }

    public boolean isParentWindowVisible(){
        if(parent == null || parent.window == null){
            return !Gui.isGuiHidden;
        }
        return !parent.window.hidden;
    }

    // used by value nodes to load state from json
    public void overwriteState(JsonElement loadedNode){

    }
}
