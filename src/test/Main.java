package test;

import processing.core.PGraphics;
import toolbox.Gui;
import processing.core.PApplet;

public class Main extends PApplet {
    Gui gui;
    PGraphics pg;

    public static void main(String[] args){
        PApplet.main("test.Main");
    }

    public void settings() {
        fullScreen(P2D);
//        size(600,1000, P2D);
    }

    public void setup() {
        gui = new Gui(this);
        int margin = 0;
        surface.setSize(1000,1000);
        surface.setLocation(displayWidth - width - margin, margin);
        surface.setAlwaysOnTop(true);
        pg = createGraphics(width, height, P2D);
    }

    public void draw() {
        pg.beginDraw();
        pg.fill(0x0F36393E);
        pg.noStroke();
        pg.rectMode(CORNER);
        pg.rect(0,0,width,height);
        pg.pushMatrix();
        pg.translate(width/2f,height/2f);
        pg.noFill();
        pg.stroke(255);
        pg.strokeWeight(3);
        pg.rotate(radians(frameCount));
        float n = 150;
        pg.rectMode(CENTER);
        pg.rect(0,0,n,n);
        pg.popMatrix();
        pg.endDraw();
        gui.update();
        clear();
        image(pg, 0, 0);
        image(gui.pg, 0, 0);
    }

    @Override
    public void keyPressed() {
        if(key == 'c'){
            clear();
        }
    }
}
