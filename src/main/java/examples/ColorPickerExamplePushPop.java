package examples;

import lazy.LazyGui;
import processing.core.PApplet;

public class ColorPickerExamplePushPop extends PApplet {
    LazyGui gui;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P2D);
    }

    public void setup() {
        gui = new LazyGui(this);
    }

    @SuppressWarnings("DuplicatedCode")
    public void draw() {
        gui.pushFolder("scene");
            background(gui.colorPicker("background", color(0xFF252525)).hex);
            gui.pushFolder("rect");
                fill(gui.colorPicker("fill", color(0xFF689FC8)).hex);
                gui.colorPickerHueAdd("fill", gui.slider("fill hue +", 0.005f));
                stroke(gui.colorPicker("stroke").hex);
                strokeWeight(gui.slider("weight", 10));
        rectMode(CENTER);
        rect(width/2f, height/2f, 150, 150);
    }
}
