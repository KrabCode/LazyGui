package com.krab.lazy.examples_intellij;

import processing.core.PApplet;
import com.krab.lazy.*;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class TexturePreviewExample extends PApplet {

    LazyGui gui;
    PGraphics pgInvert;
    PGraphics pgHue;
    PImage img;

    String SHADER_HUE_SHIFT_PATH = "src/main/java/com/krab/lazy/examples/TexturePreview/data/hueShift.glsl";
    PShader shaderHueShift;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(800, 800, P2D);
    }

    public void setup() {
        gui = new LazyGui(this);
        pgInvert = createGraphics(400, 400, P2D);
        pgHue = createGraphics(400, 400, P2D);
        img = loadImage("../src/main/java/com/krab/lazy/examples/TexturePreview/data/picsum_dog.jpg");
        shaderHueShift = loadShader(SHADER_HUE_SHIFT_PATH);
    }

    public void draw() {
        background(gui.colorPicker("background").hex);

        gui.image("source image", img);

        pgInvert.beginDraw();
        pgInvert.image(img, 0, 0);
        pgInvert.filter(INVERT);
        pgInvert.endDraw();
        gui.image("inverted", pgInvert);

        pgHue.beginDraw();
        pgHue.image(img, 0, 0);
        shaderHueShift.set("hueShiftAmount", frameCount * 0.01f);
        pgHue.filter(shaderHueShift);
        pgHue.endDraw();
        gui.image("hue shifted", pgHue);
    }
}

