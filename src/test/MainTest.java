package test;

import processing.core.PApplet;
import processing.core.PGraphics;
import toolbox.Gui;

public class MainTest extends PApplet {
    Gui gui;
    PGraphics pg;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000,1000, P2D);
//        fullScreen(P2D);
    }

    public void setup() {
        gui = new Gui(this);
        pg = createGraphics(width, height, P2D);
    }

    @SuppressWarnings("DuplicatedCode")
    public void draw() {
        pg.beginDraw();
        pg.noStroke();
        pg.fill(gui.colorPicker("background", 0.1f).hex);
        pg.rect(0,0,width,height);

        gui.applyPremadeShaders("shaders", pg);
        pg.image(gui.imagePicker("image", "https://cdn.larryludwig.com/wp-content/uploads/2017/08/color-wheel-500x500.jpg"), 0, 0);
        pg.resetShader();
        gui.applyPremadeFilters("filters", pg);
        pg.endDraw();
        clear();
        image(pg, 0, 0);
        gui.draw();
        gui.record();
    }
}
