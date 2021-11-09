package toolbox.window;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.Math;
import toolbox.style.Palette;
import toolbox.userInput.UserInputPublisher;
import toolbox.userInput.UserInputSubscriber;

import static processing.core.PApplet.floor;
import static processing.core.PConstants.*;

public abstract class Window implements UserInputSubscriber {
    private final PGraphics g;
    private final PVector windowPos;
    private final PVector windowSize;
    private final float titleBarHeight = 20;
    protected boolean isDraggedAround;

    public Window(PApplet app, PVector pos, PVector size){
        windowPos = pos;
        windowSize = size;
        g = app.createGraphics(floor(size.x), floor(size.y), P2D);
        UserInputPublisher.subscribe(this);
    }

    public void drawWindow(PGraphics gui) {
        g.beginDraw();
        g.clear();
        drawContent(g);
        drawBorder(g);
        drawTitle(g);
        g.endDraw();
        gui.image(g, windowPos.x, windowPos.y);
    }

    protected abstract void drawContent(PGraphics pg);

    protected void drawBorder(PGraphics pg) {
        pg.stroke(Palette.windowBorderStroke);
        pg.noFill();
        pg.rect(0, titleBarHeight, pg.width - 1, pg.height - titleBarHeight - 1);
    }

    protected void drawTitle(PGraphics pg) {
        pg.fill(Palette.windowTitleFill);
        pg.stroke(Palette.windowBorderStroke);
        pg.rect(0, 0, pg.width - 1, titleBarHeight);
        pg.fill(Palette.windowTitleTextFill);
        int textMargin = 4;
        pg.textAlign(LEFT);
        pg.textSize(16);
        pg.text("tree", textMargin, titleBarHeight - textMargin);

    }

    public void mousePressed(float x, float y) {
        if(isPointInsideTitleBar(x, y)){
            isDraggedAround = true;
        }
    }

    public void mouseDragged(float x, float y, float px, float py) {
        if (isDraggedAround) {
            windowPos.x += x - px;
            windowPos.y += y - py;
        }
    }

    public void mouseReleased(float x, float y) {
        isDraggedAround = false;
    }

    public boolean isPointInsideTitleBar(float x, float y) {
        return Math.isPointInRect(x, y, windowPos.x, windowPos.y, windowSize.x, titleBarHeight);
    }

    public boolean isPointInsideContent(float x, float y) {
        return Math.isPointInRect(x, y,
                windowPos.x, windowPos.y + titleBarHeight,
                windowSize.x, windowSize.y - titleBarHeight);
    }
}
