package test;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;
import toolbox.Gui;
import toolbox.global.ShaderStore;
import toolbox.global.State;

public class ImagePostProcess extends PApplet {
    Gui gui;
    PGraphics pg;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(800, 800, P2D);
//        fullScreen(P2D);
    }

    public void setup() {
        gui = new Gui(this);
        pg = createGraphics(width, height, P2D);
    }

    public void draw() {
        pg.beginDraw();
        pg.noStroke();
        pg.image(gui.gradient("background"), 0, 0);
        String chromaKeyPath = "chromaKey.glsl";
        int hex = gui.colorPicker("chroma/targetColor").hex;

        PShader chromaKey = ShaderStore.lazyInitGetShader(chromaKeyPath);
        chromaKey.set("targetColor", new float[]{
                State.normalizedColorProvider.red(hex),
                State.normalizedColorProvider.green(hex),
                State.normalizedColorProvider.blue(hex)
        }, 3);
        chromaKey.set("thresholdA", gui.slider("chroma/A", 0));
        chromaKey.set("thresholdB", gui.slider("chroma/B", 1));
        ShaderStore.hotShader(chromaKeyPath, pg);

        pg.image(gui.imagePicker("chroma/img", "C:\\Projects\\Toolbox\\src\\test\\assets\\umbrella.jpg"), 0, 0);
        pg.resetShader();

        gui.shaderFilterList("filters", pg);
        pg.image(gui.gradient("overlay", 0), 0, 0);
        pg.endDraw();
        clear();
        image(pg, 0, 0);
        gui.draw();
        gui.record(pg);
    }
}
