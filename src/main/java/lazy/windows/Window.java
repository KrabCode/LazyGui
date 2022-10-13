package lazy.windows;

import com.google.gson.annotations.Expose;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PApplet;
import processing.core.PGraphics;
import lazy.NodeTree;
import lazy.State;
import lazy.themes.ThemeStore;
import lazy.Utils;
import lazy.windows.nodes.AbstractNode;
import lazy.userInput.UserInputPublisher;
import lazy.userInput.UserInputSubscriber;
import lazy.LazyGui;

import static processing.core.PApplet.lerp;
import static processing.core.PConstants.*;
import static lazy.themes.ThemeColorType.*;

public abstract class Window implements UserInputSubscriber {
    @Expose
    public float posX;
    @Expose
    public float posY;
    @Expose
    public boolean closed = false;
    public float windowSizeX, windowSizeY;
    protected boolean isCloseable;
    protected AbstractNode parentNode;
    float cell = State.cell;
    float titleBarHeight = cell;
    private boolean isDraggedAround;

    public Window(float posX, float posY, AbstractNode parentNode, boolean isCloseable) {
        this.posX = posX;
        this.posY = posY;
        this.windowSizeX = State.defaultWindowWidthInPixels;
        this.windowSizeY = cell * 1;
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
        drawPathTooltipOnHighlight(pg);
        drawBackgroundWithWindowBorder(pg);
        drawContent(pg);
        drawTitleBar(pg);
        if (isCloseable) {
            drawCloseButton(pg);
        }
        pg.popMatrix();
    }

    private void drawPathTooltipOnHighlight(PGraphics pg) {
        if (!shouldHighlightTitleBar() || !LazyGui.drawPathTooltips) {
            return;
        }
        pg.pushMatrix();
        pg.translate(posX, posY);
        String[] pathSplit = Utils.splitFullPathWithoutEndAndRoot(parentNode.path);
        int lineCount = pathSplit.length;
        float tooltipHeight = lineCount * cell;
        pg.noStroke();
        pg.fill(ThemeStore.getColor(NORMAL_BACKGROUND));
        pg.rect(0, -tooltipHeight, windowSizeX, tooltipHeight);
        pg.fill(ThemeStore.getColor(NORMAL_FOREGROUND));
        for (int i = 0; i < lineCount; i++) {
            pg.text(pathSplit[lineCount - 1 - i], State.textMarginX, -i * cell - State.textMarginY);
        }
        pg.popMatrix();
    }

    protected void drawBackgroundWithWindowBorder(PGraphics pg) {
        pg.pushMatrix();
        pg.translate(posX, posY);
        pg.stroke(ThemeStore.getColor(WINDOW_BORDER));
        pg.strokeWeight(1);
        pg.fill(ThemeStore.getColor(NORMAL_BACKGROUND));
        pg.rect(-1, -1, windowSizeX + 1, windowSizeY + 1);
        pg.popMatrix();
    }

    private void drawCloseButton(PGraphics pg) {
        pg.pushMatrix();
        pg.translate(posX, posY);
        pg.stroke(ThemeStore.getColor(WINDOW_BORDER));
        pg.strokeWeight(1);
        pg.line(windowSizeX - cell, 0, windowSizeX - cell, cell);
        if (isPointInsideCloseButton(State.app.mouseX, State.app.mouseY)) {
            NodeTree.setAllOtherNodesMouseOver(null, false);
            pg.fill(ThemeStore.getColor(FOCUS_BACKGROUND));
            pg.noStroke();
            pg.rectMode(CORNER);
            pg.rect(windowSizeX - cell + 0.5f, 0, cell-1, cell);
            pg.stroke(ThemeStore.getColor(FOCUS_FOREGROUND));
            pg.strokeWeight(1.99f);
            pg.pushMatrix();
            pg.translate(windowSizeX - cell * 0.5f + 0.5f, cell * 0.5f);
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
        pg.translate(posX, posY);
        boolean highlight = shouldHighlightTitleBar();
        pg.fill(highlight ? ThemeStore.getColor(FOCUS_BACKGROUND) : ThemeStore.getColor(NORMAL_BACKGROUND));
        pg.noStroke();
        float titleBarWidth = windowSizeX - cell;
        if(!isCloseable){
             titleBarWidth += cell;
        }
        pg.rect(0, 0, titleBarWidth, titleBarHeight);
        pg.fill(highlight ? ThemeStore.getColor(FOCUS_FOREGROUND) : ThemeStore.getColor(NORMAL_FOREGROUND));
        pg.textAlign(LEFT, CENTER);
        String trimmedName = Utils.getTrimmedTextToFitOneLine(pg, parentNode.name, windowSizeX - cell * 1.1f);
        pg.text(trimmedName, State.textMarginX, cell - State.textMarginY);
        pg.stroke(ThemeStore.getColor(WINDOW_BORDER));
        pg.line(0, cell, windowSizeX, cell);
        pg.popMatrix();
    }


    protected boolean shouldHighlightTitleBar() {
        return isPointInsideTitleBar(State.app.mouseX, State.app.mouseY) || isDraggedAround;
    }

    private void constrainPosition(PGraphics pg) {
        float rightEdge = pg.width - windowSizeX - 1;
        float bottomEdge = pg.height - windowSizeY - 1;
        float lerpAmt = 0.3f;
        if (posX < 0) {
            posX = lerp(posX, 0, lerpAmt);
        }
        if (posY < 0) {
            posY = lerp(posY, 0, lerpAmt);
        }
        if (posX > rightEdge) {
            posX = lerp(posX, rightEdge, lerpAmt);
        }
        if (posY > bottomEdge) {
            posY = lerp(posY, bottomEdge, lerpAmt);
        }
    }

    @Override
    public void mousePressed(MouseEvent e, float x, float y) {
        if (isClosed()) {
            return;
        }
        if (isPointInsideWindow(x, y)) {
            if (!isFocused()) {
                setFocusOnThis();
            }
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
            posX += x - px;
            posY += y - py;
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

    public void open(boolean startDragging) {
        closed = false;
        if (startDragging) {
            isDraggedAround = true;
            setFocusOnThis();
        }
    }

    private boolean isClosed() {
        return closed || LazyGui.isGuiHidden;
    }

    void setFocusOnThis() {
        WindowManager.setFocus(this);
        UserInputPublisher.setFocus(this);
    }

    public boolean isPointInsideContent(float x, float y) {
        return Utils.isPointInRect(x, y,
                posX, posY + cell,
                windowSizeX, windowSizeY - cell);
    }

    public boolean isPointInsideSketchWindow(float x, float y) {
        PApplet app = State.app;
        return Utils.isPointInRect(x, y, 0, 0, app.width, app.height);
    }

    public boolean isPointInsideWindow(float x, float y) {
        return Utils.isPointInRect(x, y, posX, posY, windowSizeX, windowSizeY);
    }

    public boolean isPointInsideTitleBar(float x, float y) {
        if (isCloseable) {
            return Utils.isPointInRect(x, y, posX, posY, windowSizeX - cell, titleBarHeight);
        }
        return Utils.isPointInRect(x, y, posX, posY, windowSizeX, titleBarHeight);
    }

    protected boolean isPointInsideCloseButton(float x, float y) {
        return Utils.isPointInRect(x, y,
                posX + windowSizeX - cell - 1, posY,
                cell + 1, cell - 1);
    }
}
