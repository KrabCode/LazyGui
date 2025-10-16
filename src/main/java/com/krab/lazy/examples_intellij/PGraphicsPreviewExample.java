package com.krab.lazy.examples_intellij;

import com.krab.lazy.LazyGui;
import com.krab.lazy.LazyGuiSettings;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

/**
 * Simple example showing the main sketch canvas and a PGraphics overlay.
 * - main canvas displays "main canvas"
 * - a separate PGraphics contains a rectangle and the text "PGraphics overlay"
 * - overlay position is controlled via gui.plotXY
 * - both the main canvas (g) and the overlay PGraphics are shown in the GUI using gui.image()
 */
public class PGraphicsPreviewExample extends PApplet {
    LazyGui gui;
    PGraphics overlay;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(800, 600, P2D);
        smooth(4);
    }

    public void setup() {
        gui = new LazyGui(this, new LazyGuiSettings());
        // create a smaller overlay PGraphics to draw on
        overlay = createGraphics(240, 140, P2D);
        overlay.smooth(4);
    }

    public void draw() {
        // draw main canvas background and label
        background(40, 45, 50);
        fill(255);
        textAlign(CENTER, CENTER);
        textSize(28);
        text("main canvas", width * 0.5f, height * 0.5f);

        gui.image("null", null);

        // get() creates a snapshot at this time but calling it often may be expensive
        gui.image("canvas", get());

        // draw something into the PGraphics overlay
        overlay.beginDraw();
        overlay.clear();
        overlay.noStroke();
        overlay.fill(200, 80, 80, 220);
        overlay.rect(0, 0, overlay.width, overlay.height, 8);
        overlay.fill(255);
        overlay.textAlign(CENTER, CENTER);
        overlay.textSize(14);
        overlay.text("PGraphics overlay", overlay.width * 0.5f, overlay.height * 0.5f);
        overlay.endDraw();

        // show the overlay PGraphics as another preview in the GUI
        gui.image("overlay", overlay); // show overlay PGraphics (copied internally)

        // get overlay position from the GUI
        PVector pos = gui.plotXY("position", new PVector(510, 350));
        // display the overlay
        imageMode(CENTER);
        image(overlay, pos.x, pos.y);

    }
}

