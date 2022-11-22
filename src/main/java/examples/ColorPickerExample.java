package examples;

import processing.core.PApplet;
import lazy.LazyGui;

public class ColorPickerExample extends PApplet {
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
        String path = "scene/";
        background(gui.colorPicker(path + "background", color(0xFF252525)).hex);
        path += "rect/";
        fill(gui.colorPicker(path + "fill", color(0xFF689FC8)).hex);
        gui.colorPickerHueAdd(path + "fill", gui.slider(path + "fill hue +", 0.005f));
        stroke(gui.colorPicker(path + "stroke").hex);
        strokeWeight(gui.slider(path + "weight", 10));
        rectMode(CENTER);
        rect(width/2f, height/2f, 150, 150);
    }
}
