package com.krab.lazy.examples_intellij;

import com.krab.lazy.Input;
import com.krab.lazy.LazyGui;
import com.krab.lazy.LazyGuiSettings;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

import java.util.HashMap;

public class TextEditor extends PApplet {
    LazyGui gui;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings(){
        size(800,600,P2D);
    }

    public void setup() {
        gui = new LazyGui(this, new LazyGuiSettings()
                .setLoadLatestSaveOnStartup(false)
        );
    }

    public void draw() {
        background(gui.colorPicker("background", color(10)).hex);
        gui.pushFolder("text");
        String textValue = gui.text("hello", "lorem\nipsum\ndolor\nsit\namet");
        PVector pos = gui.plotXY("text pos", 400, 300);
        translate(pos.x, pos.y);
        font();
        text(textValue, 0, 0);
        gui.popFolder();
    }

    // font() related fields
    HashMap<String, PFont> fontCache = new HashMap<String, PFont>();
    HashMap<String, Integer> xAligns;
    HashMap<String, Integer> yAligns;

    // Select from lazily created, cached fonts.
    void font() {
        gui.pushFolder("font");
        fill(gui.colorPicker("fill", color(255)).hex);
        int size = gui.sliderInt("size", 64, 1, 256);
        float leading = gui.slider("leading", 64);
        if (xAligns == null || yAligns == null) {
            xAligns = new HashMap<String, Integer>();
            xAligns.put("left", LEFT);
            xAligns.put("center", CENTER);
            xAligns.put("right", RIGHT);
            yAligns = new HashMap<String, Integer>();
            yAligns.put("top", TOP);
            yAligns.put("center", CENTER);
            yAligns.put("bottom", BOTTOM);
        }
        String xAlignSelection = gui.radio("align x", xAligns.keySet().toArray(new String[0]), "center");
        String yAlignSelection = gui.radio("align y", yAligns.keySet().toArray(new String[0]), "center");
        textAlign(xAligns.get(xAlignSelection), yAligns.get(yAlignSelection));
        String fontName = gui.text("font name", "Arial").trim();
        if (gui.button("list fonts")) {
            String[] fonts = PFont.list();
            for (String font : fonts) {
                println(font + "                 "); // some spaces to avoid copying newlines from the console
            }
        }
        String fontKey = fontName + " | size: " + size;
        if (!fontCache.containsKey(fontKey)) {
            PFont loadedFont = createFont(fontName, size);
            fontCache.put(fontKey, loadedFont);
            println("Loaded font: " + fontKey);
        }
        PFont cachedFont = fontCache.get(fontKey);
        textFont(cachedFont);
        textLeading(leading);
        gui.popFolder();
    }
}
