package toolbox.tree;

import com.jogamp.newt.event.MouseEvent;
import com.sun.source.tree.Tree;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.Math;
import toolbox.userInput.UserInputPublisher;
import toolbox.userInput.UserInputSubscriber;

import java.util.ArrayList;

import static processing.core.PApplet.*;

public class TreeWindow extends UserInputSubscriber {
    PApplet app;
    PVector windowPos = new PVector(10, 10);
    PVector windowSize = new PVector(200, 400);
    float titleBarHeight = 20;
    PVector contentTranslate = new PVector();

    private int colorBrightStroke = 0xFFFFFFFF;
    private int colorDarkStroke = 0xFF36393E;
    private int colorBackground = 0xFF000000;

    private boolean isDraggedAround = false;
    private boolean isDraggedInside = false;

    PGraphics pg;
    TreeNode root = new TreeNode("/");

    public TreeWindow(PApplet app) {
        this.app = app;
        pg = app.createGraphics(floor(windowSize.x), floor(windowSize.y), P2D);
        UserInputPublisher.subscribe(this);
    }

    public void update(PGraphics gui) {
        pg.beginDraw();
        pg.noStroke();
        pg.fill(colorBackground);
        pg.rect(0,0,pg.width,pg.height);
        drawContent(pg);
        drawTitle(pg);
        drawBorder(pg);
        pg.endDraw();
        gui.image(pg, windowPos.x, windowPos.y);
    }

    private void drawContent(PGraphics pg) {
        pg.pushMatrix();
        pg.strokeWeight(1.5f);
        pg.stroke(colorDarkStroke);
        pg.translate(contentTranslate.x, contentTranslate.y);
        drawGrid(pg);
        pg.popMatrix();
    }

    private void drawGrid(PGraphics pg) {
        int count = 50;
        float rectSize = 20;
        float gridSize = rectSize * count;
        for(float i = -gridSize; i < gridSize; i++ ) {
            if(i%5 == 0){
                pg.strokeWeight(3);
            }else{
                pg.strokeWeight(1);
            }
            pg.line(i*rectSize, -gridSize, i*rectSize, gridSize);
            pg.line(-gridSize, i*rectSize, gridSize, i*rectSize);
        }
    }

    private void drawTitle(PGraphics pg) {
        pg.fill(colorBackground);
        pg.stroke(colorBrightStroke);
        pg.rect(0, 0, pg.width - 1, titleBarHeight);
        pg.fill(colorBrightStroke);
        int textMargin = 4;
        pg.textAlign(LEFT);
        pg.textSize(16);
        pg.text("reeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee", textMargin, titleBarHeight - textMargin);

    }

    private void drawBorder(PGraphics pg) {
        pg.stroke(colorBrightStroke);
        pg.noFill();
        pg.rect(0, 0, pg.width - 1, pg.height - 1);
        pg.endDraw();
    }

    public void mousePressed(float x, float y) {
        if (isPointInsideTreeWindowContent(x, y)) {
            isDraggedInside = true;
        } else if(isPointInsideTreeWindowTitleBar(x, y)){
            isDraggedAround = true;
        }
    }

    public void mouseDragged(float x, float y, float px, float py) {
        if (isDraggedAround) {
            windowPos.x += x - px;
            windowPos.y += y - py;
        } else if (isDraggedInside) {
            contentTranslate.x += x - px;
            contentTranslate.y += y - py;
        }
    }

    public void mouseReleased(float x, float y) {
        isDraggedInside = false;
        isDraggedAround = false;
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
