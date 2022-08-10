package toolbox.windows;

import com.jogamp.newt.event.MouseEvent;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.global.NodeTree;
import toolbox.global.State;
import toolbox.global.themes.ThemeStore;
import toolbox.global.Utils;
import toolbox.windows.nodes.AbstractNode;
import toolbox.userInput.UserInputPublisher;
import toolbox.userInput.UserInputSubscriber;
import toolbox.Gui;

import static processing.core.PApplet.lerp;
import static processing.core.PApplet.println;
import static processing.core.PConstants.*;
import static toolbox.global.themes.ThemeColorType.*;

public abstract class Window implements UserInputSubscriber {
    public boolean closed = false;
    protected boolean isCloseable;
    protected AbstractNode parentNode;
    protected PVector windowPos;
    public PVector windowSize;
    float cell = State.cell;
    float titleBarHeight = cell;

    private boolean isDraggedAround;

    public Window(PVector windowPos, AbstractNode parentNode, boolean isCloseable) {
        this.windowPos = windowPos;
        this.windowSize = new PVector(State.defaultWindowWidthInPixels, cell * 1);
        this.parentNode = parentNode;
        this.isCloseable = isCloseable;
        UserInputPublisher.subscribe(this);
    }


    public boolean isFocused() {
        return WindowManager.isFocused(this);
    }

    public void drawWindow(PGraphics pg) {
        pg.textFont(State.font);
        if (closed) {
            return;
        }
        constrainPosition(pg);
        pg.pushMatrix();
        drawBackgroundWithWindowBorder(pg);
        drawContent(pg);
        drawTitleBar(pg);
        if (isCloseable) {
            drawCloseButton(pg);
        }
        pg.popMatrix();
    }

    protected void drawBackgroundWithWindowBorder(PGraphics pg) {
        pg.pushMatrix();
        pg.translate(windowPos.x, windowPos.y);
        pg.stroke(ThemeStore.getColor(WINDOW_BORDER));
        pg.strokeWeight(1);
        pg.fill(ThemeStore.getColor(NORMAL_BACKGROUND));
        pg.rect(-1, -1, windowSize.x + 1, windowSize.y + 1);
        pg.popMatrix();
    }

    private void drawCloseButton(PGraphics pg) {
        pg.pushMatrix();
        pg.translate(windowPos.x, windowPos.y);
        pg.stroke(ThemeStore.getColor(WINDOW_BORDER));
        pg.strokeWeight(1);
        pg.line(windowSize.x - cell, 0, windowSize.x - cell, cell - 1);
        if (isPointInsideCloseButton(State.app.mouseX, State.app.mouseY)) {
            NodeTree.setAllOtherNodesMouseOver(null, false);
            pg.fill(ThemeStore.getColor(FOCUS_BACKGROUND));
            pg.noStroke();
            pg.rectMode(CORNER);
            pg.rect(windowSize.x - cell + 0.5f, 0, cell - 1, cell);
            pg.stroke(ThemeStore.getColor(FOCUS_FOREGROUND));
            pg.strokeWeight(1.99f);
            pg.pushMatrix();
            pg.translate(windowSize.x - cell * 0.5f + 0.5f, cell * 0.5f);
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
        pg.translate(windowPos.x, windowPos.y);
        boolean highlight = shouldHighlightTitleBar();
        pg.fill(highlight ? ThemeStore.getColor(FOCUS_BACKGROUND) : ThemeStore.getColor(NORMAL_BACKGROUND));
        pg.noStroke();
        pg.rect(0, 0, windowSize.x, titleBarHeight);
        pg.fill(highlight ? ThemeStore.getColor(FOCUS_FOREGROUND) : ThemeStore.getColor(NORMAL_FOREGROUND));
        pg.textAlign(LEFT, CENTER);
        String trimmedName = Utils.getTrimmedTextToFitOneLine(pg, parentNode.name, windowSize.x - cell * 1.1f);
        pg.text(trimmedName, State.textMarginX, cell - State.font.getSize() * 0.6f);
        pg.stroke(ThemeStore.getColor(WINDOW_BORDER));
        pg.line(0, cell, windowSize.x, cell);
        pg.popMatrix();
    }


    protected boolean shouldHighlightTitleBar(){
        return isPointInsideTitleBar(State.app.mouseX, State.app.mouseY) || isDraggedAround;
    }

    private void constrainPosition(PGraphics pg) {
        float rightEdge = pg.width - windowSize.x - 1;
        float bottomEdge = pg.height - windowSize.y - 1;
        float lerpAmt = 0.3f;
        if (windowPos.x < 0) {
            windowPos.x = lerp(windowPos.x, 0, lerpAmt);
        }
        if (windowPos.y < 0) {
            windowPos.y = lerp(windowPos.y, 0, lerpAmt);
        }
        if (windowPos.x > rightEdge) {
            windowPos.x = lerp(windowPos.x, rightEdge, lerpAmt);
        }
        if (windowPos.y > bottomEdge) {
            windowPos.y = lerp(windowPos.y, bottomEdge, lerpAmt);
        }
    }

    @Override
    public void mousePressed(MouseEvent e, float x, float y) {
        if (isClosed()) {
            return;
        }
        if (isPointInsideWindow(x, y)) {
            e.setConsumed(true);
        }
        if (isPointInsideTitleBar(x, y)) {
            isDraggedAround = true;
            setFocusOnThis();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e, float x, float y, float px, float py) {
        if (isClosed()) {
            return;
        }
        if (isDraggedAround) {
            windowPos.x += x - px;
            windowPos.y += y - py;
            e.setConsumed(true);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e, float x, float y) {
        if (isClosed()) {
            return;
        }
        if (isCloseable && isPointInsideCloseButton(x, y)) {
            close();
            e.setConsumed(true);
        } else if (isDraggedAround) {
            e.setConsumed(true);
        }
        isDraggedAround = false;
    }

    public void close() {
        closed = true;
        isDraggedAround = false;
    }

    public void open() {
        closed = false;
        isDraggedAround = true;
    }

    private boolean isClosed() {
        return closed || Gui.isGuiHidden;
    }

    void setFocusOnThis() {
        WindowManager.setFocus(this);
        UserInputPublisher.setFocus(this);
    }

    public boolean isPointInsideContent(float x, float y) {
        return Utils.isPointInRect(x, y,
                windowPos.x, windowPos.y + cell,
                windowSize.x, windowSize.y - cell);
    }

    public boolean isPointInsideSketchWindow(float x, float y) {
        PApplet app = State.app;
        return Utils.isPointInRect(x, y, 0, 0, app.width, app.height);
    }

    public boolean isPointInsideWindow(float x, float y) {
        return Utils.isPointInRect(x, y, windowPos.x, windowPos.y, windowSize.x, windowSize.y);
    }

    public boolean isPointInsideTitleBar(float x, float y) {
        if (isCloseable) {
            return Utils.isPointInRect(x, y, windowPos.x, windowPos.y, windowSize.x - cell, titleBarHeight);
        }
        return Utils.isPointInRect(x, y, windowPos.x, windowPos.y, windowSize.x, titleBarHeight);
    }

    protected boolean isPointInsideCloseButton(float x, float y) {
        return Utils.isPointInRect(x, y,
                windowPos.x + windowSize.x - cell - 1, windowPos.y,
                cell + 1, cell - 1);
    }
}
