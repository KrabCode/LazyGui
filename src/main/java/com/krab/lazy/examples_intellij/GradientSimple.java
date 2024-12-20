package com.krab.lazy.examples_intellij;

import com.krab.lazy.LazyGui;
import com.krab.lazy.LazyGuiSettings;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

/**
 * Created by Jakub 'Krab' Rak on 2024-08-19
 */
public class GradientSimple extends PApplet {
    private LazyGui gui;
    PGraphics pg;
    private final int fullHeight = 2340;
    private final int fullWidth = 1080;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        float scale = 0.4f;
        size(floor(fullWidth * scale), floor(fullHeight * scale), P2D);
    }

    public void setup() {
        gui = new LazyGui(this);
        pg = createGraphics(fullWidth, fullHeight);
        noStroke();
    }

    public void draw() {
        tint(gui.colorPicker("tint", color(255, 255, 255, 255)).hex);
        PImage gradient =
            gui.gradient("path",
                    gui.colorPoint(color(255,0,0), 0),
                    gui.colorPoint(color(0,255,0), 0.5f),
                    gui.colorPoint(color(0,0,255), 1)
            );
        image(gradient, 0, 0);
    }
}
