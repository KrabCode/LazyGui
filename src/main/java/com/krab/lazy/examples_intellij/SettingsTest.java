package com.krab.lazy.examples_intellij;

import com.krab.lazy.LazyGuiSettings;
import processing.core.PApplet;
import com.krab.lazy.LazyGui;

public class SettingsTest extends PApplet {
    LazyGui gui;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    @Override
    public void settings() {
        size(600,600, P2D);
        noSmooth();
    }

    public void setup() {
        gui = new LazyGui(this, new LazyGuiSettings()
            // AUTOLOAD

//            .setLoadLatestSaveOnStartup(false) // set as false to not load anything on startup

//            .setLoadSpecificSaveOnStartup("auto.json") // expects filenames like "1" or "auto.json" or an absolute path
//            .setLoadSpecificSaveOnStartupOnce("C:\\Users\\Krab\\Desktop\\auto.json") // loads save only when the save folder is found empty

            // AUTOSAVE
            .setAutosaveOnExit(false)    // the shutdown hook only works on graceful exit, for example the ESC button
            .setAutosaveLockGuardEnabled(true) // for not autosaving settings that locked the sketch in an endless loop
            .setAutosaveLockGuardMillisLimit(1000) // millis the last frame must be rendered faster than for autosave to work

            // MOUSE
            .setMouseHideWhenDragging(true) // when dragging a slider for example
            .setMouseConfineToWindow(false)

            // LAYOUT
            .setCellSize(22) // affects the size of the whole gui
            .setMainFontSize(16)
            .setSideFontSize(15)
            .setStartGuiHidden(true) // uncover hidden gui with the 'h' hotkey

            // THEME
            .setThemePreset("dark") // selected preset, one of "dark", "light", "pink", "blue"
            .setThemeCustom(
                    color(0, 0, 255),   // window border color
                    color(16),       // normal background color
                    color(0, 0, 0),      // focused background color
                    color(200),     // normal foreground color
                    color(255))    // focused foreground color
            // custom theme overrides preset when not null

            .setAutosuggestWindowWidth(true)
            .setSketchNameOverride("GUI Root")
            .setSmooth(16)
            .setHideBuiltInFolders(true)
            .setHideRadioValue(true)
        );


        textSize(64);
    }

    public void draw() {
        image(gui.gradient("background"), 0, 0);
        gui.draw();
    }
}

