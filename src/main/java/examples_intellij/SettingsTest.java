package examples_intellij;

import lazy.LazyGuiSettings;
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
            .setMouseConfinedToWindow(true)
            .setMouseHidesWhenDragging(true)
            .setLoadLatestSaveOnStartup(false)
            .setAutosaveOnExitEnabled(false)
                .setCellSize(28)
                .setMainFontSize(18)
                .setSideFontSize(16)
        );

        /*
        *
                .setCustomTheme(new Theme(
                        color(0),
                        color(20),
                        color(50),
                        color(200, 0, 50),
                        color(255)
                )
        * */
        pg = createGraphics(width, height, P2D);
        colorMode(HSB, 1, 1, 1, 1);
        frameRate(144);
    }

    @Override
    public void draw() {
        pg.beginDraw();
        image(gui.gradient("background"), 0, 0);

        pg.endDraw();
        image(pg, 0, 0);
        gui.draw();
    }
}

