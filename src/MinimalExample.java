import processing.core.PApplet;
import toolbox.Gui;

public class MinimalExample extends PApplet {
    Gui gui;

    public static void main(String[] args) {
        PApplet.main("MinimalExample");
    }

    // TODO some window borders look bad in P2D, probably the z coordinate
    public void settings() {
        size(1000, 1000, P2D);
    }

    public void setup() {
        gui = new Gui(this);
    }

    @SuppressWarnings("DuplicatedCode")
    public void draw() {
        background(gui.colorPicker("background").hex);
        gui.draw();
    }
}
