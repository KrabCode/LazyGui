package com.krab.lazy.examples_intellij;

import com.krab.lazy.LazyGui;
import com.krab.lazy.PickerColor;
import processing.core.PApplet;
import processing.core.PGraphics;

public class MouseDrawing extends PApplet {
    LazyGui gui;
    PGraphics canvas;
    PickerColor circleColor;
    PickerColor lineColor;
    private float lineWeight = 2;
    private float circleSize = 30;


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
        canvas = createGraphics(width, height, P2D);
        for (int i = 0; i < 2; i++) {
            // there's a bug in PGraphics where you have to draw the background twice for it to stick
            canvas.beginDraw();
            canvas.background(150);
            canvas.endDraw();
        }
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
        image(canvas, 0, 0);
    }

    public void mousePressed() {
        drawCircleAtMouse();
    }

    public void mouseReleased() {
        drawCircleAtMouse();
    }

    public void mouseDragged() {
        drawLineAtMouse();
    }

    private void drawCircleAtMouse() {
        canvas.beginDraw();
        if(gui.mousePressedOutsideGui()){
            canvas.noStroke();
            canvas.fill(circleColor.hex);
            canvas.ellipse(mouseX, mouseY, circleSize, circleSize);
        }
        canvas.endDraw();
    }

    private void drawLineAtMouse(){
        canvas.beginDraw();
        if(gui.mousePressedOutsideGui()){
            canvas.stroke(lineColor.hex);
            canvas.strokeWeight(lineWeight);
            canvas.line(pmouseX, pmouseY, mouseX, mouseY);
        }
        canvas.endDraw();
    }
}
