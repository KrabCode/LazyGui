package examples_intellij;

import lazy.LazyGui;
import processing.core.PApplet;
import processing.core.PVector;

public class PathHiding extends PApplet {

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
        drawText();
        drawRectangles();
    }

    private void drawText() {
        gui.pushFolder("text");
        boolean show = gui.toggle("edit mode");
        String content = gui.text("content", "lorem ipsum ");
        text(content, 200, 600);

        // You can also hide any single control element by path
        if (show) {
            gui.show("content");
        } else {
            gui.hide("content");
        }

        gui.popFolder();
    }

    void drawRectangles() {
        gui.pushFolder("rects");
        int maxRectCount = 20;
        int rectCount = gui.sliderInt("count", 10, 0, maxRectCount);

        for (int i = 0; i < maxRectCount; i++) {
            // make a dynamic list of rects each with its own folder
            gui.pushFolder("#" + i);

            if(i < rectCount){
                // show the current folder in case it was hidden
                gui.showCurrentFolder();
            }else{
                // this rect is over the rectCount limit, so we hide its folder and skip drawing it
                gui.hideCurrentFolder();
                // shouldn't forget to pop out of the folder before 'continue'
                gui.popFolder();
                continue;
            }
            PVector pos = gui.plotXY("pos", 600, 80 + i * 22);
            PVector size = gui.plotXY("size", 5);
            fill(gui.colorPicker("fill", color(200)).hex);
            noStroke();
            rect(pos.x, pos.y, size.x, size.y);
            gui.popFolder();
        }
        gui.popFolder();
    }
}
