package toolbox.tree;

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
    public boolean valueBooleanDefault = false;
    public boolean valueBoolean = false;
    public PVector pos = new PVector();
    public PVector size = new PVector();
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
        switch (type) {
            case SLIDER_X:
            case SLIDER_INT_X:
                updateDrawInlineNode(pg);
                break;
            default:
                break;
        }
        fillTextColorBasedOnFocus(pg);
        pg.textAlign(LEFT, CENTER);
        pg.text(name, textX, size.y * 0.5f);
        pg.stroke(0.3f);
        pg.line(0, size.y, size.x, size.y);
//        pg.line(0, size.y, size.x, 0);
        pg.popMatrix();
    }

    protected abstract void updateDrawInlineNode(PGraphics pg);

    protected void validatePrecision() {

    }

    protected void fillTextColorBasedOnFocus(PGraphics pg) {
        if (parent.window.isFocused() && mouseOver) {
            pg.fill(Palette.selectedTextFill);
        } else {
            pg.fill(Palette.standardTextFill);
        }
    }

    public void nodePressed(float x, float y) {
        isDragged = true;
    }

    public void mouseReleased(float x, float y) {
        isDragged = false;
    }

    public void wheelMovedInsideNode(Node clickedNode, float x, float y, int dir) {

    }
}
