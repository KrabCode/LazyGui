package examples_intellij;

import lazy.LazyGui;
import processing.core.PApplet;
import processing.core.PGraphics;

public class GradientColorAtPointTest extends PApplet {
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
        drawForegroundPoints();
        pg.endDraw();
        image(pg, 0, 0);
    }

    private void drawForegroundPoints() {
        gui.pushFolder("foreground");
        int count = gui.sliderInt("point count");
        for (int i = 0; i < count; i++) {
            float x = random(width);
            float yNorm = random(1);
            float y = yNorm * height;
            float weight = gui.slider("weight");
            if( gui.toggle("use gradient color at")){
                pg.stroke(gui.gradientColorAt("stroke", yNorm).hex);
            }else{
                pg.stroke(gui.colorPicker("fill").hex);

            }
            pg.strokeWeight(weight);
            pg.point(x,y);
        }
        gui.popFolder();
    }

    private void drawBackground() {
        pg.fill(gui.colorPicker("background").hex);
        pg.noStroke();
        pg.rectMode(CORNER);
        pg.rect(0, 0, width, height);
    }
}
