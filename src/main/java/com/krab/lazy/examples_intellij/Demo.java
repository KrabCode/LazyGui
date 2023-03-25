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
        fullScreen(P3D);
//        size(22*70, 22*40, P3D);
        smooth(8);
    }

    @Override
    public void setup() {
        gui = new LazyGui(this, new LazyGuiSettings()
//                .setLoadLatestSaveOnStartup(false)
//                .setAutosaveOnExit(false)
        );
        img = loadImage(defaultImagePath);
        frameRate(144);
    }

    @Override
    public void draw() {
        hint(DISABLE_DEPTH_TEST);
        drawBackground();
        drawSun();
        hint(ENABLE_DEPTH_TEST);
        lights();
        perspective();
        drawForeground();
        noLights();
        resetPerspective();
    }

    private void drawBackground() {
        gui.pushFolder("background");
        background(gui.colorPicker("solid").hex);
        if (gui.toggle("image", true)) {
            String path = gui.text("image path", defaultImagePath);
            if (gui.button("image reload")) {
                img = loadImage(path);
            }
            pushMatrix();
            imageMode(CENTER);
            float scale = gui.slider("image scale");
            translate(width / 2f, height / 2f);
            scale(scale, scale);
            image(img, 0, 0);
            popMatrix();
        }
        gui.popFolder();
    }

    private void drawSun() {
        gui.pushFolder("sun");
        pushMatrix();
        int detail = gui.sliderInt("detail", 360) + 1;
        float radius = gui.slider("radius", 250);
        PVector pos = gui.plotXY("pos", width / 2f, 200);
        translate(pos.x, pos.y);
        beginShape(TRIANGLE_FAN);
        noStroke();
        texture(gui.gradient("gradient"));
        textureMode(NORMAL);
        vertex(0, 0, 0.5f, 0);
        for (int i = 0; i < detail; i++) {
            float theta = map(i, 0, detail - 1, 0, TAU);
            vertex(radius * cos(theta), radius * sin(theta), 0.5f, 1f);
        }
        endShape();
        popMatrix();
        gui.popFolder();
    }

    private void drawForeground() {
        gui.pushFolder("grid");
        gui.gradient("z colors");
        PVector gridPos = gui.plotXYZ("grid pos");
        PVector gridSize = gui.plotXY("grid size", 1000);
        PVector gridHalf = PVector.div(gridSize, 2);
        PVector gridDetail = gui.plotXY("grid detail", 50);
        gridDetail.x = floor(gridDetail.x);
        gridDetail.y = floor(gridDetail.y);
        PVector step = new PVector(gridSize.x / gridDetail.x, gridSize.y / gridDetail.y);
        PVector rot = gui.plotXY("rotate xz", 1.4f, 0);
        translate(width / 2f + gridPos.x, height / 2f + gridPos.y, gridPos.z);
        rotateX(rot.x * PI);
        rotateZ(rot.y * PI);
        float weight0 = gui.slider("stroke weight 0", 3);
        float weight1 = gui.slider("stroke weight 1", 1);
        gui.pushFolder("noise");
        PVector terrainPos = gui.plotXY("pos");
        PVector terrainSpeed = gui.plotXY("speed");
        float amp = gui.slider("amp", 1, 0, 1);
        float freq = gui.slider("frequency", 0.1f) * 0.01f;
        float nh = gui.slider("height", 100);
        gui.plotSet("pos", terrainPos.copy().add(terrainSpeed));
        PVector gridOffset = new PVector(terrainPos.x % step.x, terrainPos.y % step.y);
        PVector noiseOffset = new PVector(floor(terrainPos.x / step.x) * step.x, floor(terrainPos.y / step.y) * step.y);
        translate(gridOffset.x, gridOffset.y);
        gui.popFolder();
        for (int yi = 1; yi < gridDetail.y; yi++) {
            if (gui.toggle("triangles\\/quads")) {
                beginShape(QUAD_STRIP);
            } else {
                beginShape(TRIANGLE_STRIP);
            }
            stroke(gui.colorPicker("stroke").hex);
            float yNorm = norm(yi, 0, gridDetail.y - 1);
            strokeWeight(lerp(weight0, weight1, yNorm));
            if (!gui.toggle("wireframe")) {
                noStroke();
            }
            float py = map(yi, 0, gridDetail.y, -gridHalf.y, gridHalf.y);
            for (int xi = 0; xi <= gridDetail.x; xi++) {
                float xNorm = norm(xi, 0, gridDetail.x);
                float px = map(xi, 0, gridDetail.x, -gridHalf.x, gridHalf.x);
                float qy = map(yi + 1, 0, gridDetail.y, -gridHalf.y, gridHalf.y);
                float pn = amp * noise((px - noiseOffset.x) * freq, (py - noiseOffset.y) * freq);
                float qn = amp * noise((px - noiseOffset.x) * freq, (qy - noiseOffset.y) * freq);
                pn *= valley(-1+2*xNorm);
                qn *= valley(-1+2*xNorm);
                float ph0 = pn * nh;
                float qh0 = qn * nh;
                int pColor = gui.gradientColorAt("z colors", 1 - pn).hex;
                int qColor = gui.gradientColorAt("z colors", 1 - qn).hex;
                fill(pColor);
                vertex(px, py, -ph0);
                fill(qColor);
                vertex(px, qy, -qh0);
            }
            endShape();
        }
        gui.popFolder();
    }

    public void resetPerspective() {
        float cameraFOV = radians(60); // at least for now
        float cameraY = height / 2.0f;
        float cameraZ = cameraY / ((float) Math.tan(cameraFOV / 2.0f));
        float cameraNear = cameraZ / 10;
        float cameraFar = cameraZ * 10;
        float cameraAspect = (float) width / (float) height;
        perspective(cameraFOV, cameraAspect, cameraNear, cameraFar);
    }

    public void perspective() {
        gui.pushFolder("perspective");
        float cameraFOV = radians(gui.slider("FOV", 60)); // at least for now
        float cameraY = height / 2.0f;
        float cameraZ = cameraY / ((float) Math.tan(cameraFOV / 2.0f));
        float cameraNear = gui.slider("near", cameraZ / 10.0f);
        float cameraFar = gui.slider("far", cameraZ * 10.0f);
        float cameraAspect = (float) width / (float) height;
        perspective(cameraFOV, cameraAspect, cameraNear, cameraFar);
        gui.popFolder();
    }

    public void perspective(float fov, float aspect, float zNear, float zFar) {
        float ymax = zNear * (float) Math.tan(fov / 2);
        float ymin = -ymax;
        float xmin = ymin * aspect;
        float xmax = ymax * aspect;
        frustum(xmin, xmax, ymin, ymax, zNear, zFar);
    }

    private float valley(float x) {
        return abs(max(0, abs(x)-gui.slider("valley width")))*gui.slider("valley height");
    }
}
