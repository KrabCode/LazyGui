package examples;

import lazy.LazyGui;
import processing.core.PApplet;
import processing.core.PVector;

public class ColorPickerExampleError extends PApplet {
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
        gui.pushFolder("background");
        if(gui.toggle("solidly", true)){
            background(gui.colorPicker("solid color", color(0xFF252525)).hex);
        }else{
            image(gui.gradient("gradient"), 0, 0);
        }
        gui.popFolder();
        PVector pos = gui.plotXY("pos");
        translate(pos.x, pos.y);
        gui.pushFolder("rect");
        fill(gui.colorPicker("fill", color(0xFF689FC8)).hex);
        gui.colorPickerHueAdd("fill", gui.slider("fill hue +", 0.005f));
        stroke(gui.colorPicker("stroke").hex);
        strokeWeight(gui.slider("weight", 10));
        rectMode(CENTER);
        rect(width/2f, height/2f, 150, 150);
    }
}
