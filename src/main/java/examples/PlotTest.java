package examples;

import lazy.LazyGui;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class PlotTest extends PApplet {
    LazyGui gui;
    PGraphics pg;
    private float rotation;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    @Override
    public void settings() {
        size(800, 800, P2D);
//        smooth(16);
    }

    @Override
    public void setup() {
        gui = new LazyGui(this);
        pg = createGraphics(width, height, P2D);
        pg.smooth(16);
    }

    @Override
    public void draw() {
        pg.beginDraw();
        drawBackground();
        gui.pushFolder("test");
        gui.pushFolder("factual information");
        gui.text("this is a header", "content under header\nhello :3");
        gui.slider("random slider", 0);
        gui.text("", "");
        gui.popFolder();
        PVector pos = gui.plotXY("pos");
        PVector size = gui.plotXY("size");
        rotation += radians(gui.slider("rotation +"));
        pg.fill(gui.colorPicker("fill").hex);
        pg.noStroke();
        pg.translate(pos.x, pos.y);
        pg.rotate(rotation);
        pg.rectMode(CENTER);
        pg.rect(0,0,size.x,size.y);
        gui.popFolder();
        pg.endDraw();
        image(pg, 0, 0);
    }

    private void drawBackground() {
        pg.fill(gui.colorPicker("background").hex);
        pg.noStroke();
        pg.rectMode(CORNER);
        pg.rect(0, 0, width, height);
    }
}
