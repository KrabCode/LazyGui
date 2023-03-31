package com.krab.lazy.examples_intellij;

import com.krab.lazy.LazyGuiSettings;
import processing.core.PApplet;
import processing.core.PGraphics;
import com.krab.lazy.LazyGui;
import processing.core.PVector;

public class ReadmeVisualsGenerator extends PApplet {
    LazyGui gui;
    PGraphics pg;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    @Override
    public void settings() {
        size(22 * 30, 22 * 8, P2D);
    }

    @Override
    public void setup() {
        gui = new LazyGui(this, new LazyGuiSettings().setLoadLatestSaveOnStartup(false).setAutosaveOnExit(false));
        gui.toggleSet("options/windows/keep in bounds", false);
        pg = createGraphics(width, height, P2D);
        colorMode(HSB, 1, 1, 1, 1);
        frameRate(144);
    }

    @Override
    public void draw() {
        pg.beginDraw();
        {
            gui.pushFolder("background");
            background(gui.colorPicker("color picker", color(0xFF0F0F0F)).hex);
            gui.popFolder();
        }
        {
            gui.pushFolder("gradient");
            if (gui.toggle("active")) {
                pg.image(gui.gradient("gradient picker"), 0, 0, width, height);
            }
            gui.popFolder();
        }

        updateControls();
        pg.endDraw();
        image(pg, 0, 0);
        gui.draw();
    }

    private void updateControls() {
        {
            gui.pushFolder("controls");
            pg.fill(gui.colorPicker("fill", color(0xFF7F7F7F)).hex);
            pg.noStroke();
            {
                gui.pushFolder("slider");
                float x = gui.slider("x", 0);
                pg.rect(0, 0, x, height);
                gui.popFolder();
            }
            {
                gui.pushFolder("plot");
                PVector pos = gui.plotXY("pos", -width / 3f, height / 2f);
                String shape = gui.radio("shape", new String[]{
                        "rectangle", "circle", "triangle"
                });
                pg.pushMatrix();
                pg.translate(pos.x, pos.y);
                if (shape.equals("rectangle")) {
                    pg.rectMode(CENTER);
                    pg.rect(0, 0, 50, 50);
                } else if (shape.equals("circle")) {
                    pg.ellipse(0, 0, 50, 50);
                } else {
                    pg.beginShape();
                    float r = 25;
                    for (int i = 0; i < 3; i++) {
                        float theta = map(i, 0, 3, 0, TAU);
                        pg.vertex(r * cos(theta), r * sin(theta));
                    }
                    pg.endShape();
                }
                pg.popMatrix();
                gui.popFolder();
            }
            {
                if (gui.toggle("toggle/move")) {
                    gui.sliderAdd("slider/x", 1);
                }
            }
            {
                gui.pushFolder("text input");
                String text = gui.text("content", "hello");
                pg.textAlign(LEFT, TOP);
                pg.textSize(50);
                pg.text(text, 20, 10);
                gui.popFolder();
            }
            gui.popFolder();
        }
    }
}

