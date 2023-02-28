package examples_intellij;

import lazy.LazyGui;
import processing.core.PApplet;
import processing.core.PGraphics;

public class ReadmeCreator extends PApplet {
    LazyGui gui;
    PGraphics pg;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    @Override
    public void settings() {
        size(800, 800, P2D);
    }

    @Override
    public void setup() {
        gui = new LazyGui(this);
        pg = createGraphics(width, height, P2D);
    }

    @Override
    public void draw() {
        pg.beginDraw();
        drawBackground();
        gui.pushFolder("example folder");
        gui.radio("mode", new String[]{"square", "circle"});
        gui.popFolder();
        pg.endDraw();
        image(pg, 0, 0);
    }

    private void drawBackground() {
        background(gui.colorPicker("background").hex);
    }
}
