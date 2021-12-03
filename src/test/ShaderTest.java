package test;

import processing.core.PApplet;
import processing.core.PGraphics;
import toolbox.Gui;

public class ShaderTest extends PApplet {

    Gui gui;
    PGraphics pg;

    public static void main(String[] args) {
        PApplet.main("test.ShaderTest");
    }

    public void settings() {
        size(800, 800, P2D);
    }

    public void setup() {
        gui = new Gui(this);
        pg = createGraphics(width, height, P2D);
    }

    public void draw() {
        pg.beginDraw();
        pg.fill(gui.colorPicker("background").hex);
        pg.rect(0, 0, pg.width, pg.height);
        gui.filter("shaders", pg);
        pg.endDraw();
        image(pg, 0, 0);
        gui.draw();
        gui.record();
    }
}
