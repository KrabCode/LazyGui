package test;

import toolbox.Gui;
import processing.core.PApplet;

public class Main extends PApplet {
    Gui gui;

    public static void main(String[] args){
        PApplet.main("test.Main");
    }

    public void settings() {
//        fullScreen(P2D);
        size(600,1000, P2D);
    }

    public void setup() {
        gui = new Gui(this);
        int margin = 20;
        surface.setLocation(displayWidth - width - margin, margin);
        surface.setAlwaysOnTop(true);

    }

    public void draw() {
        clear();
        background(0xFF36393E);
        strokeWeight(gui.slider("draw/stroke weight"));
        stroke(255);
        if(mousePressed){
            if(mouseX != pmouseX || mouseY != pmouseY){
                line(mouseX, mouseY, pmouseX, pmouseY);
            }else{
                point(mouseX, mouseY);
            }
        }
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
