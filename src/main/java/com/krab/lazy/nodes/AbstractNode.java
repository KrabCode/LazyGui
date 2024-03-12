package com.krab.lazy.nodes;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;


import com.krab.lazy.input.LazyKeyEvent;
import com.krab.lazy.stores.*;
import com.krab.lazy.utils.NodePaths;
import com.krab.lazy.input.LazyMouseEvent;
import com.krab.lazy.themes.ThemeColorType;
import com.krab.lazy.themes.ThemeStore;
import processing.core.PGraphics;
import processing.core.PVector;

import static com.krab.lazy.stores.LayoutStore.cell;
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
    public final String className = this.getClass().getSimpleName();
    @Expose
    public
    String path;
    @Expose
    public
    NodeType type;

    public final FolderNode parent;
    public final PVector pos = new PVector();
    public final PVector size = new PVector();
    public final String name;

    public float masterInlineNodeHeightInCells = 1;
    public boolean isInlineNodeDragged = false;
    public boolean isInlineNodeDraggable = true;
    public boolean isMouseOverNode = false;

    private boolean isInlineNodeVisible = true;

    public void setIsMouseOverThisNodeOnly(){
        isMouseOverNode = true;
        NodeTree.setAllOtherNodesMouseOverToFalse(this);
    }

    protected AbstractNode(NodeType type, String path, FolderNode parentFolder) {
        this.path = path;
        this.name = getNameFromPath(path);
        this.type = type;
        this.parent = parentFolder;
    }

    @SuppressWarnings("unused")
    private AbstractNode(){
        parent = null;
        name = null;
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

    /**
     * Main update function, only called when the parent window containing this node is open.
     * @see AbstractNode#drawNodeBackground(PGraphics)
     * @param pg main PGraphics of the gui of the same size as the main PApplet canvas to draw on
     */
    public final void updateDrawInlineNode(PGraphics pg) {
        // the node knows its absolute position but here the current matrix is already translated to it
        if(isMouseOverNode){
            highlightNodeBackground(pg);
        }
        pg.pushMatrix();
        pg.pushStyle();
        drawNodeBackground(pg);
        pg.popMatrix();
        pg.popStyle();
        pg.pushMatrix();
        pg.pushStyle();
        drawNodeForeground(pg, name);
        pg.popMatrix();
        pg.popStyle();
    }

    /**
     * Secondary update function, called for all nodes every frame, regardless of their parent window's closed state.
     */
    public void updateValuesRegardlessOfParentWindowOpenness(){

    }

    protected void highlightNodeBackground(PGraphics pg) {
        pg.noStroke();
        pg.fill(ThemeStore.getColor(ThemeColorType.FOCUS_BACKGROUND));
        pg.rect(0,0,size.x,size.y);
    }

    protected abstract void drawNodeBackground(PGraphics pg);

    protected abstract void drawNodeForeground(PGraphics pg, String name);

    protected void drawLeftText(PGraphics pg, String text){
        fillForegroundBasedOnMouseOver(pg);
        String trimmedText = FontStore.getSubstringFromStartToFit(pg, text, size.x - FontStore.textMarginX);
        pg.textAlign(LEFT, CENTER);
		pg.text(trimmedText, FontStore.textMarginX, cell - FontStore.textMarginY);
    }

    protected void drawRightTextToNotOverflowLeftText(PGraphics pg, String rightText, String leftText, boolean fillBackground) {
        pg.textAlign(RIGHT, CENTER);
        String trimmedTextLeft = FontStore.getSubstringFromStartToFit(pg, leftText, size.x - FontStore.textMarginX);
		float leftOffset = pg.textWidth(trimmedTextLeft)+(FontStore.textMarginX*2);
        String trimmedRightText = FontStore.getSubstringFromStartToFit(pg, rightText, size.x - FontStore.textMarginX -leftOffset);
        if(fillBackground){
            float w = pg.textWidth(trimmedRightText) + FontStore.textMarginX * 2;
            drawRightBackdrop(pg, w);
        }
        pg.text(trimmedRightText,size.x - FontStore.textMarginX,size.y - FontStore.textMarginY);
    }

    protected void drawRightText(PGraphics pg, String text, boolean fillBackground) {
        if(fillBackground){
            float backdropBuffer = cell * 0.5f;
            float w = pg.textWidth(text) + FontStore.textMarginX + backdropBuffer;
            drawRightBackdrop(pg, w);
        }
        pg.textAlign(RIGHT, CENTER);
        pg.text(text,size.x - FontStore.textMarginX,size.y - FontStore.textMarginY);
    }

    protected void drawRightBackdrop(PGraphics pg, float backdropSize) {
        pg.pushStyle();
        fillBackgroundBasedOnMouseOver(pg);
        pg.noStroke();
        pg.rectMode(CORNER);
        pg.rect(size.x-backdropSize, 0, backdropSize, size.y);
        pg.popStyle();
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

    public void mousePressedOverNode(float x, float y) {
        isInlineNodeDragged = true;
        isMouseOverNode = true;
        println("mousePressedOverNode(): " + path);
    }

    public void mouseReleasedAnywhere(LazyMouseEvent e) {
        if(isInlineNodeDragged){
            println("mouseReleasedAnywhere() and isInlineNodeDragged: " + path);
            e.setConsumed(true);
        }
        isInlineNodeDragged = false;
    }

    public void onActionEnded(){
        // TODO re-enable undo/redo when fixed
//        UndoRedoStore.onUndoableActionEnded();
        ChangeStore.onChange(path);
    }

    public void keyPressedOverNode(LazyKeyEvent e, float x, float y) {

    }

    public void mouseReleasedOverNode(float x, float y) {

    }

    public void mouseWheelMovedOverNode(float x, float y, int dir) {

    }

    public void mouseDragNodeContinue(LazyMouseEvent e) {
        isMouseOverNode = true;
    }

    public boolean isParentWindowVisible(){
        if(parent == null || parent.window == null){
            return !LayoutStore.isGuiHidden();
        }
        return !parent.window.closed;
    }

    public boolean isParentWindowOpen(){
        if(parent == null || parent.window == null){
            return false;
        }
        return !parent.window.closed;
    }

    // used by value nodes to load state from json
    public void overwriteState(JsonElement loadedNode){

    }

    public String getValueAsString(){
        return "";
    }

    @Override
    public String toString() {
        return className + " @ " + path;
    }

    private String getNameFromPath(String path) {
        if ("".equals(path)) { // this is the root node
            String overridingSketchName = LayoutStore.getOverridingSketchName();
            if(overridingSketchName != null){
                return overridingSketchName;
            }
            return GlobalReferences.app.getClass().getSimpleName(); // not using lowercase separated class name after all because it breaks what users expect to see
        }
        String[] split = NodePaths.splitByUnescapesSlashesWithoutRemovingThem(path);
        if (split.length == 0) {
            return "";
        }
        String nameWithoutPrefixSlash = NodePaths.getNameWithoutPrefixSlash(split[split.length - 1]);
        return NodePaths.getDisplayStringWithoutEscapes(nameWithoutPrefixSlash);
    }

    @SuppressWarnings("unused") // previously used for getting root window name, might still be useful some day
    private String getClassNameAsSpaceSeparatedLowerCase(String className){
        if(className.trim().isEmpty()){
            return "";
        }
        String wordSplitRegex = "(?<=.)(?=[A-Z])";
        /*  Regex explanation
            (?<=.)      look behind, find any character that is not the beginning of a line
            (?=[A-Z])   look ahead, find any upper case character
            together this matches the space between words and thus splits the string without removing any chars
            example input: OofTestBingo
            split result: [Oof,Test,Bingo]
        */
        String[] split = className.split(wordSplitRegex);
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(s);
            sb.append(" ");
        }
        return sb.toString().trim().toLowerCase();
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

    public void hideInlineNode() {
        if(this.equals(NodeTree.getRoot())){
            return;
        }
        isInlineNodeVisible = false;
    }

    public void showInlineNode() {
        isInlineNodeVisible = true;
    }

    public boolean isInlineNodeVisible(){
        return isInlineNodeVisible;
    }

    public boolean isInlineNodeVisibleParentAware(){
        return NodeTree.areAllParentsInlineVisible(this) && isInlineNodeVisible;
    }
}
