package test;

import processing.core.PApplet;
import processing.core.PGraphics;
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
        size(1000, 1000, P2D);
//        fullScreen(P2D);
    }

    public void setup() {
        surface.setAlwaysOnTop(true);
        gui = new Gui(this);
        pg = createGraphics(width, height, P2D);
    }

    public void draw() {
        pg.beginDraw();
        pg.noStroke();
        pg.image(gui.gradient("background", 0), 0, 0);
        drawScene();
        gui.shaderFilterList("filters", pg);
        pg.endDraw();
        clear();
        image(pg, 0, 0);
        gui.draw();
        gui.record(pg);
    }

    private void drawScene() {
        String shaderPath = "wip/apollo.glsl";
        ShaderStore.lazyInitGetShader(shaderPath).set("time", radians(frameCount));
        ShaderStore.hotFilter(shaderPath, pg);
    }
}





































