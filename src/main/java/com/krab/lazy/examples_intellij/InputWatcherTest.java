package com.krab.lazy.examples_intellij;

import com.krab.lazy.Input;
import com.krab.lazy.LazyGui;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.List;

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
        Input.debugPrintKeyEvents(gui.toggle("debug keys"));
        pg.beginDraw();
        pg.colorMode(HSB,1,1,1,1);
        drawBackground();
        drawTexts();
        detectCtrlSpacePress();
        pg.endDraw();
        image(pg, 0, 0);
    }

    private void detectCtrlSpacePress() {
        boolean isControlDown = Input.getCode(CONTROL).down;
        boolean spaceWasJustPressed = Input.getChar(' ').pressed;
        if(isControlDown && spaceWasJustPressed){
            println("ctrl + space pressed");
        }
    }

    private void drawTexts() {
        pg.fill(0.75f);
        pg.textFont(gui.getMainFont());
        pg.textAlign(LEFT, BOTTOM);
        List<String> downChars = Input.getAllDownChars();
        List<Integer> downCodes = Input.getAllDownCodes();
        String textContent = "chars: " + downChars + "\ncodes: " + downCodes;
        pg.text(textContent, 10, height-10);
    }

    private void drawBackground() {
        pg.fill(gui.colorPicker("background", 0xFF0F0F0F).hex);
        pg.noStroke();
        pg.rectMode(CORNER);
        pg.rect(0, 0, width, height);
    }
}
