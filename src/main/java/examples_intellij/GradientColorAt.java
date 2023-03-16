package examples_intellij;

import lazy.LazyGui;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

@SuppressWarnings("DuplicatedCode")
public class GradientColorAt extends PApplet {
    LazyGui gui;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1200, 1200, P2D);
        smooth(8);
    }

    public void setup() {
        gui = new LazyGui(this);
    }

    public void draw() {
        gui.pushFolder("scene");
        drawBackground();
        drawForegroundShape();
        gui.popFolder();
    }

    private void drawForegroundShape() {
        gui.pushFolder("shape");
        String[] shapeTypeOptions = new String[]{
                "rectangle",
                "ellipse"
        };
        String selectedShape = gui.radio("shape type", shapeTypeOptions);
        PVector pos = gui.plotXY("position", 0, height / 2f);
        PVector size = gui.plotXY("size", 50);
        float rotationAngle = gui.slider("rotation");
        float rotationAngleDelta = gui.slider("rotation ++", 0.1f);
        gui.sliderSet("rotation", rotationAngle + rotationAngleDelta);
        stroke(gui.colorPicker("stroke").hex);
        strokeWeight(gui.slider("stroke weight", 10));
        if(gui.toggle("no stroke")){
            noStroke();
        }
        gui.popFolder();
        gui.pushFolder("background");
        if(gui.toggle("gradientColorAt", true)){
            float colorAtPosNorm = gui.slider("fill(gradientColorAt())", 0.5f, 0, 1);
            fill(gui.gradientColorAt("gradient", colorAtPosNorm).hex);
            translate(width * colorAtPosNorm, 0);
        }
        gui.popFolder();
        rectMode(CENTER);
        translate(pos.x, pos.y);
        rotate(radians(rotationAngle));
        if(selectedShape.equals("ellipse")){
            ellipse(0, 0, size.x, size.y);
        }else{
            rect(0, 0, size.x, size.y);
        }
        gui.popFolder();
    }

    private void drawBackground() {
        gui.pushFolder("background");
        int solidBackgroundColor = gui.colorPicker("solid", color(0xFF252525)).hex;
        PGraphics gradient = gui.gradient("gradient");
        background(solidBackgroundColor);
        image(gradient, 0, 0);
        gui.popFolder();
    }
}
