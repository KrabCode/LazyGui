package toolbox.tree;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.GlobalState;
import toolbox.Palette;
import toolbox.tree.nodes.FolderNode;

import static processing.core.PApplet.*;

public abstract class Node {
    public final NodeType type;
    public final FolderNode parent;
    public final String path;
    public final String name;
    public final float cell = GlobalState.cell;
    public PVector pos = new PVector();
    public PVector size = new PVector();
    protected PVector dragStartPos = new PVector();
    public boolean isDragged = false;
    public boolean mouseOver = false;

    public Node(String path, NodeType type, FolderNode parentFolder) {
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

    public void updateNodeCoordinates(float x, float y, float w, float h) {
        pos.x = x;
        pos.y = y;
        size.x = w;
        size.y = h;
    }

    float textX = 5;
    protected float textY = -3;

    public void drawNode(PGraphics pg) {
        pg.pushMatrix();
        if(mouseOver){
            pg.noStroke();
            pg.fill(Palette.contentBackgroundFocusedFill);
            pg.rect(0,0,size.x,size.y);
        }
        updateDrawInlineNode(pg);
        fillTextColorBasedOnFocus(pg);
        pg.textAlign(LEFT, CENTER);
        pg.text(name, textX, size.y * 0.5f);

        pg.popMatrix();
    }

    protected abstract void updateDrawInlineNode(PGraphics pg);

    protected void validatePrecision() {

    }

    protected void fillTextColorBasedOnFocus(PGraphics pg) {
        if (isFocusedAndMouseOver()) {
            pg.fill(Palette.selectedTextFill);
        } else {
            pg.fill(Palette.standardTextFill);
        }
    }

    protected void strokeContentBasedOnFocus(PGraphics pg) {
        if (isFocusedAndMouseOver()) {
            pg.stroke(Palette.selectedContentStroke);
        } else {
            pg.stroke(Palette.standardContentStroke);
        }
    }

    protected void fillContentBasedOnFocus(PGraphics pg) {
        if (isFocusedAndMouseOver()) {
            pg.fill(Palette.selectedContentFill);
        } else {
            pg.fill(Palette.standardContentFill);
        }
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

    public void mouseReleased(float x, float y) {
        isDragged = false;
    }

    public void keyPressedInsideNode(KeyEvent e, float x, float y) {

    }

    public void nodeReleased(float x, float y) {

    }

    public void mouseWheelMovedInsideNode(float x, float y, int dir) {

    }

    public void mouseDragged(MouseEvent e, float x, float y, float px, float py) {

    }
}
