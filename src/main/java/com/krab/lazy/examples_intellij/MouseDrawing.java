package com.krab.lazy.examples_intellij;

import com.krab.lazy.LazyGui;
import com.krab.lazy.LazyGuiSettings;
import com.krab.lazy.PickerColor;
import processing.core.PApplet;
import processing.core.PGraphics;

public class MouseDrawing extends PApplet {
    LazyGui gui;
    PGraphics canvas;
    PickerColor circleColor;
    PickerColor lineColor;
    private float lineWeight = 15;
    private float circleSize = 50;


    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    @Override
    public void settings() {
        size(800, 800, P2D);
    }

    @Override
    public void setup() {
        gui = new LazyGui(this, new LazyGuiSettings().setLoadLatestSaveOnStartup(false));
        canvas = createGraphics(width, height);
        canvas.beginDraw();
        canvas.background(50);
        canvas.endDraw();
        noStroke();
    }

    @Override
    public void draw() {
        gui.pushFolder("drawing");
        circleColor = gui.colorPicker("circle color", color(0));
        circleSize = gui.slider("circle size", circleSize);
        lineColor = gui.colorPicker("line color", color(150, 255, 100));
        gui.colorPickerHueAdd("line color", radians(gui.slider("line hue +", 0.5f)));
        lineWeight = gui.slider("line weight", lineWeight);
        gui.popFolder();
        clear();
        if(gui.button("clear canvas")){
            canvas.beginDraw();
            canvas.background(50);
            canvas.endDraw();
        }
        image(canvas, 0, 0);

        boolean mouseOutsideGui = gui.isMouseOutsideGui();
        textSize(32);
        text(mouseOutsideGui ? "The mouse is now a brush." : "The mouse is now using the GUI.", 10, height - 12);
    }

    public void mousePressed() {
        if (gui.isMouseOutsideGui()) {
            drawCircleAtMouse();
        }
    }

    public void mouseReleased() {
        if (gui.isMouseOutsideGui()) {
            drawCircleAtMouse();
        }
    }

    public void mouseDragged() {
        if (gui.isMouseOutsideGui()) {
            drawLineAtMouse();
        }
    }

    private void drawCircleAtMouse() {
        canvas.beginDraw();
        canvas.noStroke();
        canvas.fill(circleColor.hex);
        canvas.ellipse(mouseX, mouseY, circleSize, circleSize);
        canvas.endDraw();
    }

    private void drawLineAtMouse() {
        canvas.beginDraw();
        canvas.stroke(lineColor.hex);
        canvas.strokeWeight(lineWeight);
        canvas.line(pmouseX, pmouseY, mouseX, mouseY);
        canvas.endDraw();
    }
}
