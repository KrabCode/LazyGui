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
        background(gui.colorPicker("background", color(20)).hex);
        fill(gui.colorPicker("foreground", color(200)).hex);
        gui.colorPickerHueAdd("foreground", gui.slider("foreground hue +"));
        gui.slider("test/input");
        noStroke();
        rectMode(CENTER);
        rect(width/2f, height/2f, 150, 150);
    }
}
