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
//        fullScreen(P3D);
        size(22*60, 22*36, P3D);
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
        PVector gridPos = gui.plotXY("grid pos");
        float gridSizeHalf = gui.slider("grid size", 1000) / 2f;
        float gridDetail = gui.sliderInt("grid detail", 50);
        float freq = gui.slider("frequency", 0.1f);
        float nh = gui.slider("noise height", 100);
        PVector rot = gui.plotXY("rotate xz");
        PVector noisePos = gui.plotXY("noise pos");
        PVector noiseSpeed = gui.plotXY("noise speed");
        translate(width / 2f + gridPos.x, height / 2f + gridPos.y);
        rotateX(rot.x * PI);
        rotateZ(rot.y * PI);
        gui.plotSet("noise pos", noisePos.copy().add(noiseSpeed.div(100f)));
        for (int yi = 0; yi < gridDetail; yi++) {
            beginShape(TRIANGLE_STRIP);
            stroke(gui.colorPicker("stroke").hex);
            strokeWeight(gui.slider("stroke weight"));
            if(!gui.toggle("wireframe")){
                noStroke();
            }
            for (int xi = 0; xi < gridDetail; xi++) {
                float px = map(xi, 0, gridDetail-1, -gridSizeHalf, gridSizeHalf);
                float py = map(yi, 0, gridDetail-1, -gridSizeHalf, gridSizeHalf);
                float qx = map(xi,0, gridDetail-1, -gridSizeHalf, gridSizeHalf);
                float qy = map(yi+1,0, gridDetail-1, -gridSizeHalf, gridSizeHalf);
                float pn = noise((noisePos.x + px) * 0.1f * freq, (noisePos.y + py) * 0.1f * freq);
                float qn = noise((noisePos.x + qx) * 0.1f * freq, (noisePos.y + qy) * 0.1f * freq);
                float ph0 = pn * nh;
                float qh0 = qn * nh;
                int pColor = gui.gradientColorAt("z colors", pn).hex;
                int qColor = gui.gradientColorAt("z colors", qn).hex;
                pushMatrix();
                fill(pColor);
                vertex(px,py,ph0);
                fill(qColor);
                vertex(qx,qy,qh0);
                popMatrix();
            }
            endShape();
        }
        gui.popFolder();
    }
}
