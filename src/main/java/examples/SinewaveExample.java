package examples;

import processing.core.PApplet;
import processing.core.PGraphics;
import lazy.LazyGui;
import processing.core.PVector;

public class SinewaveExample extends PApplet {
    LazyGui gui;
    PGraphics pg;
    float time;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    @Override
    public void settings() {
        size(1200, 1000, P2D);
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
        gui.pushFolder("scene");
        drawBackground();
        pg.translate(width / 2f, height / 2f);
        drawRectangle();
        drawSinewave();
        gui.popFolder();
        pg.endDraw();
        image(pg, 0, 0);
    }

    private void drawRectangle() {
        pg.pushMatrix();
        gui.pushFolder("rect");
        pg.fill(gui.colorPicker("fill", 0xFF000000).hex);
        pg.stroke(gui.colorPicker("stroke", 0xFFA0A0A0).hex);
        pg.strokeWeight(gui.slider("weight", 6));
        pg.rectMode(CENTER);
        PVector pos = gui.plotXY("pos");
        PVector size = gui.plotXY("size", 500, 280);
        pg.rect(pos.x, pos.y, size.x, size.y);
        gui.popFolder();
        pg.popMatrix();
    }

    private void drawSinewave() {
        pg.pushMatrix();
        gui.pushFolder("sinewave");
        int detail = gui.sliderInt("detail", 100);
        float freq = gui.slider("freq", 1);
        time += radians(gui.slider("time", 1));
        PVector pos = gui.plotXY("pos");
        PVector size = gui.plotXY("size", 400, 200);
        pg.translate(pos.x, pos.y);
        pg.noFill();
        pg.stroke(gui.colorPicker("stroke", color(255)).hex);
        pg.strokeWeight(gui.slider("weight", 4));
        pg.beginShape();
        for (int i = 0; i < detail; i++) {
            float norm = norm(i, 0, detail - 1);
            float x = -size.x / 2f + size.x * norm;
            float y = size.y * 0.5f * sin(norm * freq * TAU + time);
            pg.vertex(x, y);
        }
        gui.popFolder();
        pg.endShape();
        pg.popMatrix();
    }

    private void drawBackground() {
        pg.fill(gui.colorPicker("background", color(255 * 0.15f)).hex);
        pg.noStroke();
        pg.rectMode(CORNER);
        pg.rect(0, 0, width, height);
    }
}

