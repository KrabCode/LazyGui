package test;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import toolbox.Gui;


@SuppressWarnings("DuplicatedCode")
public class Lissajous extends PApplet {
    Gui gui;
    PGraphics pg;
    int recordingIndex = 0;
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
        pg = createGraphics(width, height, P2D);
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

        int count = gui.sliderInt("lissajous/count", 5000, 1, 10000);
        float maxAngle = gui.slider("lissajous/max angle", 182.1f, 0.1f);
        float xMag = gui.slider("lissajous/dist mag", 370);
        float yMag = gui.slider("lissajous/rot mag", 165);
        float xFreq = gui.slider("lissajous/dist freq", 0.0743f);
        float yFreq = gui.slider("lissajous/rot freq", 0.154f);
        float timeDelta = gui.slider("lissajous/time", 0.002f);
        float ellipseSize = gui.slider("lissajous/ellipse size", 15);
        float colorMult = 0.3f;
        //noinspection PointlessArithmeticExpression
        pg.fill(gui.colorPicker("stroke",
                (255/255f)*colorMult, (160/255f)*colorMult, (87/255f*colorMult), 0.3f).hex);
//        pg.strokeWeight(gui.slider("weight", 2, 0.1f));
        pg.noStroke();
        time += radians(timeDelta);
        if(gui.toggle("blend mode: add", true)){
            pg.blendMode(ADD);
        }else{
            pg.blendMode(BLEND);
        }
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
        clear();
        image(pg, 0, 0);
        gui.draw();
        gui.record();
    }

    @Override
    public void keyPressed() {
        if (key == 'k') {
            record = !record;
            println("recording: " + record);
        }
        if(key == 'i'){
            save("out/screenshots/" + year() + month() + day() + "_" + hour() + minute() + second() + ".jpg");
        }
    }
}
