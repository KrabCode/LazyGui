package com.krab.lazy.examples_intellij;

import com.krab.lazy.LazyGui;
import com.krab.lazy.LazyGuiSettings;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;


public class OutrunDemo extends PApplet {
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
        colorMode(HSB, 1, 1, 1, 1);
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
        perspective();
        drawNoisyQuadStrip();
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
            float scale = gui.slider("image scale", 1);
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
        int detail = gui.sliderInt("detail", 12) + 1;
        float radius = gui.slider("radius", 250);
        PVector pos = gui.plotXY("pos", width / 2f, 200);
        translate(pos.x, pos.y);
        beginShape(TRIANGLE_FAN);
        noStroke();
        texture(gui.gradient("gradient", new int[]{color(0), color(0), color(1), color(0, 0)}));
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

    private void drawNoisyQuadStrip() {
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
        noiseDetail(gui.sliderInt("detail"), gui.slider("faloff"));

        float terrainSpeed = gui.slider("speed", 1);
        float amp = gui.slider("amp", 1, 0, 1);
        float freq = gui.slider("frequency", 0.1f) * 0.01f;
        float nh = gui.slider("height", 100);
        gui.plotSet("pos", terrainPos.copy().add(0, terrainSpeed));
        PVector gridOffset = new PVector(terrainPos.x % step.x, terrainPos.y % step.y);
        PVector noiseOffset = new PVector(floor(terrainPos.x / step.x), floor(terrainPos.y / step.y));
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
                float pn = amp * noise((xi - floor(noiseOffset.x)) * freq, (yi - floor(noiseOffset.y)) * freq);
                float qn = amp * noise((xi - floor(noiseOffset.x)) * freq, (yi + 1 - floor(noiseOffset.y)) * freq);

                float valleyMultiplier = valleyMultiplier(-1 + 2 * xNorm);
                pn *= valleyMultiplier;
                qn *= valleyMultiplier;
                float ph0 = pn * nh;
                float qh0 = qn * nh;
                int pColor = gui.gradientColorAt("z colors", constrain(1 - pn * gui.slider("gradient pos *"), 0, 1)).hex;
                int qColor = gui.gradientColorAt("z colors", constrain(1 - qn * gui.slider("gradient pos *"), 0, 1)).hex;
                fill(pColor);
                vertex(px, py, -ph0);
                fill(qColor);
                vertex(px, qy, -qh0);
            }
            endShape();
        }
        gui.popFolder();
    }

    public void perspective() {
        gui.pushFolder("perspective");
        float cameraFOV = radians(gui.slider("FOV", 60)); // at least for now
        float cameraY = height / 2.0f;
        float cameraZ = cameraY / ((float) Math.tan(cameraFOV / 2.0f));
        float cameraNear = cameraZ * gui.slider("near", 0.01f);
        float cameraFar = cameraZ * gui.slider("far", 10.0f);
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

    // TODO make it make sense lmao
    private float valleyMultiplier(float x) {
        gui.pushFolder("valley");
        float result = 1;
        if (gui.toggle("active")) {
            float valleyRange = gui.slider("valley range", 1);
            result = abs(max(0, pow(abs(x), gui.slider("slope power", 1)) * valleyRange - gui.slider("center width"))) * gui.slider("side height", 1f);
        }
        gui.popFolder();
        return constrain(result, 0, 1);
    }
}
