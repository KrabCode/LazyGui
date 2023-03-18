package examples_intellij;

import lazy.LazyGuiSettings;
import lazy.themes.ThemeType;
import processing.core.PApplet;
import processing.core.PGraphics;
import lazy.LazyGui;

public class SettingsTest extends PApplet {
    LazyGui gui;
    PGraphics pg;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    @Override
    public void settings() {
        size(1080, 1080, P2D);
    }

    @Override
    public void setup() {
        gui = new LazyGui(this, new LazyGuiSettings()
            .setMouseShouldHideWhenDragging(true)
            .setLoadLatestSaveOnStartup(false)
            .setAutosaveOnExit(false)
            .setThemePreset(ThemeType.DARK)
            .setStartGuiHidden(true)
        );
        pg = createGraphics(width, height, P2D);
        colorMode(HSB, 1, 1, 1, 1);
        frameRate(144);
    }

    @Override
    public void draw() {
        pg.beginDraw();
        image(gui.gradient("background", new int[]{
               unhex("FFFFF4E0"),
               unhex("FFFFBF9B"),
               unhex("FFB46060"),
               unhex("FF4D4D4D")
        }), 0, 0);

        pg.endDraw();
        image(pg, 0, 0);
        gui.draw();
    }
}

