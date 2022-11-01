package lazy;

import com.google.gson.annotations.Expose;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

import static lazy.State.cell;
import static processing.core.PApplet.lerp;
import static processing.core.PApplet.println;
import static processing.core.PConstants.*;
import static lazy.ThemeColorType.*;

abstract class Window implements UserInputSubscriber {
    @Expose
    float posX;
    @Expose
    float posY;
    @Expose
    boolean closed = false;
    float windowSizeX, windowSizeY;
    protected boolean isCloseable;
    protected AbstractNode parentNode;
    boolean isDraggedAround;

    Window(float posX, float posY, AbstractNode parentNode, boolean isCloseable) {
        this.posX = posX;
        this.posY = posY;
        this.parentNode = parentNode;
        this.isCloseable = isCloseable;
        UserInputPublisher.subscribe(this);
    }


    boolean isFocused() {
        return WindowManager.isFocused(this);
    }

    void drawWindow(PGraphics pg, PGraphics overlay) {
        pg.textFont(State.font);
        if (closed) {
            return;
        }
        constrainPosition(pg);
        pg.pushMatrix();
        drawBackgroundWithWindowBorder(pg);
        boolean highlight = (isPointInsideTitleBar(State.app.mouseX, State.app.mouseY) && isDraggedAround) || parentNode.isMouseOverNode;
        if(LazyGui.showContextLines && highlight && parentNode != NodeTree.getRoot()){
            drawConnectingLineFromTitleBarToInlineNode(pg);
        }
        drawTitleBar(pg, highlight);
        drawPathTooltipOnHighlight(pg);
        drawContent(pg);
        if (isCloseable) {
            drawCloseButton(pg);
        }
        pg.popMatrix();
    }

    private void drawPathTooltipOnHighlight(PGraphics pg) {
        if (!isPointInsideTitleBar(State.app.mouseX, State.app.mouseY) || !LazyGui.showPathTooltips) {
            return;
        }
        pg.pushMatrix();
        pg.pushStyle();
        pg.translate(posX, posY);
        String[] pathSplit = Utils.splitFullPathWithoutEndAndRoot(parentNode.path);
        int lineCount = pathSplit.length;
        float tooltipHeight = lineCount * cell;
        float tooltipYOffset = -1;
        float tooltipXOffset = cell * 0.5f;
        float tooltipWidth = windowSizeX - tooltipXOffset - cell;
//        pg.stroke(ThemeStore.getColor(WINDOW_BORDER)); // tooltip border maybe?
        pg.noStroke();
        pg.fill(ThemeStore.getColor(NORMAL_BACKGROUND));
        pg.rect(tooltipXOffset, tooltipYOffset - tooltipHeight, tooltipWidth, tooltipHeight);
        pg.fill(ThemeStore.getColor(NORMAL_FOREGROUND));
        pg.textAlign(LEFT,CENTER);
        for (int i = 0; i < lineCount; i++) {
            pg.text(pathSplit[lineCount - 1 - i], State.textMarginX + tooltipXOffset, tooltipYOffset -i * cell - State.textMarginY);
        }
        pg.popMatrix();
        pg.popStyle();
    }

    protected void drawBackgroundWithWindowBorder(PGraphics pg) {
        pg.pushMatrix();
        pg.translate(posX, posY);
        pg.stroke(ThemeStore.getColor(WINDOW_BORDER));
        pg.strokeWeight(1);
        pg.fill(ThemeStore.getColor(NORMAL_BACKGROUND));
        //TODO make window fit exactly, fix overlaps... maybe draw background first, content middle and border last?
        // https://github.com/KrabCode/LazyGui/issues/44
        pg.rect(-1, -1, windowSizeX + 1, windowSizeY + 1);
        pg.popMatrix();
    }

