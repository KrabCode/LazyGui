package com.krab.lazy.examples_intellij;

import com.krab.lazy.LazyGui;
import processing.core.PApplet;
import processing.core.PGraphics;

public class SliderSketch extends PApplet {
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
        colorMode(HSB, 1, 1, 1, 1);
        frameRate(144);
    }

    @Override
    public void draw() {
        pg.beginDraw();
        drawBackground();
        drawArc();
        pg.endDraw();
        image(pg, 0, 0);
        gui.draw();
    }

    private void drawArc() {
        float maxAngleNorm = gui.slider("max angle", 1, 0, 1);
        pg.translate(width/2f, height/2f);
        pg.fill(gui.colorPicker("fill", color(0.2f)).hex);
        pg.stroke(gui.colorPicker("stroke", color(0.8f)).hex);
        pg.strokeWeight(gui.slider("stroke weight", 4));
        float diameter = gui.slider("diameter", 400);
        pg.arc(0, 0, diameter, diameter, 0, maxAngleNorm * TWO_PI);
    }

    private void drawBackground() {
        if(gui.toggle("gradient", true)){
            pg.image(gui.gradient("background"), 0, 0);
            return;
        }
        pg.fill(gui.colorPicker("solid").hex);
        pg.noStroke();
        pg.rectMode(CORNER);
        pg.rect(0, 0, width, height);
    }
}

