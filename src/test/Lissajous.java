package test;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import toolbox.Gui;


@SuppressWarnings("DuplicatedCode")
public class Lissajous extends PApplet {
    Gui gui;
    PGraphics pg;
    private boolean record = false;

    public static void main(String[] args) {
        PApplet.main("test.Lissajous");
    }

    public void settings() {
        fullScreen(P2D);
        smooth(8);
//        size(600,1000, P2D);
    }

    public void setup() {
        gui = new Gui(this);
        int margin = 0;
//        surface.setSize(800, 800);
//        surface.setLocation(displayWidth - width - margin, margin);
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

    int i = 0;
    float time = 0;

    public void draw() {
        pg.beginDraw();
        pg.fill(0xFF36393E);
        pg.noStroke();
        pg.rectMode(CORNER);
        pg.rect(0, 0, width, height);
        pg.pushMatrix();
        pg.translate(width / 2f, height / 2f);
        pg.noFill();
        boolean recordMode = gui.toggle("record", false);

        pg.stroke(gui.sliderIntConstrained("stroke", 255, 0, 255));
        pg.strokeWeight(gui.slider("weight", 2, 0.1f));

        int count = gui.sliderIntConstrained("lissajous/count", 800, 1, 1000);
        float maxAngle = gui.slider("lissajous/max angle", 10, 0.1f);
        float xMag = gui.slider("lissajous/x mag", 300);
        float yMag = gui.slider("lissajous/y mag", 300);
        float xFreq = gui.slider("lissajous/x freq", TAU);
        float yFreq = gui.slider("lissajous/y freq", TAU);
        float timeDelta = gui.slider("lissajous/time", 1);
        time += radians(timeDelta);
        for (int i = 0; i < count; i++) {
            float a = map(i, 0, count, 0, maxAngle);
            pg.pushMatrix();
            pg.translate(xMag * cos(a * xFreq + time), yMag * sin(a * yFreq + time));
            pg.point(0, 0);
            pg.popMatrix();
        }
        pg.popMatrix();
        pg.endDraw();
        gui.update();
        clear();
        image(pg, 0, 0);
        image(gui.pg, 0, 0);

        if (recordMode) {
            noCursor();
            if (mousePressed) {
                stroke(255, 0, 0);
            } else {
                stroke(255);
            }
            strokeWeight(10);
            point(mouseX, mouseY);
            if (record) {
                save("out/1/" + ++i + ".jpg");
            }
        } else {
            cursor();
        }

    }

    @Override
    public void keyPressed() {
        if (key == 'k') {
            record = !record;
            println("recording: " + record);
        }
    }
}
