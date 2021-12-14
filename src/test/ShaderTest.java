package test;

import processing.core.PApplet;
import processing.core.PGraphics;
import toolbox.Gui;
import toolbox.global.ShaderStore;

public class ShaderTest extends PApplet {

    Gui gui;
    PGraphics pg;

    public static void main(String[] args) {
        PApplet.main("test.ShaderTest");
    }

    public void settings() {
//        size(800, 800, P2D);
        fullScreen(P2D);
    }

    public void setup() {
        gui = new Gui(this);
        pg = createGraphics(width, height, P2D);
    }

    public void draw() {
        pg.beginDraw();
        pg.noStroke();
        pg.fill(gui.colorPicker("background").hex);
        pg.rectMode(CORNER);
        pg.rect(0, 0, pg.width, pg.height);
        gui.filterList("filters", pg);
        pg.endDraw();
        clear();
        image(pg, 0, 0);
        if (gui.toggle("invert")) {
            ShaderStore.hotFilter("filters/invert.glsl", g);
        }
        gui.draw();
        gui.record();
    }
}
