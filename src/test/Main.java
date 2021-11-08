package test;

import toolbox.Gui;
import processing.core.PApplet;

public class Main extends PApplet {
    Gui gui;

    public static void main(String[] args){
        PApplet.main("test.Main");
    }

    public void settings() {
        size(600,600, P2D);
    }

    public void setup() {
        gui = new Gui(this);
        background(0);
        int margin = 20;
        surface.setLocation(displayWidth - width - margin, margin);
        surface.setAlwaysOnTop(true);
    }

    public void draw() {
//        background(0xFF36393E);
        strokeWeight(3);
        stroke(255);
        if(mousePressed){
            if(mouseX != pmouseX || mouseY != pmouseY){
                line(mouseX, mouseY, pmouseX, pmouseY);
            }else{
                point(mouseX, mouseY);
            }
        }
        gui.update();
        image(gui.display(), 0, 0);
    }

    @Override
    public void keyPressed() {
        if(key == 'c'){
            clear();
        }
    }
}
