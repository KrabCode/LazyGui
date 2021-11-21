package test;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import toolbox.Gui;


@SuppressWarnings("DuplicatedCode")
public class Lissajous extends PApplet {
    Gui gui;
    PGraphics pg;
    int i = 0;
    float time = 0;

    private boolean record = false;

    public static void main(String[] args) {
        PApplet.main("test.Lissajous");
    }

    public void settings() {
        fullScreen(P2D);
//        smooth(8);
    }

    public void setup() {
        gui = new Gui(this);
        int margin = 0;
        surface.setSize(1000,1000);
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
        pg.blendMode(BLEND);
        pg.fill(0xFF36393E);
        pg.noStroke();
        pg.rectMode(CORNER);
        pg.rect(0, 0, width, height);
        pg.pushMatrix();
        pg.translate(width / 2f, height / 2f);
        pg.noFill();

        boolean recordMode = gui.toggle("record/record mode", false);
        boolean customCursor = gui.toggle("record/custom cursor", false);
        int folderNumber = gui.sliderInt("record/number", 1);
        int count = gui.sliderIntConstrained("lissajous/count", 800, 1, 10000);
        float maxAngle = gui.slider("lissajous/max angle", 10, 0.1f);
        float xMag = gui.slider("lissajous/dist mag", 300);
        float yMag = gui.slider("lissajous/rot mag", TAU);
        float xFreq = gui.slider("lissajous/dist freq", TAU);
        float yFreq = gui.slider("lissajous/rot freq", TAU);
        float timeDelta = gui.slider("lissajous/time", 0.03f);
        pg.fill(gui.colorPicker("stroke", 0.8f, 0.2f, 0.5f).hex);
//        pg.strokeWeight(gui.slider("weight", 2, 0.1f));
        pg.noStroke();
        time += radians(timeDelta);
        if(gui.toggle("blend mode: add")){
            pg.blendMode(ADD);
        }else{
            pg.blendMode(BLEND);
        }
        float ellipseSize = gui.slider("lissajous/ellipse size", 30);
        for (int i = 0; i < count; i++) {
            float a = map(i, 0, count, 0, maxAngle);
            pg.pushMatrix();
            float x = xMag * cos(a * xFreq + time);
            float y = yMag * sin(a * yFreq + time);
            pg.rotate(y);
            pg.translate(x,0);
            pg.ellipse(0, 0, ellipseSize, ellipseSize);
            pg.popMatrix();
        }
        pg.popMatrix();
        pg.endDraw();
        gui.update();
        clear();
        image(pg, 0, 0);
        image(gui.pg, 0, 0);

        if(customCursor){
            noCursor();
            pushMatrix();
            stroke(0);
            strokeWeight(1);
            if (mousePressed) {
                fill(255, 0, 0);
            } else {
                fill(255);
            }
            translate(mouseX, mouseY);
            beginShape();
            float size = 8;
            vertex(0,0);
            vertex(2*size,2*size);
            vertex(1*size, 2*size);
            vertex(0, 3*size);
            endShape(CLOSE);
            fill(255);
            popMatrix();
        }else {
            cursor();
        }
        if (recordMode) {
            if (record) {
                save("out/" + folderNumber + "/" + ++i + ".jpg");
            }
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
