package com.krab.lazy.examples_intellij;

import com.krab.lazy.Input;
import com.krab.lazy.LazyGui;
import com.krab.lazy.LazyGuiSettings;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

public class TextEditor extends PApplet {

    LazyGui gui;
    PFont font;
    int fontSize = -1;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings(){
        size(800,600,P2D);
    }

    public void setup() {
        gui = new LazyGui(this, new LazyGuiSettings()
                .setLoadLatestSaveOnStartup(true)
        );
        lazySetFont(20);
    }

    void lazySetFont(int size) {
        if (fontSize != size) {
            fontSize = size;
            PFont candidate = createFont("JetBrainsMono-Regular.ttf", size);
            if (candidate != null) {
                font = candidate;
            }
        }
    }

    public void draw() {
        background(gui.colorPicker("background", color(10)).hex);
        gui.pushFolder("text");
        String textValue = gui.text("hello", "testing");
        fill(gui.colorPicker("text color", color(160)).hex);
        PVector pos = gui.plotXY("text pos", 500, 500);
        translate(pos.x, pos.y);
        textAlign(LEFT, TOP);
        lazySetFont(gui.sliderInt("text size", fontSize));
        textFont(font);
        text(textValue, 0, 0);
        gui.popFolder();
    }

}
