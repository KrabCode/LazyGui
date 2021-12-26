package test;


import processing.core.PApplet;
import processing.core.PGraphics;
import toolbox.Gui;

public class RectangleFeedback extends PApplet {
    private PGraphics pg;
    private Gui gui;
    private float rotateBase;
    private float rotateRelative;

    public static void main(String[] args) {
        main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P3D);
//        size(800,800,P3D);
    }

    public void setup() {
        gui = new Gui(this);
        pg = createGraphics(width, height, P2D);
        pg.smooth(16);
    }

    public void draw() {
        pg.beginDraw();
        pg.rectMode(CORNER);
        pg.fill(gui.colorPicker("background").hex);
        pg.noStroke();
        pg.rect(0,0,width,height);
        pg.translate(width/2f,height/2f);
        pg.rectMode(CENTER);
        rotateBase += radians(gui.slider("rotation/time base", 0));
        rotateRelative += radians(gui.slider("rotation/time relative", 0));
        int minSize = gui.sliderInt("rectangles/size A", 10);
        int maxSize = gui.sliderInt("rectangles/size B", height);
        int count = gui.sliderInt("rectangles/count", 100);
        pg.rotate(gui.slider("rotation/base") + rotateBase);
        pg.strokeWeight(gui.slider("rectangles/weight", 1.99f));
        int strokeA = gui.colorPicker("rectangles/stroke A", 1f).hex;
        int strokeB = gui.colorPicker("rectangles/stroke B", 1f).hex;
        int fillA = gui.colorPicker("rectangles/fill A", 0x00000000).hex;
        int fillB = gui.colorPicker("rectangles/fill B", 0x00000000).hex;
        for (int i = count - 1; i >= 0; i--) {
            float iNorm = norm(i, 0, count-1);
            float size = minSize + iNorm * (maxSize - minSize);
            pg.fill(lerpColor(fillA, fillB, iNorm));
            pg.stroke(lerpColor(strokeA, strokeB, iNorm));
            pg.rotate(gui.slider("rotation/relative", 0.1f) + rotateRelative);
            pg.rect(0,0,size,size);
        }
        pg.endDraw();
        gui.shaderFilterList("shaders", pg);
        clear();
        image(pg, 0, 0);
        gui.record(pg);
        gui.draw(g);
    }
}

