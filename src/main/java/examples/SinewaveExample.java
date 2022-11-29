package examples;

import processing.core.PApplet;
import processing.core.PGraphics;
import lazy.LazyGui;

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
        pg.translate(width/2f, height/2f);
        pg.rect(gui.slider("x"),
                gui.slider("y"),
                gui.slider("w", 500),
                gui.slider("h", 280));
        gui.popFolder();
        pg.popMatrix();
    }

    private void drawSinewave() {
        gui.pushFolder("sinewave");
        int detail = gui.sliderInt("detail", 100);
        float freq = gui.slider("freq", 1);
        time += radians(gui.slider("time", 1));
        pg.translate(width/2f + gui.slider("x"), height/2f +  + gui.slider("y"));
        float w = gui.slider("width", 400);
        float h = gui.slider("height", 200);
        pg.noFill();
        pg.stroke(gui.colorPicker("stroke", color(255)).hex);
        pg.strokeWeight(gui.slider("weight", 4));
        pg.beginShape();
        for (int i = 0; i < detail; i++) {
            float norm = norm(i, 0, detail-1);
            float x = -w/2f + w * norm;
            float y = h * 0.5f * sin(norm * freq * TAU + time);
            pg.vertex(x,y);
        }
        gui.popFolder();
        pg.endShape();
    }

    private void drawBackground() {
        pg.fill(gui.colorPicker("background", color(255*0.15f)).hex);
        pg.noStroke();
        pg.rectMode(CORNER);
        pg.rect(0, 0, width, height);
    }
}

