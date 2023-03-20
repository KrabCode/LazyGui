 package com.krab.lazy.examples_intellij;

import com.krab.lazy.LazyGuiSettings;
import com.krab.lazy.stores.FontStore;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import com.krab.lazy.LazyGui;
import processing.core.PVector;

public class Controls extends PApplet {
    LazyGui gui;
    PGraphics pg;
    PFont segoeUIBlack;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    @Override
    public void settings() {
        size(ceil(1200/22f)*22,ceil(500/22f)*22, P2D);
    }

    @Override
    public void setup() {
        gui = new LazyGui(this, new LazyGuiSettings()
                .setLoadLatestSaveOnStartup(false)
//                .setLoadSpecificSaveOnStartup("1")
        );
        pg = createGraphics(width, height, P2D);
        colorMode(HSB, 1, 1, 1, 1);
    }

    @Override
    public void draw() {
        pg.beginDraw();
        pg.clear();
        pg.image(gui.gradient("background"), 0, 0);
        drawNoiseRectangle();
        pg.endDraw();
        image(pg, 0, 0);
    }

    private void drawNoiseRectangle() {
        gui.pushFolder("sketch");
        pg.translate(gui.plotXY("translate", width / 2f, height / 2f).x, gui.plotXY("translate").y);

        pg.pushMatrix();
        gui.pushFolder("rect");
        {
            PVector rectPos = gui.plotXY("pos");
            PVector rectSize = gui.plotXY("size", 450, 300);
            pg.fill(gui.colorPicker("fill", color(0.65f)).hex);
            pg.stroke(gui.colorPicker("stroke").hex);
            pg.strokeWeight(gui.slider("weight", 5));
            pg.translate(rectPos.x, rectPos.y);
            pg.rectMode(CORNER);
            pg.rect(0, 0, rectSize.x, rectSize.y);
        }
        gui.popFolder();
        pg.popMatrix();

        gui.pushFolder("graph");
        pg.pushMatrix();
        {
            PVector graphPos = gui.plotXY("pos");
            PVector graphSize = gui.plotXY("graph size", 400,200);
            float sinHeight = gui.slider("sin height", 100);
            float barWidth = gui.slider("bar width", 1);
            pg.translate(graphPos.x, graphPos.y);
            int count = gui.sliderInt("count", 6, 2, width);
            float freq = gui.slider("freq", 1);
            float time = gui.slider("time");
            gui.sliderSet("time", time + radians(gui.slider("time speed", 1)));
            pg.stroke(gui.colorPicker("stroke").hex);
            pg.strokeWeight(gui.slider("weight", 5));
            pg.rectMode(CENTER);
            gui.gradient("fill gradient", new int[]{unhex("FF86A3B8"), unhex("FFE8D2A6"), unhex("FFF48484"), unhex("FFF55050")});
            for (int i = 0; i < count; i++) {
                float iNorm = norm(i, 0, count - 1);
                float x = iNorm * graphSize.x;
                float sinX = sin(iNorm * TAU * freq + time);
                float w = graphSize.x / (count-1) * barWidth;
                pg.fill(gui.gradientColorAt("fill gradient", 0.5f + 0.5f * sinX).hex);
                float h = -sinX * sinHeight - graphSize.y;
                pg.rect(x+w/2, -h/2, w, h);
            }
        }
        pg.popMatrix();
        gui.popFolder();

        pg.pushMatrix();
        gui.pushFolder("text");
        {
            if (gui.button("reset font") || segoeUIBlack == null) {
                segoeUIBlack = createFont(FontStore.mainFontPathDefault, gui.slider("size", 64));
            }
            pg.textFont(segoeUIBlack, gui.slider("size", 64));
            pg.fill(gui.colorPicker("fill").hex);
            pg.text(gui.text("title", "example sketch"),
                    gui.plotXY("pos", 0, -50).x,
                    gui.plotXY("pos").y);
        }
        gui.popFolder();
        pg.popMatrix();

        gui.popFolder();
    }
}

