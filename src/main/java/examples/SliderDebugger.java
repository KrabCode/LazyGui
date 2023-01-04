package examples;

import lazy.LazyGui;
import processing.core.PApplet;
import processing.core.PGraphics;

public class SliderDebugger extends PApplet {
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
        gui.pushFolder("sliders");
        int intVal = gui.sliderInt("integer slider", 12);
        float floatVal = gui.slider("float slider", 3.14159f);
        gui.textDisplay("~~ slider tutorial ~~", "sliders are very nice\nthey come in different forms\nand all are super cool");
        gui.textInput("notes", "edit meeee");
        gui.popFolder();
        pg.fill(255);
        pg.textSize(64);
        pg.textAlign(CENTER, CENTER);
        pg.translate(width/2f, height/2f);
        pg.rotate(floatVal);
        pg.text(intVal, 0, 0);
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