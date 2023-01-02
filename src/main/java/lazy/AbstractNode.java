package lazy;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;


import processing.core.PGraphics;
import processing.core.PVector;

import static lazy.State.cell;
import static processing.core.PApplet.*;

/**
 *
 * A node in the GUI Tree representing one or more of the following
 *  - a folder of other nodes
 *  - a transient preview of some value
 *  - a directly adjustable value that is returned to the user
 */
abstract class AbstractNode {
    @Expose
    final String className = this.getClass().getSimpleName();
    @Expose
    String path;
    @Expose
    NodeType type;

    final FolderNode parent;
    final PVector pos = new PVector();
    final PVector size = new PVector();
    final String name;

    float idealInlineNodeHeightInCells = 1;
    boolean isDragged = false;
    boolean isMouseOverNode = false;

    void setIsMouseOverThisNodeOnly(){
        isMouseOverNode = true;
        NodeTree.setAllOtherNodesMouseOverToFalse(this);
    }

    protected boolean shouldDrawLeftNameText = true;

    protected AbstractNode(NodeType type, String path, FolderNode parentFolder) {
        this.path = path;
        this.name = getNameFromPath(path);
        this.type = type;
        this.parent = parentFolder;
    }

    private String getNameFromPath(String path) {
        if ("".equals(path)) {
            return "root";
        }
        String[] split = UtilTreePaths.splitByUnescapedSlashes(path);
        if (split.length == 0) {
            return "";
        }
        return UtilTreePaths.getDisplayStringWithoutEscapes(split[split.length - 1]);
    }

    /**
     * The node must know its absolute position and size, so it can respond to user input events
     * @param x absolute screen x
     * @param y absolute screen y
     * @param w absolute screen width
     * @param h absolute screen height
     */
    void updateInlineNodeCoordinates(float x, float y, float w, float h) {
        pos.x = x;
        pos.y = y;
        size.x = w;
        size.y = h;
    }

    /**
     * Main update function, only called when the parent window containing this node is open.
     * @see AbstractNode#updateDrawInlineNodeAbstract(PGraphics)
     * @param pg main PGraphics of the gui of the same size as the main PApplet canvas to draw on
     */
    void updateDrawInlineNode(PGraphics pg) {
        // the node knows its absolute position but here it is already translated to it for more readable relative drawing code
        if(isMouseOverNode){
            highlightNodeOnMouseOver(pg);
        }
        pg.pushMatrix();
        pg.pushStyle();
        updateDrawInlineNodeAbstract(pg);
        pg.popMatrix();
        pg.popStyle();
        if(shouldDrawLeftNameText){
            fillForegroundBasedOnMouseOver(pg);
            drawLeftText(pg, name);
        }
    }

    /**
     * Secondary update function, called for all nodes every frame, regardless of their parent window's closed state.
     */
    void updateValuesRegardlessOfParentWindowOpenness(){

    }

    protected void highlightNodeOnMouseOver(PGraphics pg) {
        pg.noStroke();
        pg.fill(ThemeStore.getColor(ThemeColorType.FOCUS_BACKGROUND));
        pg.rect(0,0,size.x,size.y);
    }

    protected abstract void updateDrawInlineNodeAbstract(PGraphics pg);

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

    @SuppressWarnings("unused")
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


    void drawLeftText(PGraphics pg, String text) {
        String trimmedText = FontStore.getSubstringFromStartToFit(pg, text, size.x - cell - FontStore.textMarginX * 2);
        pg.textAlign(LEFT, CENTER);
        pg.text(trimmedText, FontStore.textMarginX, size.y - FontStore.textMarginY);
    }

    void drawRightText(PGraphics pg, String text) {
        pg.textAlign(RIGHT, CENTER);
        pg.text(text,
                size.x - FontStore.textMarginX,
                size.y - FontStore.textMarginY
        );
    }

    protected void drawRightToggleHandle(PGraphics pg, boolean valueBoolean) {
        float rectWidth = cell * 0.3f;
        float rectHeight = cell * 0.25f;
        pg.rectMode(CENTER);
        pg.translate(size.x - cell * 0.5f, size.y * 0.5f);
        if(isMouseOverNode){
            pg.stroke(ThemeStore.getColor(ThemeColorType.FOCUS_FOREGROUND));
        }else{
            pg.stroke(ThemeStore.getColor(ThemeColorType.NORMAL_FOREGROUND));
        }
        float turnedOffHandleScale = 0.25f;
        if(valueBoolean){
            pg.fill(ThemeStore.getColor(ThemeColorType.NORMAL_BACKGROUND));
            pg.rect(-rectWidth*0.5f,0, rectWidth, rectHeight);
            pg.fill(ThemeStore.getColor(ThemeColorType.FOCUS_FOREGROUND));
            pg.rect(rectWidth*0.5f,0, rectWidth, rectHeight);
        }else{
            pg.fill(ThemeStore.getColor(ThemeColorType.NORMAL_BACKGROUND));
            pg.rect(0,0, rectWidth*2, rectHeight);
            pg.fill(ThemeStore.getColor(ThemeColorType.NORMAL_FOREGROUND));
            pg.rect(-rectWidth*0.5f,0, rectWidth*turnedOffHandleScale, rectHeight*turnedOffHandleScale);
        }
    }

    protected void drawRightButton(PGraphics pg) {
        pg.noFill();
        pg.translate(size.x - cell *0.5f, cell * 0.5f);
        fillBackgroundBasedOnMouseOver(pg);
        pg.stroke(ThemeStore.getColor(ThemeColorType.NORMAL_FOREGROUND));
        pg.rectMode(CENTER);
        float outerButtonSize = cell * 0.6f;
        pg.rect(0,0, outerButtonSize, outerButtonSize);
        pg.stroke(ThemeStore.getColor(isDragged ? ThemeColorType.FOCUS_FOREGROUND : ThemeColorType.NORMAL_FOREGROUND));
        if(isMouseOverNode){
            if (isDragged){
                pg.fill(ThemeStore.getColor(ThemeColorType.FOCUS_FOREGROUND));
            }else{
                pg.fill(ThemeStore.getColor(ThemeColorType.NORMAL_FOREGROUND));
            }
        }
        float innerButtonSize = cell * 0.35f;
        pg.rect(0,0, innerButtonSize, innerButtonSize);
    }

    void mousePressedOverNode(float x, float y) {
        isDragged = true;
    }

    void mouseReleasedAnywhere(LazyMouseEvent e) {
        if(isDragged){
            e.setConsumed(true);
        }
        isDragged = false;
    }

    void keyPressedOverNode(LazyKeyEvent e, float x, float y) {

    }

    void mouseReleasedOverNode(float x, float y) {

    }

    void mouseWheelMovedOverNode(float x, float y, int dir) {

    }

    void mouseDragNodeContinue(LazyMouseEvent e) {
//        app.noCursor();
//        State.robot.mouseMove(State.window.getX() + floor(dragStartPos.x), State.window.getY() + floor(dragStartPos.y));
    }

    boolean isParentWindowVisible(){
        if(parent == null || parent.window == null){
            return !LazyGui.isGuiHidden;
        }
        return !parent.window.closed;
    }

    boolean isParentWindowOpen(){
        if(parent == null || parent.window == null){
            return false;
        }
        return !parent.window.closed;
    }

    // used by value nodes to load state from json
    void overwriteState(JsonElement loadedNode){

    }

    String getConsolePrintableValue(){
        return "";
    }

    @Override
    public String toString() {
        return "Folder @ " + path + " | " + (isParentWindowOpen() ? "open" : "closed");
    }

}
