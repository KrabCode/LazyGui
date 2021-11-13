package toolbox.windows;

import com.jogamp.newt.event.MouseEvent;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.Gui;
import toolbox.ToolboxMath;
import toolbox.GlobalState;
import toolbox.Palette;
import toolbox.tree.Node;
import toolbox.userInput.UserInputPublisher;
import toolbox.userInput.UserInputSubscriber;

import static processing.core.PApplet.*;

public abstract class Window implements UserInputSubscriber {
    protected float cell = GlobalState.cell;
    protected final PVector windowPos;  // position relative to sketch origin (top left)
    protected final PVector windowSize;
    protected boolean hidden = false;
    protected boolean isDraggedInside = false;
    protected final Node node;
    private final boolean closeable;
    private PGraphics g;
    protected boolean isDraggedAround;
    PVector dragAroundStartedMousePos = new PVector();
    PVector dragInsideStartedMousePos = new PVector();

    public Window(Node node, PVector pos, PVector size) {
        this.node = node;
        this.windowPos = pos;
        this.windowSize = size;
        this.closeable = true; // default is closeable, only the main tree window cannot be closed
        // TODO resizable
        initialize();
    }

    public Window(Node node, PVector pos, PVector size, boolean closeable) {
        this.node = node;
        this.windowPos = pos;
        this.windowSize = size;
        this.closeable = closeable;
        initialize();
    }

    private void initialize() {
        g = GlobalState.app.createGraphics(floor(windowSize.x), floor(windowSize.y), P2D);
        // do not g.beginDraw here, it's called from a user input thread, processing doesn't like it
        UserInputPublisher.subscribe(this);
    }

    public void drawWindow(PGraphics gui) {
        g.beginDraw();
        g.colorMode(HSB, 1, 1, 1, 1);
        g.textFont(GlobalState.font);
        g.clear();
        if (hidden) {
            g.endDraw();
            return;
        }
        constrainPosition(gui);
        drawBackground(g);
        drawContent(g);
        drawBorder(g);
        drawTitleBar(g);
        if (closeable) {
            drawCloseButton(g);
        }
        g.endDraw();
        gui.image(g, windowPos.x, windowPos.y);
    }

    private void constrainPosition(PGraphics gui) {
//        windowPos.x = constrain(windowPos.x, 0, );
//        windowPos.y = constrain(windowPos.y, 0, );
        float rightEdge = gui.width - windowSize.x - 1;
        float bottomEdge = gui.height - windowSize.y - 1;
        float lerpAmt = 0.1f;
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

    private void drawBackground(PGraphics pg) {
        pg.noStroke();
        pg.fill(Palette.windowContentFill);
        pg.rect(0, cell, pg.width, pg.height - cell);
    }

    protected abstract void drawContent(PGraphics pg);

    protected void drawBorder(PGraphics pg) {
        setBorderStrokeBasedOnFocus(pg);
        pg.noFill();
        pg.rect(0, cell, pg.width - 1, pg.height - cell - 1);
    }

    protected void drawTitleBar(PGraphics pg) {
        pg.fill(Palette.windowTitleFill);
        setBorderStrokeBasedOnFocus(pg);
        pg.rect(0, 0, pg.width - 1, cell);
        if(isThisFocused()){
            pg.fill(Palette.selectedTextFill);
        }else{
            pg.fill(Palette.standardTextFill);
        }
        pg.textAlign(LEFT);
        int textMarginX = 4;
        float textOffsetY = GlobalState.textOffsetY;
        pg.text(node.name, textMarginX, cell - textMarginX + textOffsetY);
    }

    protected boolean isThisFocused() {
        return WindowManager.isFocused(this);
    }

    private void drawCloseButton(PGraphics pg) {
        setBorderStrokeBasedOnFocus(pg);
        pg.noFill();
        pg.rect(windowSize.x - cell, 0, cell, cell);
    }

    protected void drawGrid(PGraphics pg) {
        pg.strokeWeight(1.5f);
        pg.stroke(Palette.darkContentStroke);
        int count = 50;
        float rectSize = 20;
        float gridSize = rectSize * count;
        for (float i = -gridSize; i < gridSize; i++) {
            if (i % 5 == 0) {
                pg.strokeWeight(3);
            } else {
                pg.strokeWeight(1);
            }
            pg.line(i * rectSize, -gridSize, i * rectSize, gridSize);
            pg.line(-gridSize, i * rectSize, gridSize, i * rectSize);
        }
    }

    private void setBorderStrokeBasedOnFocus(PGraphics pg) {
        if (isThisFocused()) {
            pg.stroke(Palette.windowBorderStrokeFocused);
        } else {
            pg.stroke(Palette.windowBorderStroke);
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
            dragAroundStartedMousePos.x = x;
            dragAroundStartedMousePos.y = y;
        } else if(isPointInsideContent(x,y)){
            isDraggedInside = true;
            dragInsideStartedMousePos.x = x;
            dragInsideStartedMousePos.y = y;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e, float x, float y, float px, float py) {
        if(isHidden()){
            return;
        }
        if (isDraggedAround) {
            if (isPointInsideSketchWindow(x, y)) {
                windowPos.x += x - px;
                windowPos.y += y - py;
            } else {
                isDraggedAround = false;
            }
            e.setConsumed(true);
        }else if (isDraggedInside) {
            reactToMouseDraggedInsideWithoutDrawing(x,y, px, py);
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
        } else {
            if (isPointInsideContent(x,y)) {
                reactToMouseReleasedInsideWithoutDrawing(x,y);
            }
            e.setConsumed(true);
        }
        isDraggedInside = false;
        isDraggedAround = false;
    }

    protected void reactToMouseDraggedInsideWithoutDrawing(float x, float y, float px, float py) {
//        println("dragged inside");
    }

    protected void reactToMouseReleasedInsideWithoutDrawing(float x, float y) {
//        println("released inside");
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
        return ToolboxMath.isPointInRect(x, y,
                windowPos.x, windowPos.y + cell,
                windowSize.x, windowSize.y - cell);
    }

    public boolean isPointInsideSketchWindow(float x, float y) {
        PApplet app = GlobalState.app;
        return ToolboxMath.isPointInRect(x, y, 0, 0, app.width, app.height);
    }

    public boolean isPointInsideWindow(float x, float y) {
        return ToolboxMath.isPointInRect(x, y, windowPos.x, windowPos.y, windowSize.x, windowSize.y);
    }

    public boolean isPointInsideTitleBar(float x, float y) {
        return ToolboxMath.isPointInRect(x, y, windowPos.x, windowPos.y, windowSize.x - cell, cell);
    }

    protected boolean isPointInsideCloseButton(float x, float y) {
        return ToolboxMath.isPointInRect(x, y,
                windowPos.x + windowSize.x - cell, windowPos.y,
                cell, cell);
    }
}
