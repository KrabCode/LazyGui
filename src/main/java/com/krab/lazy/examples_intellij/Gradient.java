package com.krab.lazy.examples_intellij;

import com.krab.lazy.LazyGui;
import com.krab.lazy.PickerColor;
import processing.core.PApplet;

public class Gradient extends PApplet {

    int defaultBackgroundColor = 0xFF0F0F0F;
    LazyGui gui;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1144, 880, P2D);
        smooth(8);
    }

    public void setup() {
        colorMode(HSB, 1, 1, 1, 1);
        background(defaultBackgroundColor);
        gui = new LazyGui(this);
    }

    public void draw() {
        if(gui.hasChanged("circles")){
            println("circles changed");
            background(defaultBackgroundColor);
        }
        fadeToBlack();
        drawNewCircles();
    }

    void drawNewCircles() {
        gui.pushFolder("circles");
        gui.gradient("fill", new int[]{
                //specify default colors for this gradient
                unhex("FFF6E1C3"),
                unhex("FFE9A178"),
                unhex("FFA84448"),
                unhex("FF7A3E65")
        });
        float circleSize = 10;
        for (int i = 0; i < gui.sliderInt("new per frame", 50); i++) {
            float size = abs(randomGaussian()) * circleSize;
            float xPosition = random(width);
            float yPosition = random(height);
            float yPositionNormalized = norm(yPosition, 0, height);
            // get the color at this position between 0 and 1
            PickerColor gradientColor = gui.gradientColorAt("fill", yPositionNormalized);
            fill(gradientColor.hex);
            ellipse(xPosition, yPosition, size, size);
        }
        gui.popFolder();
    }

    void fadeToBlack() {
        // fadeToBlack by subtracting white at low alpha
        blendMode(SUBTRACT);
        float fadeAlpha = gui.sliderInt("fade to background", 1, 0, 255) / (float) 255;
        fill(1, fadeAlpha);
        noStroke();
        rect(0, 0, width, height);

        // enforce a minimum brightness
        blendMode(LIGHTEST);
        fill(gui.colorPicker("background", defaultBackgroundColor).hex);
        rect(0, 0, width, height);

        // reset blend mode to default
        blendMode(BLEND);
    }

}
