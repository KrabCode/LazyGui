package examples;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import lazy.LazyGui;
import lazy.global.Utils;

public class FullShowcase extends PApplet {

    LazyGui gui;
    PGraphics pg;
    private float rotationTime;
    String recSketchId = Utils.generateRandomShortId();

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
//        size(1000, 1000, P3D);
        fullScreen(P3D);
    }

    public void setup() {
        gui = new LazyGui(this);
        pg = createGraphics(width, height, P3D);
        colorMode(HSB, 1, 1, 1, 1);
    }

    @SuppressWarnings("DuplicatedCode")
    public void draw() {
        pg.beginDraw();
        drawBg();
        drawBrush();
        drawBox();
        pg.endDraw();
        clear();
        image(pg, 0, 0);
        if(frameCount < 800){
//            pg.save("out/rec/"+ recSketchId + "/" + frameCount + ".jpg");
        }
    }

    private void drawBg() {
        boolean disableDepthTest = gui.toggle("background/disable depth test", true);
        if (disableDepthTest) {
            pg.hint(PConstants.DISABLE_DEPTH_TEST);
        }
        pg.noStroke();
        pg.blendMode(getBlendMode("background", "subtract"));
        if (gui.toggle("background/use gradient")) {
            pg.image(gui.gradient("background/gradient colors"), 0, 0);
        } else {
            pg.fill(gui.colorPicker("background/solid color", color(0xFF081014)).hex);
            pg.rect(0, 0, width, height);
        }
        if (disableDepthTest) {
            pg.hint(PConstants.ENABLE_DEPTH_TEST);
        }
        pg.blendMode(BLEND);
    }

    private int getBlendMode(String path, String defaultMode) {
        String selectedMode = gui.stringPicker(path + "/blend mode",
                new String[]{"blend", "add", "subtract"}, defaultMode);
        switch (selectedMode) {
            case "add":
                return ADD;
            case "subtract":
                return SUBTRACT;
            default:
                return BLEND;
        }
    }

    private void drawBrush() {
        pg.fill(gui.colorPicker("mouse brush/color", color(1)).hex);
        float brushWeight = gui.slider("mouse brush/weight", 5);
        if (gui.mousePressedOutsideGui()) {
            float dist = dist(pmouseX, pmouseY, mouseX, mouseY);
            for (int i = 0; i <= dist; i++) {
                float iNorm = norm(i, 0, dist);
                pg.ellipse(lerp(mouseX, pmouseX, iNorm), lerp(mouseY, pmouseY, iNorm), brushWeight, brushWeight);
            }
        }
    }

    private void drawBox() {
        pg.strokeWeight(gui.slider("box/stroke weight", 2));
        pg.stroke(gui.colorPicker("box/stroke color", color(1)).hex);
        int fillColor = gui.colorPicker("box/fill color", color(0.05f)).hex;
        if (gui.toggle("box/no fill", true)) {
            pg.noFill();
        } else {
            pg.fill(fillColor);
        }
        pg.translate(width / 2f, height / 2f);
        pg.translate(gui.slider("box/pos/x"),
                gui.slider("box/pos/y"),
                gui.slider("box/pos/z", 500));
        float boxSize = gui.slider("box/size", 120);
        float rotationSpeed = gui.slider("box/rotate speed", 0.5f);
        rotationTime += radians(rotationSpeed);
        pg.rotate(rotationTime, gui.slider("box/rotate axis/x", 0),
                gui.slider("box/rotate axis/y", 0),
                gui.slider("box/rotate axis/z", 1));
        pg.blendMode(getBlendMode("box", "blend"));
        pg.box(boxSize);
        if (gui.toggle("box/recursion/active", false)) {
            int copies = gui.sliderInt("box/recursion/count", 5);
            float scale = gui.slider("box/recursion/scale", 0.9f);
            for (int i = 0; i < copies; i++) {
                pg.scale(scale);
                pg.box(boxSize);
            }
        }
    }
}