    private void drawCloseButton(PGraphics pg) {
        pg.pushMatrix();
        pg.translate(posX, posY);
        pg.stroke(ThemeStore.getColor(WINDOW_BORDER));
        pg.strokeWeight(1);
        pg.line(windowSizeX - cell, 0, windowSizeX - cell, cell - 1);
        if (isPointInsideCloseButton(State.app.mouseX, State.app.mouseY)) {
            pg.fill(ThemeStore.getColor(FOCUS_BACKGROUND));
            pg.noStroke();
            pg.rectMode(CORNER);
            pg.rect(windowSizeX - cell + 0.5f, 0, cell-1, cell - 1);
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

    protected void drawTitleBar(PGraphics pg, boolean highlight) {
        pg.pushMatrix();
        pg.pushStyle();
        pg.translate(posX, posY);
        pg.fill(highlight ? ThemeStore.getColor(FOCUS_BACKGROUND) : ThemeStore.getColor(NORMAL_BACKGROUND));
        float titleBarWidth = windowSizeX;
        pg.stroke(ThemeStore.getColor(WINDOW_BORDER));
        pg.rect(-1, -1, titleBarWidth + 1, cell);
        pg.fill(highlight ? ThemeStore.getColor(FOCUS_FOREGROUND) : ThemeStore.getColor(NORMAL_FOREGROUND));
        pg.textAlign(LEFT, CENTER);
        String trimmedName = Utils.getTrimmedTextToFitOneLine(pg, parentNode.name, windowSizeX - cell * 1.1f);
        pg.text(trimmedName, State.textMarginX, cell - State.textMarginY);
        pg.popStyle();
        pg.popMatrix();
    }

    private void drawConnectingLineFromTitleBarToInlineNode(PGraphics overlay) {
        if(!parentNode.isParentWindowVisible()){
            return;
        }
        overlay.pushStyle();
        overlay.stroke(ThemeStore.getColor(NORMAL_FOREGROUND));
        overlay.strokeCap(ROUND);
        overlay.strokeWeight(1);
        overlay.line(posX + 1, posY + cell / 2f, parentNode.pos.x + parentNode.size.x - 1, parentNode.pos.y + parentNode.size.y  / 2f);
        overlay.popStyle();
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
    public void mousePressed(LazyMouseEvent e) {
        if (isClosed()) {
            return;
        }
        if (isPointInsideWindow(e.getX(), e.getY())) {
            if (!isFocused()) {
                setFocusOnThis();
            }
            e.setConsumed(true);
        }
        if (isPointInsideTitleBar(e.getX(), e.getY())) {
            isDraggedAround = true;
            setFocusOnThis();
        }
    }

    @Override
    public void mouseDragged(LazyMouseEvent e) {
        if (isClosed()) {
            return;
        }
        if (isDraggedAround) {
            posX += e.getX() - e.getPrevX();
            posY += e.getY() - e.getPrevY();
            e.setConsumed(true);
        }
    }

    @Override
    public void mouseReleased(LazyMouseEvent e) {
        if (isClosed()) {
            return;
        }
        if (isCloseable && isPointInsideCloseButton(e.getX(), e.getY())) {
            close();
            e.setConsumed(true);
        } else if (isDraggedAround) {
            e.setConsumed(true);
            trySnapToGrid();
        }
        isDraggedAround = false;
    }

    private void trySnapToGrid() {
        PVector snappedPos = GridSnapHelper.snapToGrid(posX, posY);
        posX = snappedPos.x;
        posY = snappedPos.y;
    }

    void close() {
        closed = true;
        isDraggedAround = false;
    }

    void open(boolean startDragging) {
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

    boolean isPointInsideContent(float x, float y) {
        return Utils.isPointInRect(x, y,
                posX, posY + cell,
                windowSizeX, windowSizeY - cell);
    }

    boolean isPointInsideSketchWindow(float x, float y) {
        PApplet app = State.app;
        return Utils.isPointInRect(x, y, 0, 0, app.width, app.height);
    }

    boolean isPointInsideWindow(float x, float y) {
        return Utils.isPointInRect(x, y, posX, posY, windowSizeX, windowSizeY);
    }

    boolean isPointInsideTitleBar(float x, float y) {
        if (isCloseable) {
            return Utils.isPointInRect(x, y, posX, posY, windowSizeX - cell, cell);
        }
        return Utils.isPointInRect(x, y, posX, posY, windowSizeX, cell);
    }

    protected boolean isPointInsideCloseButton(float x, float y) {
        return Utils.isPointInRect(x, y,
                posX + windowSizeX - cell - 1, posY,
                cell + 1, cell - 1);
    }
}
