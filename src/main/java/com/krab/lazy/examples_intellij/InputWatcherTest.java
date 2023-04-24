package com.krab.lazy.examples_intellij;

import com.krab.lazy.Input;
import com.krab.lazy.LazyGui;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.HashMap;
import java.util.Map;

public class InputWatcherTest extends PApplet {
    LazyGui gui;
    PGraphics pg;
    Map<Integer, String> codeDisplayMap;
    char[] interestingKeys = "wsad".toCharArray();
    int[] interestingCodes = new int[]{UP, DOWN, LEFT, RIGHT};

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
        codeDisplayMap = new HashMap<Integer, String>();
        codeDisplayMap.put(UP, "^");
        codeDisplayMap.put(DOWN, "v");
        codeDisplayMap.put(LEFT, "<");
        codeDisplayMap.put(RIGHT, ">");
    }

    @Override
    public void draw() {
        pg.beginDraw();
        pg.colorMode(HSB,1,1,1,1);
        drawBackground();
        Input.debugPrintKeyEvents(gui.toggle("debug keys"));
        StringBuilder textToDisplay = new StringBuilder("hello");
        for(char c : interestingKeys){
            if(Input.getKey(c).down){
                textToDisplay.append(c);
            }
        }
        for(int code : interestingCodes){
            if(Input.getKey(code).down){
                textToDisplay.append(codeDisplayMap.get(code));
            }
        }
        if(Input.getKey(' ').pressed){
            println("space pressed");
        }
        if(Input.getKey(' ').released){
            println("space released");
        }
        pg.fill(1);
        pg.textFont(gui.getMainFont());
        pg.textAlign(CENTER, CENTER);
        pg.text(textToDisplay.toString(), width/2f, height/2f);
        pg.stroke(1);
        pg.strokeWeight(5);
        PVector mousePos = Input.mousePos();
        PVector mouseDelta = Input.mouseDelta().mult(5);
        pg.translate(mousePos.x, mousePos.y);
        pg.line(0,0, mouseDelta.x, mouseDelta.y);
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
