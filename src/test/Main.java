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
        surface.setAlwaysOnTop(true);
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

    public void draw() {
        pg.beginDraw();
        pg.fill(0xFF36393E);
        pg.noStroke();
        pg.rectMode(CORNER);
        pg.rect(0, 0, width, height);
        pg.pushMatrix();
        pg.translate(width / 2f, height / 2f);
        pg.noFill();
        pg.stroke(255);
        pg.strokeWeight(3);
        pg.rotate(radians(frameCount));
        float n = 150;
        pg.rectMode(CENTER);
        for (int i = 0; i < 8; i++) {
            float a = PApplet.map(i,0, 8, 0, HALF_PI);
            pg.pushMatrix();
            pg.rotate(a);
            pg.rect(0, 0, n, n);
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
