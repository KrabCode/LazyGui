package com.krab.lazy.examples_intellij;

import com.krab.lazy.LazyGui;
import com.krab.lazy.LazyGuiSettings;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

/**
 * Shows off the vertical window scrollbar.
 * The GUI windows are capped at 300 pixels of height with setMaxWindowHeight(),
 * and this sketch creates far more control rows than fit into that height,
 * so the root window immediately shows a scrollbar on its right edge.
 * Scroll by hovering over the scrollbar and rolling the mouse wheel,
 * or by click-and-dragging the scrollbar thumb.
 * You can also open the "dots" folder as a window to see its own scrollbar,
 * or change the height limit live inside the GUI at "options/windows/max window height".
 */
public class WindowScrollbar extends PApplet {
    LazyGui gui;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    @Override
    public void settings() {
        size(800, 800, P2D);
    }

    @Override
    public void setup() {
        gui = new LazyGui(this, new LazyGuiSettings().setMaxWindowHeight(300));
        colorMode(HSB, 1, 1, 1, 1);
    }

    @Override
    public void draw() {
        background(gui.colorPicker("background", color(0.1f)).hex);
        List<Float> speeds = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            speeds.add(gui.slider("dot " + i + " speed", i / 30f));
        }
        gui.pushFolder("dots");
        float dotSize = gui.slider("dot size", 30, 4, 100);
        float hueShift = gui.slider("hue shift", 0, 0, 1);
        float ySpread = gui.slider("y spread", 1);
        gui.popFolder();

        noStroke();
        for (int i = 0; i < speeds.size(); i++) {
            float hue = (i / (float) speeds.size() + hueShift) % 1f;
            fill(hue, 0.8f, 0.9f);
            float x = (frameCount * speeds.get(i) * 4) % width;
            float y = height * (0.1f + 0.8f * (i / (float) speeds.size())) * lerp(0.5f, 1f, ySpread);
            ellipse(x, y, dotSize, dotSize);
        }
    }
}
