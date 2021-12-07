package test;

import processing.core.PApplet;
import processing.core.PConstants;
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
        pg.noStroke();
        pg.fill(gui.colorPicker("background").hex);
        pg.rectMode(CORNER);
        pg.rect(0, 0, pg.width, pg.height);
        pg.fill(gui.colorPicker("fill").hex);
        pg.stroke(gui.colorPicker("stroke", 1).hex);
        pg.rectMode(CENTER);
        pg.strokeWeight(gui.slider("weight", 1.9f));
        float size = gui.slider("size", 60);
        pg.translate(pg.width * 0.5f, pg.height * 0.5f);
        pg.rect(0,0,size,size);
        gui.filterList("shaders", pg);
        pg.endDraw();
        image(pg, 0, 0);
        gui.draw();
        gui.record();
    }
}
