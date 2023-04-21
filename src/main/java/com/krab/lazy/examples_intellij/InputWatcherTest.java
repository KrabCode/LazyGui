package com.krab.lazy.examples_intellij;

import com.krab.lazy.InputWatcher;
import com.krab.lazy.LazyGui;
import processing.core.PApplet;
import processing.core.PGraphics;

public class InputWatcherTest extends PApplet {
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
        if (InputWatcher.getKey('w')) {
            print("w");
        }
        if (InputWatcher.getKey('s')) {
            print("s");
        }
        if (InputWatcher.getKey('a')) {
            print("a");
        }
        if (InputWatcher.getKey('d')) {
            print("d");
        }
        if (InputWatcher.getKey(' ')) {
            print("_");
        }
        println();
        pg.endDraw();
        image(pg, 0, 0);
    }

    private void drawBackground() {
        pg.fill(gui.colorPicker("background").hex);
        pg.noStroke();
        pg.rectMode(CORNER);
        pg.rect(0, 0, width, height);
    }
}
