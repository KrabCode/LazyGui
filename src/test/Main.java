package test;

import toolbox.Gui;
import processing.core.PApplet;

public class Main extends PApplet {
    Gui gui;

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

    }

    public void draw() {
        clear();
        background(0xFF36393E);
        pushMatrix();
        translate(width/2f,height/2f);
        noFill();
        stroke(150);
        rotate(radians(frameCount));
        float n = 150;
        rectMode(CENTER);
        rect(0,0,n,n);
        popMatrix();
        gui.update();
        image(gui.pg, 0, 0);
    }

    @Override
    public void keyPressed() {
        if(key == 'c'){
            clear();
        }
    }
}
