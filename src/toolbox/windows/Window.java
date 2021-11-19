package toolbox.windows;

import com.jogamp.newt.event.MouseEvent;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.*;
import toolbox.tree.Node;
import toolbox.userInput.UserInputPublisher;
import toolbox.userInput.UserInputSubscriber;

import static processing.core.PApplet.lerp;
import static processing.core.PConstants.LEFT;

public abstract class Window implements UserInputSubscriber {
    protected boolean closeable;
    protected Node node;
    protected PVector pos;
    protected PVector size; // TODO resize dynamically to fit contents, (maybe scroll bar?)
    float cell = GlobalState.cell;
    float titleBarHeight = cell;
    private boolean hidden = false;
    private boolean isDraggedAround;

    public Window(PVector pos, PVector size, Node node, boolean closeable){
        this.pos = pos;
        this.size = size;
        this.node = node;
        this.closeable = closeable;
        UserInputPublisher.subscribe(this);
    }


    public boolean isFocused() {
        return WindowManager.isFocused(this);
    }

    public void drawWindow(PGraphics pg) {
        pg.textFont(GlobalState.font);
        if (hidden) {
            return;
        }
        constrainPosition(pg);
        pg.pushMatrix();
        drawBackground(pg);
        drawContent(pg);
        drawBorder(pg);
        drawTitleBar(pg);
        if (closeable) {
            drawCloseButton(pg);
        }
        pg.popMatrix();
    }

    private void drawCloseButton(PGraphics pg) {
        pg.pushMatrix();
        pg.translate(pos.x, pos.y);
        setBorderStrokeBasedOnFocus(pg);
        pg.noFill();
        pg.rect(size.x - cell, 0, cell, cell);
        pg.popMatrix();
    }

    private void drawBackground(PGraphics pg) {
        pg.pushMatrix();
        pg.translate(pos.x, pos.y);
        pg.noStroke();
        pg.fill(Palette.windowContentFill);
        pg.rect(0, cell, size.x, size.y - cell);
        pg.popMatrix();
    }

    protected abstract void drawContent(PGraphics pg);

    protected void drawBorder(PGraphics pg) {
        pg.pushMatrix();
        pg.translate(pos.x, pos.y);
        setBorderStrokeBasedOnFocus(pg);
        pg.noFill();
        pg.rect(0, cell, size.x, size.y - cell - 1);
        pg.popMatrix();
    }

    protected void drawTitleBar(PGraphics pg) {
        pg.pushMatrix();
        pg.translate(pos.x, pos.y);
        pg.fill(Palette.windowTitleFill);
        setBorderStrokeBasedOnFocus(pg);
        pg.rect(0, 0, size.x, titleBarHeight);
        if(isFocused()){
            pg.fill(Palette.selectedTextFill);
        }else{
            pg.fill(Palette.standardTextFill);
        }
        pg.textAlign(LEFT);
        int textMarginX = 4;
        pg.text(node.name, textMarginX, titleBarHeight - textMarginX);
        pg.popMatrix();
    }

    private void setBorderStrokeBasedOnFocus(PGraphics pg) {
        if (isFocused()) {
            pg.stroke(Palette.windowBorderStrokeFocused);
        } else {
            pg.stroke(Palette.windowBorderStroke);
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
            setFocusOnThis();
            e.setConsumed(true);
        }
        if(isPointInsideTitleBar(x,y)){
            isDraggedAround = true;
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
    }

    public void uncover() {
        hidden = false;
        setFocusOnThis();
    }

    private boolean isHidden() {
        return hidden || Gui.isGuiHidden;
    }

    private void setFocusOnThis() {
        WindowManager.setFocus(this);
        UserInputPublisher.setFocus(this);
    }

    public boolean isPointInsideContent(float x, float y) {
        return MathUtils.isPointInRect(x, y,
                pos.x, pos.y + cell,
                size.x, size.y - cell);
    }

    public boolean isPointInsideSketchWindow(float x, float y) {
        PApplet app = GlobalState.app;
        return MathUtils.isPointInRect(x, y, 0, 0, app.width, app.height);
    }

    public boolean isPointInsideWindow(float x, float y) {
        return MathUtils.isPointInRect(x, y, pos.x, pos.y, size.x, size.y);
    }

    public boolean isPointInsideTitleBar(float x, float y) {
        return MathUtils.isPointInRect(x, y, pos.x, pos.y, size.x - cell, titleBarHeight);
    }

    protected boolean isPointInsideCloseButton(float x, float y) {
        return MathUtils.isPointInRect(x, y,
                pos.x + size.x - cell, pos.y,
                cell, cell);
    }


}
