package com.krab.lazy.examples_intellij;

import com.krab.lazy.LazyGui;
import com.krab.lazy.LazyGuiSettings;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

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
        float freq = gui.slider("frequency", 0.1f) * 0.01f;
        float nh = gui.slider("noise height", 100);
        PVector rot = gui.plotXY("rotate xz");
        PVector noisePos = gui.plotXY("noise pos");
        PVector noiseSpeed = gui.plotXY("noise speed");
        translate(width / 2f + gridPos.x, height / 2f + gridPos.y);
        rotateX(rot.x * PI);
        rotateZ(rot.y * PI);

        gui.plotSet("noise pos", noisePos.copy().add(noiseSpeed.div(100f)));
        ArrayList<PVector> noisePositions = new ArrayList<>();
        for (int yi = 0; yi < gridDetail; yi++) {
            if(gui.toggle("triangles\\/quads")){
                beginShape(QUAD_STRIP);
            }else{
                beginShape(TRIANGLE_STRIP);
            }
            stroke(gui.colorPicker("stroke").hex);
            strokeWeight(gui.slider("stroke weight"));
            if(!gui.toggle("wireframe")){
                noStroke();
            }
            for (int xi = 0; xi <= gridDetail; xi++) {
                float px = map(xi, 0, gridDetail, -gridSizeHalf, gridSizeHalf);
                float py = map(yi, 0, gridDetail, -gridSizeHalf, gridSizeHalf);
                float qx = px;
                float qy = map(yi+1,0, gridDetail, -gridSizeHalf, gridSizeHalf);
                float pn = noise((noisePos.x + px) * freq, (noisePos.y + py) * freq);
                float qn = noise((noisePos.x + qx) * freq, (noisePos.y + qy) * freq);
                float ph0 = (-1+2)*pn * nh;
                float qh0 = (-1+2)*qn * nh;
                int pColor = gui.gradientColorAt("z colors", pn).hex;
                int qColor = gui.gradientColorAt("z colors", qn).hex;
                fill(pColor);
                vertex(px,py,ph0);
                fill(qColor);
                vertex(qx,qy,qh0);
                noisePositions.add(new PVector(px, py, ph0));
                noisePositions.add(new PVector(qx, qy, qh0));
            }
            endShape();
        }

        noFill();
        rectMode(CENTER);
        for (PVector p : noisePositions) {
            line(p.x, p.y, p.z, p.x, p.y, nh);
        }
        translate(0,0,nh);
        rect(0,0,gridSizeHalf*2, gridSizeHalf*2);
        gui.popFolder();
    }
}
