package toolbox.tree.rows;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.Gui;
import toolbox.global.State;
import toolbox.global.PaletteStore;

import static processing.core.PApplet.*;
import static toolbox.global.palettes.PaletteColorType.*;

/**
 *
 * A row in the GUI Tree representing either a folder of other rows or a more primitive value
 */
public abstract class AbstractRow {
    public final RowType type;
    public final FolderRow parent;
    public final String path;
    public String name;
    public final float cell = State.cell;
    public PVector pos = new PVector();
    public PVector size = new PVector();
    public int rowCount = 1;
    protected PVector dragStartPos = new PVector();
    public boolean isDragged = false;
    public boolean isMouseOverRow = false;

    protected boolean displayInlineName = true;

    public AbstractRow(RowType type, String path, FolderRow parentFolder) {
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
     * The row must know its absolute position and size, so it can respond to user input events
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
        // the row knows its absolute position but here it is already translated to it for more readable relative drawing code
        pg.pushStyle();
        pg.pushMatrix();
        if(isMouseOverRow){
            highlightNodeRowOnMouseOver(pg);
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

    protected void highlightNodeRowOnMouseOver(PGraphics pg) {
        pg.noStroke();
        pg.fill(PaletteStore.get(FOCUS_BACKGROUND));
        pg.rect(0,0,size.x,size.y);
    }

    protected abstract void updateDrawInlineNode(PGraphics pg);

    protected void validatePrecision() {

    }

    protected void strokeForegroundBasedOnMouseOver(PGraphics pg) {
        if (isMouseOverRow) {
            pg.stroke(PaletteStore.get(FOCUS_FOREGROUND));
        } else {
            pg.stroke(PaletteStore.get(NORMAL_FOREGROUND));
        }
    }

    protected void fillForegroundBasedOnMouseOver(PGraphics pg) {
        if(isMouseOverRow){
            pg.fill(PaletteStore.get(FOCUS_FOREGROUND));
        } else {
            pg.fill(PaletteStore.get(NORMAL_FOREGROUND));
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

    private boolean isFocused(){
        return parent.window.isFocused();
    }

    private boolean isFocusedAndMouseOver() {
        return parent.window.isFocused() && isMouseOverRow;
    }

    public void rowPressed(float x, float y) {
        isDragged = true;
        dragStartPos.x = x;
        dragStartPos.y = y;
    }

    public void mouseReleasedAnywhere(float x, float y) {
        isDragged = false;
        State.app.cursor();
    }

    public void keyPressedOverRow(KeyEvent e, float x, float y) {

    }

    public void mouseReleasedOverRow(float x, float y) {

    }

    public void mouseWheelMovedOverRow(float x, float y, int dir) {

    }

    public void mouseDragRowContinue(MouseEvent e, float x, float y, float px, float py) {
        State.app.noCursor();
        State.robot.mouseMove(State.window.getX() + floor(dragStartPos.x), State.window.getY() + floor(dragStartPos.y));
    }

    public boolean isParentWindowHidden(){
        if(parent == null || parent.window == null){
            return Gui.isGuiHidden;
        }
        return parent.window.hidden;
    }
}
