package com.krab.lazy.examples_intellij;

import com.krab.lazy.LazyGui;
import com.krab.lazy.LazyGuiSettings;
import processing.core.PApplet;
import processing.core.PVector;

public class Demo extends PApplet {
    LazyGui gui;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    @Override
    public void settings() {
        fullScreen(P3D);
        smooth(8);
    }

    @Override
    public void setup() {
        gui = new LazyGui(this, new LazyGuiSettings()
//                .setLoadLatestSaveOnStartup(false)
//                .setAutosaveOnExit(false)
        );
    }

    @Override
    public void draw() {
        gui.pushFolder("sketch");
        background(gui.colorPicker("background").hex);
        gui.gradient("z colors");
        float gridSizeHalf = gui.slider("grid size", 1000) / 2f;
        float gridDetail = gui.sliderInt("grid detail", 50);
        float cellSize = gui.slider("cell size", gridSizeHalf / (gridDetail / 2f) + 0.5f);
        float freq = gui.slider("frequency", 0.1f);
        stroke(gui.colorPicker("stroke").hex);
        strokeWeight(gui.slider("stroke weight"));
        PVector rot = gui.plotXY("rotate xz");
        PVector noisePos = gui.plotXY("noise pos");
        PVector noiseSpeed = gui.plotXY("noise speed");
        gui.plotSet("noise pos", noisePos.copy().add(noiseSpeed.div(100f)));
        for (int xi = 0; xi < gridDetail; xi++) {
            for (int yi = 0; yi < gridDetail; yi++) {
                float xNorm = norm(xi, 0, gridDetail - 1);
                float yNorm = norm(yi, 0, gridDetail - 1);
                float x = -gridSizeHalf + xNorm * 2 * gridSizeHalf;
                float y = -gridSizeHalf + yNorm * 2 * gridSizeHalf;
                float noise = noise((noisePos.x + x) * freq, (noisePos.y + y) * freq);
                int clr = gui.gradientColorAt("z colors", 1 - noise).hex;
                float baseHeight = gui.slider("base height", 200);
                float noiseHeight = noise * gui.slider("noise ratio", 100);
                float h = baseHeight + noiseHeight;
                fill(clr);
                pushMatrix();
                translate(width / 2f, height / 2f);
                rotateX(rot.x * PI);
                rotateZ(rot.y * PI);
                rectMode(CENTER);
                rect(x, y, cellSize, cellSize);
                translate(x, y, h / 2f);
                box(cellSize, cellSize, -h);
                popMatrix();
            }
        }
        gui.popFolder();
    }
}
