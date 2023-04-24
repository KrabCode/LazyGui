package com.krab.lazy.examples_intellij;

import com.krab.lazy.Input;
import com.krab.lazy.LazyGui;
import processing.core.PApplet;
import processing.core.PGraphics;

public class InputWatcherTest extends PApplet {
    LazyGui gui;
    PGraphics pg;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    @Override
    public void settings() {
        size(800, 800, P2D);
    }

    @Override
    public void setup() {
        gui = new LazyGui(this);
        pg = createGraphics(width, height, P2D);
    }

    @Override
    public void draw() {
        pg.beginDraw();
        drawBackground();
        Input.debug(gui.toggle("debug input"));
        char[] interestingKeys = "wsad".toCharArray();
        StringBuilder textToDisplay = new StringBuilder();
        for(char c : interestingKeys){
            if(Input.getKey(c).down){
                textToDisplay.append(c);
            }
        }
        if(Input.getKey('w').press){
            println("w pressed");
        }
        if(Input.getKey('w').release){
            println("w released");
        }

        pg.fill(0xffffffff);
        pg.textFont(gui.getMainFont());
        pg.textAlign(CENTER, CENTER);
        pg.text(textToDisplay.toString(), width/2f, height/2f);
        pg.endDraw();
        image(pg, 0, 0);
    }

    private void drawBackground() {
        pg.fill(gui.colorPicker("background").hex);
        pg.noStroke();
        pg.rectMode(CORNER);
        pg.rect(0, 0, width, height);
    }
}
