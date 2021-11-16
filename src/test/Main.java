package test;

import processing.core.PFont;
import processing.core.PGraphics;
import toolbox.Gui;
import processing.core.PApplet;

public class Main extends PApplet {
    Gui gui;
    PGraphics pg;

    public static void main(String[] args) {
        PApplet.main("test.Main");
    }

    public void settings() {
        fullScreen(P2D);
        smooth(8);
//        size(600,1000, P2D);
    }

    public void setup() {
        gui = new Gui(this);
        int margin = 0;
        surface.setSize(1000, 1000);
        surface.setLocation(displayWidth - width - margin, margin);
//        surface.setAlwaysOnTop(true);
        pg = createGraphics(width, height, P2D);
//        printAvailableFonts();
    }

    private void printAvailableFonts() {
        String[] fontList = PFont.list();
        for (String s :
                fontList) {
            println(s);
        }
    }

    float rotation = 0;
    public void draw() {
        pg.beginDraw();
        pg.fill(0xFF36393E);
        pg.noStroke();
        pg.rectMode(CORNER);
        pg.rect(0, 0, width, height);
        pg.pushMatrix();
        pg.translate(width / 2f, height / 2f);
        pg.noFill();
        if(gui.button("/print")){
            println("button pressed!");
        }
        float rotate = gui.slider("/rotation", 0, 0.01f);
        if(gui.toggle("/print rotation", true)){

            println(rotate);
        }
        rotation += radians(rotate);
        pg.stroke(gui.sliderInt("/stroke", 255, 0, 255, true));
        pg.strokeWeight(gui.slider("/weight", 2, 0.3f));
        pg.rotate(rotation);
        float size = gui.sliderConstrained("/size", 150, 0, width);
        pg.rectMode(CENTER);
        int count = gui.sliderInt("/count", 8, 1, 100, true);
        for (int i = 0; i < count; i++) {
            float a = PApplet.map(i,0, count, 0, HALF_PI);
            pg.pushMatrix();
            pg.rotate(a);
            pg.rect(0, 0, size, size);
            pg.popMatrix();
        }
        pg.popMatrix();
        pg.endDraw();
        gui.update();
        clear();
        image(pg, 0, 0);
        image(gui.pg, 0, 0);
    }

    @Override
    public void keyPressed() {
        if (key == 'c') {
            clear();
        }
    }
}
