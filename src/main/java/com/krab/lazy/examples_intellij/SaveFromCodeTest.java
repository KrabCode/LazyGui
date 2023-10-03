package com.krab.lazy.examples_intellij;

import com.krab.lazy.LazyGui;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class SaveFromCodeTest extends PApplet {
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
        pg.beginDraw();
        pg.colorMode(HSB, 1, 1, 1, 1);
        pg.endDraw();
        loadRandomImage();
        colorMode(HSB, 1, 1, 1, 1);
        frameRate(144);
    }

    private void loadRandomImage() {
        drawImageOnCanvas(loadImage("https://picsum.photos/800/800.jpg"));
    }

    private void drawImageOnCanvas(PImage img) {
        pg.beginDraw();
        pg.colorMode(HSB, 1, 1, 1, 1);
        pg.background(1);
        pg.image(img, 0, 0);
        pg.endDraw();
    }

    @Override
    public void mousePressed(){
        pg.beginDraw();
        pg.noStroke();
        pg.fill(0.1f);
        pg.ellipse(mouseX, mouseY, 10, 10);
        pg.endDraw();
    }

    @Override
    public void mouseDragged(){
        pg.beginDraw();
        float t = (frameCount * 0.01f) % 1;
        pg.stroke(t, 1, 1);
        pg.noFill();
        pg.strokeWeight(10);
        pg.line(mouseX, mouseY, pmouseX, pmouseY);
        pg.endDraw();
    }

    @Override
    public void draw() {
        if(gui.button("random image")){
            loadRandomImage();
        }
        String savePath = gui.text("save name\\/path");
        String imagePath = savePath + ".jpg";
        if(gui.button("create save")) {
            gui.createSave(savePath);
            pg.save(imagePath);
            println("Saved img at: " + imagePath);
        }
        if(gui.button("load save")){
            gui.loadSave(savePath);
            drawImageOnCanvas(loadImage(imagePath));
            println("Loaded img from: " + imagePath);
        }
        image(pg, 0, 0);
    }
}

