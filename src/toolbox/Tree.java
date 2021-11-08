package toolbox;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.userInput.UserInputPublisher;
import toolbox.userInput.UserInputSubscriber;

import static processing.core.PApplet.*;

public class Tree implements UserInputSubscriber {
    PApplet app;
    PVector windowPos = new PVector(10, 10);
    PVector windowSize = new PVector(200, 400);
    float titleBarHeight = 30;
    PVector contentTranslate = new PVector();


    private int colorBrightStroke = 0xFFFFFFFF;
    private int colorDarkStroke = 0xFF36393E;
    private int colorBackground = 0xFF000000;

    boolean clicked = false;
    private boolean isDraggedAround = false;
    private boolean isDraggedInside = false;

    PGraphics pg;

    public Tree(PApplet app) {
        this.app = app;
        pg = app.createGraphics(floor(windowSize.x), floor(windowSize.y), P2D);
        UserInputPublisher.subscribe(this);
    }

    public void update(PGraphics gui) {

        float mouseDeltaX = app.mouseX - app.pmouseX;
        float mouseDeltaY = app.mouseY - app.pmouseY;
        if (isDraggedAround) {
            windowPos.x += mouseDeltaX;
            windowPos.y += mouseDeltaY;
        } else if (isDraggedInside) {
            contentTranslate.x += mouseDeltaX;
            contentTranslate.y += mouseDeltaY;
        }

        pg.beginDraw();
        pg.background(colorBackground);
        pg.strokeWeight(1);
        drawContent(pg);
        drawTitle(pg);
        drawBorder(pg);
        gui.image(pg, windowPos.x, windowPos.y);
    }

    private void drawBorder(PGraphics pg) {
        pg.stroke(colorBrightStroke);
        pg.noFill();
        pg.rect(0, 0, pg.width - 1, pg.height - 1);
        pg.endDraw();
    }

    private void drawTitle(PGraphics pg) {
        pg.fill(colorBackground);
        pg.stroke(colorBrightStroke);
        pg.rect(0, 0, pg.width - 1, titleBarHeight);
        pg.fill(colorBrightStroke);
        int textMargin = 4;
        pg.textAlign(LEFT);
        pg.textSize(24);
        pg.text("Tree", textMargin, titleBarHeight - textMargin);
    }

    private void drawContent(PGraphics pg) {
        int count = 100;
        float rectSize = 20;
        float gridSize = rectSize * count;
        pg.pushMatrix();
        pg.strokeWeight(1);
        pg.stroke(colorDarkStroke);
        pg.translate(contentTranslate.x, contentTranslate.y);
        for(float i = -gridSize; i < gridSize; i++ ) {
            pg.line(i*rectSize, -gridSize, i*rectSize, gridSize);
            pg.line(-gridSize, i*rectSize, gridSize, i*rectSize);
        }
        pg.popMatrix();
    }

    public void onMouseClick(float x, float y) {
        if (isPointInsideTreeWindowContent(x, y)) {
            clicked = !clicked;
        }
    }

    @Override
    public void onMouseDrag(float x, float y, float px, float py) {
        if (isPointInsideTreeWindowTitleBar(x, y)) {
            if (!isDraggedInside) {
                isDraggedAround = true;
            }
        } else if (isPointInsideTreeWindowContent(x, y)) {
            isDraggedInside = true;
        }
    }

    @Override
    public void onMouseRelease(int x, int y) {
        isDraggedAround = false;
        isDraggedInside = false;
    }

    public boolean isPointInsideTreeWindow(float x, float y) {
        return Math.isPointInRect(x, y, windowPos, windowSize);
    }

    public boolean isPointInsideTreeWindowTitleBar(float x, float y) {
        return Math.isPointInRect(x, y, windowPos.x, windowPos.y, windowSize.x, titleBarHeight);
    }

    public boolean isPointInsideTreeWindowContent(float x, float y) {
        return isPointInsideTreeWindow(x, y) && !isPointInsideTreeWindowTitleBar(x, y);
    }
}
