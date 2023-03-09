package examples_intellij;

import lazy.LazyGui;
import processing.core.PApplet;
import processing.core.PVector;

public class VisibilityTest extends PApplet {

    LazyGui gui;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1200, 800, P2D);
        smooth(8);
    }

    public void setup() {
        gui = new LazyGui(this);

        // hide the gui options if you don't need to see them
        gui.hide("options");

        // change the gui options by setting their values from code
        gui.toggleSet("options/saves/autosave on exit", false);

        textSize(64);
    }

    public void draw() {
        background(gui.colorPicker("background", color(50)).hex);
        drawRects();
    }

    void drawRects() {
        gui.pushFolder("rects");
        int maxRectCount = 20;
        int rectCount = gui.sliderInt("count", 10, 0, maxRectCount);
        String dynamicFolderPrefix = "#";

        for (int i = 0; i < rectCount; i++) {
            // make a dynamic list of rects each with its own folder
            gui.pushFolder(dynamicFolderPrefix + i);
            PVector pos = gui.plotXY("pos", 200 + i * 10);
            PVector size = gui.plotXY("size", 5);
            fill(gui.colorPicker("fill", color(200)).hex);
            noStroke();
            rect(pos.x, pos.y, size.x, size.y);

            gui.showCurrentFolder(); // show the current folder in case it was hidden by lower rectCount but then the count went back up again
            gui.popFolder();
        }

        // hide any unused folders when the count gets lowered
        for (int i = rectCount; i < maxRectCount; i++) {
            gui.pushFolder(dynamicFolderPrefix + i);
            gui.hideCurrentFolder();
            gui.popFolder();
        }

        // OR hide any unused folders without going into them with pushFolder()
        for (int i = rectCount; i < maxRectCount; i++) {
            gui.hide(dynamicFolderPrefix + i);
        }
    }

}
