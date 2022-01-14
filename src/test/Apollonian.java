package test;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;
import toolbox.Gui;
import toolbox.global.ShaderStore;

@SuppressWarnings("DuplicatedCode")
public class Apollonian extends PApplet {
    Gui gui;
    PGraphics pg;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        fullScreen(P2D);
    }

    public void setup() {
        gui = new Gui(this);
        pg = createGraphics(width, height, P2D);
    }

    public void draw() {
        pg.beginDraw();
        pg.noStroke();
        pg.clear();
        drawScene();
        gui.shaderFilterList("filters", pg);
        pg.endDraw();
        clear();
        image(pg, 0, 0);
        gui.draw();
        gui.record(pg);
    }

    private void drawScene() {
        String shaderPath = "fractals/apollo.glsl";
        PShader shader = ShaderStore.lazyInitGetShader(shaderPath);
        shader.set("time", radians(frameCount));
        shader.set("customGradient", gui.toggle("apollo/custom gradient"));
        shader.set("gradient", gui.gradient("apollo/gradient"));
        shader.set("iterations", gui.sliderInt("apollo/iterations", 5));
        shader.set("scaling", gui.slider("apollo/scale", 2f));
        shader.set("range", gui.slider("apollo/range", 5f));
        shader.set("ampBase", gui.slider("apollo/ampBase", 1));
        shader.set("ampMult", gui.slider("apollo/ampMult", 0.5f));
        shader.set("offsetX", gui.slider("apollo/offsetX", 0.5f));
        shader.set("offsetY", gui.slider("apollo/offsetY", 0.5f));
        ShaderStore.hotFilter(shaderPath, pg);
    }
}





































