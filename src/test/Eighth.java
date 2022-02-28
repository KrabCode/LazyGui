package test;

import processing.core.PApplet;
import processing.core.PGraphics;
import toolbox.Gui;
import toolbox.global.State;
import toolbox.global.noise.OpenSimplexNoise;
import toolbox.windows.nodes.colorPicker.Color;

public class Eighth extends PApplet {
    Gui gui;
    PGraphics strip;
    OpenSimplexNoise noise;


    public static void main(String[] args) {
        PApplet.main("test.Eighth");
    }

    @Override
    public void settings() {
        size(800, 800, P2D);
//        fullScreen(P2D);

    }

    @Override
    public void setup() {
        gui = new Gui(this);
        strip = createGraphics(width, height, P2D);
        strip.smooth(16);
        noise = new OpenSimplexNoise();
    }


    float time = 0;

    @Override
    public void draw() {
        Color background = gui.colorPicker("background");
        background(background.hex);
        strip.beginDraw();
        strip.background(background.hex);
        time += radians(gui.slider("time", 1));
        strip.translate(gui.slider("lines/x"), gui.slider("lines/y"));
        if(gui.toggle("blend mode")){
            strip.blendMode(ADD);
        }else{
            strip.blendMode(BLEND);
        }
        drawNoiseLines();
        gui.shaderFilterList("strip shaders", strip);
        strip.endDraw();
        image(strip, width - strip.width, height - strip.height);
        gui.draw();
        gui.record(strip);
    }

    private void drawNoiseLines() {
        int count = gui.sliderInt("lines/count", 10);
        float radius = gui.slider("lines/radius", 300);
        for (int index = 0; index < count; index++) {
            float i = norm(index, 0, count);
            float a = i * TAU;
            float x = width * 0.5f + radius*cos(a);
            float y = height * 0.5f + radius*sin(a);
            drawNoiseLine(x, y);
        }
    }

    private void drawNoiseLine(float x, float y) {
        float weightStart = gui.slider("points/weight start", 1.99f);
        float weightEnd = gui.slider("points/weight end", 1.99f);
        Color strokeStart = gui.colorPicker("points/stroke start", State.normalizedColorProvider.color(1,1,0.5f)); //WHITE
        Color strokeEnd = gui.colorPicker("points/stroke end", State.normalizedColorProvider.color(1, 1, 1)); // RED
        int vertexCount = gui.sliderInt("points/count", 50);
        float pointDistance = gui.slider("points/distance", 1);
        float freq = gui.slider("points/angle frequency");
        float pointAngleRange = gui.slider("points/angle range", 0.2f);
        float timeRadius = gui.slider("points/speed");
        strip.noFill();
        strip.strokeCap(ROUND);
        float runningAngle = gui.slider("points/base angle") + atan2(y, x);
        for (int index = 0; index < vertexCount; index++) {
            float i = norm(index, 0, vertexCount - 1);
            runningAngle +=  pointAngleRange * (float) noise.eval(x * freq, y * freq, timeRadius * cos(time), timeRadius * sin(time));
            float lastX = x;
            float lastY = y;
            x += pointDistance * cos(runningAngle);
            y += pointDistance * sin(runningAngle);
            strip.stroke(lerpColor(strokeStart.hex, strokeEnd.hex, i));
            strip.strokeWeight(lerp(weightStart, weightEnd, i));
            strip.line(lastX, lastY, x, y);
        }
        strip.endShape();
    }
}
