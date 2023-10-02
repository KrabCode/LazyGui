package com.krab.lazy.examples_intellij;

import com.krab.lazy.LazyGui;
import processing.core.PApplet;
import processing.core.PGraphics;

public class SaveTest extends PApplet {
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
        if(gui.button("# save")){
            gui.createSave();
        }
        String filename = gui.text("name");
        if(gui.button("named save")){
            gui.createSave(filename);
        }
        if(gui.button("named load")){
            gui.loadSave(filename);
        }
        pg.endDraw();
        image(pg, 0, 0);
        gui.draw();
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

