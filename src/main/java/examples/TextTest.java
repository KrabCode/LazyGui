package examples;

import lazy.LazyGui;
import processing.core.PApplet;
import processing.core.PGraphics;

public class TextTest extends PApplet {
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
        pg.fill(gui.colorPicker("fill", 0xFFFFFFFF).hex);
        pg.textAlign(CENTER, CENTER);
        pg.textSize(gui.slider("font size", 64));
        pg.text(gui.text("main text", "hello"), width/2f, height/2f);
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
