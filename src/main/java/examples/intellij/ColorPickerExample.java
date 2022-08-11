package examples.intellij;

import processing.core.PApplet;
import toolbox.Gui;

public class ColorPickerExample extends PApplet {
    Gui gui;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P2D);
    }

    public void setup() {
        gui = new Gui(this);
    }

    @SuppressWarnings("DuplicatedCode")
    public void draw() {
        background(gui.colorPicker("background", color(20)).hex);
        fill(gui.colorPicker("foreground", color(200)).hex);
        noStroke();
        rect(width/2f, height/2f, 50, 50);
    }
}
