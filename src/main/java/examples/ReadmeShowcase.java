package examples;

import lazy.LazyGui;
import processing.core.PApplet;
import processing.core.PGraphics;

public class ReadmeShowcase extends PApplet {

    LazyGui gui;

    public static void main(String[] args) {
        PApplet.main("examples.ReadmeShowcase");
    }

    public void settings() {
        size(800,800,P2D);
    }

    public void setup() {
        gui = new LazyGui(this);
    }

    public void draw() {
        background(gui.colorPicker("background").hex);
        gui.colorPicker("color name");
        PGraphics gradient = gui.gradient("gradient name");

    }
}
