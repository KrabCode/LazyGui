package com.krab.lazy.examples_intellij;

import com.krab.lazy.LazyGui;
import processing.core.PApplet;

/**
 * Created by Jakub 'Krab' Rak on 2024-08-19
 */
public class GradientSimple extends PApplet {
    private LazyGui gui;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P2D);
    }

    public void setup() {
        gui = new LazyGui(this);
        colorMode(HSB, 360, 100, 100);
        noStroke();
    }

    public void draw() {
        image(gui.gradient("gradient"), 0, 0);
    }
}
