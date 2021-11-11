package toolbox.window;

import com.jogamp.newt.event.MouseEvent;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.Math;
import toolbox.style.Palette;
import toolbox.userInput.UserInputPublisher;
import toolbox.userInput.UserInputSubscriber;

import static processing.core.PApplet.*;

public abstract class Window implements UserInputSubscriber {
    protected final String path;
    protected final PVector windowPos;
    protected final PVector windowSize;
    protected boolean hidden = false;
    private final String title;
    private final boolean closeable;
    protected final float cellSize = 20;
    private PGraphics g;
    protected final PApplet app;
    protected boolean isDraggedAround;

    public Window(PApplet app, String path, String title, PVector pos, PVector size) {
        this.app = app;
        this.path = path;
        this.title = title;
        this.windowPos = pos;
        this.windowSize = size;
        this.closeable = true; // default is closeable, only the main tree window cannot be closed
        initialize(size);
    }

    public Window(PApplet app, String path, String title, PVector pos, PVector size, boolean closeable) {
        this.app = app;
        this.path = path;
        this.title = title;
        this.windowPos = pos;
        this.windowSize = size;
        this.closeable = closeable;
        initialize(size);
    }

    private void initialize(PVector size) {
        g = app.createGraphics(floor(size.x), floor(size.y), P2D);
        UserInputPublisher.subscribe(this);
    }

    public void drawWindow(PGraphics gui) {
        g.beginDraw();
        g.colorMode(HSB, 1, 1, 1, 1);
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
        pg.rect(0, cellSize, pg.width, pg.height - cellSize);
    }

    protected abstract void drawContent(PGraphics pg);

    protected void drawBorder(PGraphics pg) {
        setBorderStrokeBasedOnFocus(pg);
        pg.noFill();
        pg.rect(0, cellSize, pg.width - 1, pg.height - cellSize - 1);
    }

    protected void drawTitleBar(PGraphics pg) {
        pg.fill(Palette.windowTitleFill);
        setBorderStrokeBasedOnFocus(pg);
        pg.rect(0, 0, pg.width - 1, cellSize);
        pg.fill(Palette.windowTitleTextFill);
        int textMargin = 4;
        pg.textAlign(LEFT);
        pg.textSize(16);
        pg.text(title, textMargin, cellSize - textMargin);
    }

    private void drawCloseButton(PGraphics pg) {
        setBorderStrokeBasedOnFocus(pg);
        pg.noFill();
        pg.rect(windowSize.x - cellSize, 0, cellSize, cellSize);
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
        if (WindowManager.isFocused(this)) {
            pg.stroke(Palette.windowBorderStrokeFocused);
        } else {
            pg.stroke(Palette.windowBorderStroke);
        }
    }

    @Override
    public void mousePressed(MouseEvent e, float x, float y) {
        if (isPointInsideTitleBar(x, y)) {
            isDraggedAround = true;
            setFocusOnThis();
            e.setConsumed(true);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e, float x, float y, float px, float py) {
        if (isDraggedAround) {
            if(isPointInsideSketchWindow(x,y)){
                windowPos.x += x - px;
                windowPos.y += y - py;
            }else{
                isDraggedAround = false;
            }
            e.setConsumed(true);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e, float x, float y) {
        if (closeable && isPointInsideCloseButton(x, y)) {
            hide();
            e.setConsumed(true);
        } else if (isDraggedAround) {
            e.setConsumed(true);
        } else if (isPointInsideWindow(x, y)) {
            setFocusOnThis();
            e.setConsumed(true);
        }
        isDraggedAround = false;
    }

    private void hide() {
        // todo unsub manually here?
        hidden = true;
    }

    public void uncover() {
        UserInputPublisher.subscribe(this);
        hidden = false;
        setFocusOnThis();
    }

    private void setFocusOnThis() {
        WindowManager.setFocus(this);
        UserInputPublisher.setFocus(this);
    }

    public boolean isPointInsideContent(float x, float y) {
        return Math.isPointInRect(x, y,
                windowPos.x, windowPos.y + cellSize,
                windowSize.x, windowSize.y - cellSize);
    }

    public boolean isPointInsideSketchWindow(float x, float y) {
        return Math.isPointInRect(x, y, 0, 0, app.width, app.height);
    }

    public boolean isPointInsideWindow(float x, float y) {
        return Math.isPointInRect(x, y, windowPos.x, windowPos.y, windowSize.x, windowSize.y);
    }

    public boolean isPointInsideTitleBar(float x, float y) {
        return Math.isPointInRect(x, y, windowPos.x, windowPos.y, windowSize.x - cellSize, cellSize);
    }

    protected boolean isPointInsideCloseButton(float x, float y) {
        return Math.isPointInRect(x, y,
                windowPos.x + windowSize.x - cellSize, windowPos.y,
                cellSize, cellSize);
    }
}
