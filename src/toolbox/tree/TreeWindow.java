package toolbox.tree;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.style.Palette;
import toolbox.window.Window;

public class TreeWindow extends Window {
    PVector contentTranslate = new PVector();

    private boolean isDraggedInside = false;
    TreeNode root = new TreeNode("");

    public TreeWindow(PApplet app) {
        super(app, new PVector(20, 20), new PVector(150, 250));
    }

    protected void drawContent(PGraphics pg) {
        pg.pushMatrix();
        pg.strokeWeight(1.5f);
        pg.fill(120);
        pg.stroke(Palette.darkContentStroke);
        pg.translate(contentTranslate.x, contentTranslate.y);
        drawGrid(pg);
        pg.popMatrix();
    }

    private void drawGrid(PGraphics pg) {
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

    @Override
    public void mousePressed(float x, float y) {
        super.mousePressed(x, y);
        if(!isDraggedAround && isPointInsideContent(x,y)){
            isDraggedInside = true;
        }
    }

    @Override
    public void mouseReleased(float x, float y) {
        super.mouseReleased(x, y);
        isDraggedInside = false;
    }

    @Override
    public void mouseDragged(float x, float y, float px, float py) {
        super.mouseDragged(x, y, px, py);
        if(!isDraggedAround && isDraggedInside){
            contentTranslate.x += x - px;
            contentTranslate.y += y - py;
        }
    }
}
