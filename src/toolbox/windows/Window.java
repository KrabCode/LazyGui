package toolbox.windows;

import com.jogamp.newt.event.MouseEvent;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.*;
import toolbox.global.State;
import toolbox.global.GuiPaletteStore;
import toolbox.global.Utils;
import toolbox.tree.rows.Row;
import toolbox.userInput.UserInputPublisher;
import toolbox.userInput.UserInputSubscriber;

import static processing.core.PApplet.lerp;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;
import static toolbox.global.palettes.GuiPaletteColorType.*;

public abstract class Window implements UserInputSubscriber {
    protected boolean closeable;
    protected Row parentRow;
    protected PVector pos;
    protected PVector size;
    float cell = State.cell;
    float titleBarHeight = cell;
    public boolean hidden = false;
    private boolean isDraggedAround;

    public Window(PVector pos, Row parentRow, boolean closeable){
        this.pos = pos;
        this.size = new PVector(cell * 8, cell * 1);
        this.parentRow = parentRow;
        this.closeable = closeable;
        UserInputPublisher.subscribe(this);
    }


    public boolean isFocused() {
        return WindowManager.isFocused(this);
    }

    public void drawWindow(PGraphics pg) {
        pg.textFont(State.font);
        if (hidden) {
            return;
        }
        constrainPosition(pg);
        pg.pushMatrix();
        drawBackgroundWithWindowBorder(pg);
        drawContent(pg);
        drawTitleBar(pg);
        if (closeable) {
            drawCloseButton(pg);
        }
        pg.popMatrix();
    }

    protected void drawBackgroundWithWindowBorder(PGraphics pg) {
        pg.pushMatrix();
        pg.translate(pos.x, pos.y);
        pg.stroke(GuiPaletteStore.get(WINDOW_BORDER));
        pg.strokeWeight(1);
        pg.fill(GuiPaletteStore.get(NORMAL_BACKGROUND));
        pg.rect(-1,-1,size.x+1, size.y+1);
        pg.popMatrix();
    }

    private void drawCloseButton(PGraphics pg) {
        pg.pushMatrix();
        pg.translate(pos.x, pos.y);
        pg.stroke(GuiPaletteStore.get(WINDOW_BORDER));
        pg.strokeWeight(1);
        pg.line(size.x-cell, 0, size.x-cell, cell-1);
        if(isPointInsideCloseButton(State.app.mouseX, State.app.mouseY)){
            pg.stroke(GuiPaletteStore.get(NORMAL_FOREGROUND));
            pg.strokeWeight(1.99f);
            pg.pushMatrix();
            pg.translate(size.x - cell * 0.5f, cell * 0.5f);
            float n = cell * 0.2f;
            pg.line(-n, -n, n, n);
            pg.line(-n, n, n, -n);
            pg.popMatrix();
        }
        pg.popMatrix();
    }

    protected abstract void drawContent(PGraphics pg);

    protected void drawTitleBar(PGraphics pg) {
        pg.pushMatrix();
        pg.translate(pos.x, pos.y);
        fillWindowBasedOnDragged(pg);
        pg.noStroke();
        pg.rect(0, 0, size.x, titleBarHeight);
        if(isDraggedAround){
            pg.fill(GuiPaletteStore.get(FOCUS_FOREGROUND));
        }else{
            pg.fill(GuiPaletteStore.get(NORMAL_FOREGROUND));
        }
        pg.textAlign(LEFT, CENTER);
        pg.text(parentRow.name, State.textMarginX, cell - State.font.getSize() * 0.6f);
        pg.stroke(GuiPaletteStore.get(WINDOW_BORDER));
        pg.line(0, cell, size.x, cell);
        pg.popMatrix();
    }

    private void fillWindowBasedOnDragged(PGraphics pg) {
        if(isDraggedAround){
            pg.fill(GuiPaletteStore.get(FOCUS_BACKGROUND));
        }else{
            pg.fill(GuiPaletteStore.get(NORMAL_BACKGROUND));
        }
    }

    private void constrainPosition(PGraphics pg) {
        float rightEdge = pg.width - size.x - 1;
        float bottomEdge = pg.height - size.y - 1;
        float lerpAmt = 0.3f;
        if (pos.x < 0) {
            pos.x = lerp(pos.x, 0, lerpAmt);
        }
        if (pos.y < 0) {
            pos.y = lerp(pos.y, 0, lerpAmt);
        }
        if (pos.x > rightEdge) {
            pos.x = lerp(pos.x, rightEdge, lerpAmt);
        }
        if (pos.y > bottomEdge) {
            pos.y = lerp(pos.y, bottomEdge, lerpAmt);
        }
    }

    @Override
    public void mousePressed(MouseEvent e, float x, float y) {
        if (isHidden()) {
            return;
        }
        if (isPointInsideWindow(x, y)) {
            e.setConsumed(true);
        }
        if(isPointInsideTitleBar(x,y)){
            isDraggedAround = true;
            setFocusOnThis();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e, float x, float y, float px, float py) {
        if(isHidden()){
            return;
        }
        if (isDraggedAround) {
            pos.x += x - px;
            pos.y += y - py;
            e.setConsumed(true);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e, float x, float y) {
        if(isHidden()){
            return;
        }
        if (closeable && isPointInsideCloseButton(x, y)) {
            hide();
            e.setConsumed(true);
        } else if (isDraggedAround) {
            e.setConsumed(true);
        }
        isDraggedAround = false;
    }

    private void hide() {
        hidden = true;
        isDraggedAround = false;
    }

    public void uncover() {
        hidden = false;
        isDraggedAround = true;
    }

    private boolean isHidden() {
        return hidden || Gui.isGuiHidden;
    }

    void setFocusOnThis() {
        WindowManager.setFocus(this);
        UserInputPublisher.setFocus(this);
    }

    public boolean isPointInsideContent(float x, float y) {
        return Utils.isPointInRect(x, y,
                pos.x, pos.y + cell,
                size.x, size.y - cell);
    }

    public boolean isPointInsideSketchWindow(float x, float y) {
        PApplet app = State.app;
        return Utils.isPointInRect(x, y, 0, 0, app.width, app.height);
    }

    public boolean isPointInsideWindow(float x, float y) {
        return Utils.isPointInRect(x, y, pos.x, pos.y, size.x, size.y);
    }

    public boolean isPointInsideTitleBar(float x, float y) {
        if(closeable){
            return Utils.isPointInRect(x, y, pos.x, pos.y, size.x-cell, titleBarHeight);
        }
        return Utils.isPointInRect(x, y, pos.x, pos.y, size.x, titleBarHeight);
    }

    protected boolean isPointInsideCloseButton(float x, float y) {
        return Utils.isPointInRect(x, y,
                pos.x + size.x - cell, pos.y,
                cell, cell);
    }


}
