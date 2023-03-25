package com.krab.lazy.examples_intellij;

import com.krab.lazy.LazyGui;
import com.krab.lazy.LazyGuiSettings;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Demo extends PApplet {
    LazyGui gui;
    PImage img;
    private final String defaultImagePath = "https://i.imgur.com/oIppd93.jpg";

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    @Override
    public void settings() {
//        fullScreen(P3D);
        size(22*70, 22*40, P3D);
        smooth(8);
    }

    @Override
    public void setup() {
        gui = new LazyGui(this, new LazyGuiSettings()
//                .setLoadLatestSaveOnStartup(false)
//                .setAutosaveOnExit(false)
        );
        img = loadImage(defaultImagePath);
    }

    @Override
    public void draw() {
        hint(DISABLE_DEPTH_TEST);
        drawBackground();
        drawSun();
        hint(ENABLE_DEPTH_TEST);
        drawForeground();
    }

    private void drawBackground() {
        gui.pushFolder("background");
        background(gui.colorPicker("solid").hex);
        if(gui.toggle("image", true)){
            String path = gui.text("image path", defaultImagePath);
            if(gui.button("image reload")){
                img = loadImage(path);
            }
            pushMatrix();
            imageMode(CENTER);
            float scale = gui.slider("image scale");
            translate(width/2f, height/2f);
            scale(scale, scale);
            image(img, 0, 0);
            popMatrix();
        }
        gui.popFolder();
    }

    private void drawSun() {
        gui.pushFolder("sun");
        pushMatrix();
        int detail = gui.sliderInt("detail", 360);
        float radius = gui.slider("radius", 250);
        PVector pos = gui.plotXY("pos", width/2f, 200);
        translate(pos.x, pos.y);
        beginShape(TRIANGLE_FAN);
        noStroke();
        texture(gui.gradient("gradient"));
        textureMode(NORMAL);
        vertex(0, 0, 0.5f, 0);
        for (int i = 0; i < detail; i++) {
            float theta = map(i, 0, detail - 1, 0, TAU);
            vertex(radius*cos(theta), radius*sin(theta), 0.5f, 1f);
        }
        endShape();
        popMatrix();
        gui.popFolder();
    }

    private void drawForeground() {
        gui.pushFolder("grid");
        gui.gradient("z colors");
        PVector gridPos = gui.plotXY("grid pos");
        float gridSizeHalf = gui.slider("grid size", 1000) / 2f;
        float gridDetail = gui.sliderInt("grid detail", 50);
        float freq = gui.slider("frequency", 0.1f) * 0.01f;
        float nh = gui.slider("noise height", 100);
        PVector rot = gui.plotXY("rotate xz", 1.4f, 0);
        PVector noisePos = gui.plotXY("noise pos");
        PVector noiseSpeed = gui.plotXY("noise speed");
        translate(width / 2f + gridPos.x, height / 2f + gridPos.y);
        rotateX(rot.x * PI);
        rotateZ(rot.y * PI);
        float weight0 = gui.slider("stroke weight 0", 3);
        float weight1 = gui.slider("stroke weight 1", 1);

        gui.plotSet("noise pos", noisePos.copy().add(noiseSpeed.div(100f)));
        for (int yi = 0; yi < gridDetail; yi++) {
            if(gui.toggle("triangles\\/quads")){
                beginShape(QUAD_STRIP);
            }else{
                beginShape(TRIANGLE_STRIP);
            }
            stroke(gui.colorPicker("stroke").hex);
            float yNorm = norm(yi, 0, gridDetail-1);
            strokeWeight(lerp(weight0, weight1, yNorm));
            if(!gui.toggle("wireframe")){
                noStroke();
            }
            float py = map(yi, 0, gridDetail, -gridSizeHalf, gridSizeHalf);
            for (int xi = 0; xi <= gridDetail; xi++) {
                float px = map(xi, 0, gridDetail, -gridSizeHalf, gridSizeHalf);
                float qy = map(yi+1,0, gridDetail, -gridSizeHalf, gridSizeHalf);
                float pn = noise((noisePos.x + px) * freq, (noisePos.y + py) * freq);
                float qn = noise((noisePos.x + px) * freq, (noisePos.y + qy) * freq);
                pn *= valley(px);
                qn *= valley(px);
                float ph0 = pn * nh;
                float qh0 = qn * nh;
                int pColor = gui.gradientColorAt("z colors", 1-pn).hex;
                int qColor = gui.gradientColorAt("z colors", 1-qn).hex;
                fill(pColor);
                vertex(px,py,-ph0);
                fill(qColor);
                vertex(px,qy,-qh0);
            }
            endShape();
        }
        gui.popFolder();
    }

    private float valley(float x) {
        float valleyWidth = gui.slider("valley width", 1);
        return constrain(1-pow(abs(x*valleyWidth), gui.slider("valley power", 8)), 0, 1);
    }
}
