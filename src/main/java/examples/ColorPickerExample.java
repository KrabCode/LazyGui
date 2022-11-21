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
        background(gui.colorPicker(path + "background", color(20)).hex);
        fill(gui.colorPicker(path + "rect/fill", color(200)).hex);
        gui.colorPickerHueAdd(path + "rect/fill", gui.slider(path + "rect/fill hue +"));
        stroke(gui.colorPicker(path + "rect/stroke").hex);
        strokeWeight(gui.slider(path + "rect/weight", 10));
        rectMode(CENTER);
        rect(width/2f, height/2f, 150, 150);
    }
}
