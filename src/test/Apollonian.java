package test;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;
import toolbox.Gui;
import toolbox.global.ShaderStore;

@SuppressWarnings("DuplicatedCode")
public class Apollonian extends PApplet {
    Gui gui;
    PGraphics pg;
    PImage img;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P2D);
//        size(800,800,P2D);
    }

    public void setup() {
        gui = new Gui(this);
        img = loadImage("C:/Users/Krab/Desktop/me.jpg");
        pg = createGraphics(width, height, P2D);
    }

    public void draw() {
        pg.beginDraw();
        pg.noStroke();
        pg.clear();
        drawScene();
        gui.applyPremadeFilters("filters", pg);
        pg.endDraw();
        clear();
        image(pg, 0, 0);
        gui.draw();
        gui.record(pg);
    }

    float time = 0;
    private void drawScene() {
        String shaderPath = "fractals/apollo.glsl";
        PShader shader = ShaderStore.lazyInitGetShader(shaderPath);
        time += gui.slider("apollo/time") * radians(1);
        shader.set("time", time);
        shader.set("iterations", gui.sliderInt("apollo/iterations", 5));
        shader.set("scaleBase", gui.slider("apollo/scaleBase", 0.5f));
        shader.set("scaleMult", gui.slider("apollo/scaleMult", 2f));
        shader.set("range", gui.slider("apollo/range", 5f));
        shader.set("power", gui.slider("apollo/power", 1));
        shader.set("radius", gui.slider("apollo/radius", 0.5f));
        ShaderStore.hotFilter(shaderPath, pg);
    }
}





































