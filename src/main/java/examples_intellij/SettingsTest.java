package examples_intellij;

import lazy.LazyGuiSettings;
import lazy.themes.Theme;
import lazy.themes.ThemeType;
import processing.core.PApplet;
import lazy.LazyGui;

public class SettingsTest extends PApplet {
    LazyGui gui;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    @Override
    public void settings() {
        size(1080, 1080, P2D);
    }

    public void setup() {
        gui = new LazyGui(this, new LazyGuiSettings()

                // LOADING ON STARTUP
                .setLoadLatestSaveOnStartup(true) // set as false to not load anything on startup
                .setLoadSpecificSaveOnStartup("2") // expects filenames like "1" or "auto.json"

                // AUTOSAVE
                .setAutosaveOnExit(true)    // but the shutdown hook only works on graceful exit, for example the ESC button
                .setAutosaveLockGuardEnabled(true) // for not autosaving settings that locked the sketch in an endless loop
                .setAutosaveLockGuardMillisLimit(1000) // millis the last frame must be rendered faster than for autosave to work

                // MOUSE
                .setMouseShouldHideWhenDragging(true) // when dragging a slider for example
                .setMouseShouldConfineToWindow(false)

                // LAYOUT
                .setCellSize(22) // affects the size of the whole gui
                .setMainFontSize(16)
                .setSideFontSize(15)
                .setStartGuiHidden(false) // uncover hidden gui with the 'h' hotkey

                // THEME
                .setThemePreset(ThemeType.PINK)
                .setThemeCustom(new Theme(
                        color(150, 0, 255), // window border color
                        color(16), // normal background color
                        color(0, 0, 0), // focused background color
                        color(200), // normal foreground color
                        color(255)          // focused foreground color
                )) // custom theme overrides preset when not null
        );


        textSize(64);
    }

    public void draw() {
        background(gui.colorPicker("background").hex);
        int number = gui.sliderInt("pick a number", 7);
        fill(255);
        text(number, 400, 500);
    }

}

