package examples;

import lazy.LazyGui;
import processing.core.PApplet;
import processing.core.PGraphics;

@SuppressWarnings("IntegerDivisionInFloatingPointContext")
public class TutorialGenerator extends PApplet {
    LazyGui gui;
    PGraphics pg;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000,1000,P2D);
        smooth(16);
    }

    public void setup() {
        gui = new LazyGui(this);
        pg = createGraphics(width, height, P2D);
    }

    public void draw() {
        background(gui.colorPicker("background").hex);
        pg.beginDraw();
        drawScene();
        pg.endDraw();
        image(pg, 0, 0, width, height);
    }

    private void drawScene() {
        float x = gui.slider("my circle/x");
        pg.clear();
        pg.fill(gui.colorPicker("circle/fill").hex);
        pg.stroke(gui.colorPicker("circle/stroke").hex);
        pg.strokeWeight(gui.slider("circle/weight", 3));
        pg.ellipse(x, height/2, 50, 50);
    }

    public void keyPressed() {
        if(key == 's'){
            pg.beginDraw();
            drawScene();
            pg.image(gui.getGuiCanvas(), 0, 0);
            pg.endDraw();
            pg.save("C:\\Projects\\LazyGui\\readme_assets\\slider.png");
        }
    }
}
