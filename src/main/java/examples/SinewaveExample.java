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
        size(800, 800, P2D);
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
        drawBackground();
        drawSinewave();
        pg.endDraw();
        image(pg, 0, 0);
    }

    private void drawSinewave() {
        String path = "sinewave/";
        int detail = gui.sliderInt(path + "detail", 100);
        float freq = gui.slider(path + "freq", 0.01f);
        time += radians(gui.slider(path + "time", 1));
        pg.translate(width/2f + gui.slider(path + "x"), height/2f +  + gui.slider(path + "y"));
        float w = gui.slider(path + "width", 400);
        float h = gui.slider(path + "height", 200);
        pg.noFill();
        pg.stroke(gui.colorPicker(path + "stroke", color(255)).hex);
        pg.strokeWeight(gui.slider(path + "weight", 5));
        pg.beginShape();
        for (int i = 0; i < detail; i++) {
            float norm = norm(i, 0, detail-1);
            float x = -w/2f + w * norm;
            float y = h * sin(norm * freq * TAU + time);
            pg.vertex(x,y);
        }
        pg.endShape();
    }

    private void drawBackground() {
        pg.fill(gui.colorPicker("background", color(255*0.15f)).hex);
        pg.noStroke();
        pg.rectMode(CORNER);
        pg.rect(0, 0, width, height);
    }
}

