package com.krab.lazy.examples_intellij;

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
        gui.pushFolder("grid");
        background(gui.colorPicker("background", color(36)).hex);
        stroke(gui.colorPicker("foreground", color(100)).hex);
        strokeWeight(gui.slider("weight", 3));
        float step = gui.slider("step", 50);
        PVector size = gui.plotXY("size", width*1.2f, height*1.2f);
        PVector offset = gui.plotXY("offset");
        for (float x = -size.x + offset.x; x < size.x; x+= step) {
            for (float y = -size.y + offset.y; y < size.y; y+= step) {
                point(x, y);
            }
        }
        gui.popFolder();

        String longText = "Among those who devote themselves to the transmutation of metals,"+
                "\nhowever, there can be no such thing as mediocrity of attainment."+
                "\nA person who studies this Art, must have either everything or nothing."+
                "\nAn Alchemist who knows only half their craft, reaps nothing but disappointment"+
                "\nand waste of time and money; moreover, they lay themselves open to the mockery"+
                "\nof those who despise our Art. Those, indeed, who succeed in reaching the goal"+
                "\nof the Magistery, have not only infinite riches, but the means of continued"+
                "\nlife and health. Hence it is the most popular of all pursuits.";
        gui.pushFolder("rect");
        PVector rectPos = gui.plotXY("rect pos", 250);
        PVector rectSize = gui.plotXYZ("rect size", 150);
        fill(gui.colorPicker("rect fill", 0).hex);
        stroke(gui.colorPicker("rect stroke", 100).hex);
        strokeWeight(gui.slider("rect weight", 3));
        float cornerRadius = gui.slider("corner radius");
        rotate(radians(gui.slider(longText)));
        rect(rectPos.x, rectPos.y, rectSize.x, rectSize.y, cornerRadius);
        gui.popFolder();
        gui.pushFolder("text");
        String textValue = gui.text(longText);
        fill(gui.colorPicker("text color", color(160)).hex);
        PVector pos = gui.plotXY("text pos");
        translate(pos.x, pos.y);
        textAlign(LEFT, TOP);
        lazySetFont(gui.sliderInt("text size", fontSize));
        textFont(font);
        text(textValue, 0, 0);
        gui.popFolder();
        gui.popFolder();
    }

}
