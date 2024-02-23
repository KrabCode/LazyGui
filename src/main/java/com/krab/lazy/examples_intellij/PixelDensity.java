package com.krab.lazy.examples_intellij;

import processing.core.PApplet;
import com.krab.lazy.LazyGui;

public class PixelDensity extends PApplet {
    LazyGui gui;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    @Override
    public void settings() {
        size(1080, 1080, P2D);
        pixelDensity(2); // see issue #291: https://github.com/KrabCode/LazyGui/issues/291
    }

    @Override
    public void setup() {
        gui = new LazyGui(this);
        gui.sliderIntSet("options/windows/cell size", 44);
        gui.sliderIntSet("options/font/main font size", 32);
        gui.sliderIntSet("options/font/side font size", 28);
        gui.sliderIntSet("options/font/y offset", 25);

        colorMode(HSB, 1, 1, 1, 1);
    }

    @Override
    public void draw() {
        drawBackground();
    }

    private void drawBackground() {
        fill(gui.colorPicker("background").hex);
        noStroke();
        rectMode(CORNER);
        rect(0, 0, width, height);
    }
}

