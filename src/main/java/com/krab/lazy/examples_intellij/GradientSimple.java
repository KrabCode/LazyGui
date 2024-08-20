package com.krab.lazy.examples_intellij;

import com.krab.lazy.LazyGui;
import com.krab.lazy.LazyGuiSettings;
import processing.core.PApplet;
import processing.core.PGraphics;

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
        gui = new LazyGui(this,
                new LazyGuiSettings());
        pg = createGraphics(fullWidth, fullHeight);
        noStroke();
    }

    public void draw() {
        tint(gui.colorPicker("tint", color(255, 255, 255, 255)).hex);
        image(gui.gradient("gradient"), 0, 0);
    }
}
