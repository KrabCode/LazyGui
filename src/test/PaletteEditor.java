package test;

import processing.core.PApplet;
import toolbox.Gui;
import toolbox.global.Palette;

public class PaletteEditor extends PApplet {
    Gui gui;


    public static void main(String[] args) {
        PApplet.main("test.PaletteEditor");
    }


    @Override
    public void settings() {
//        size(1600,800,P2D);
        size(600, 600, P2D);
    }

    @Override
    public void setup() {
        gui = new Gui(this);
    }

    @Override
    public void draw() {
        background(gui.colorPicker("sketch background", 0xFF000000).hex);
        Palette.windowBorder = gui.colorPicker("palette/window border", Palette.windowBorder).hex;
        Palette.normalBackground = gui.colorPicker("palette/normal background", Palette.normalBackground ).hex;
        Palette.focusBackground = gui.colorPicker("palette/focus background", Palette.focusBackground).hex;
        Palette.normalForeground = gui.colorPicker("palette/normal foreground", Palette.normalForeground).hex;
        Palette.focusForeground = gui.colorPicker("palette/focus foreground", Palette.focusForeground).hex;
        gui.update();
        gui.recorder();
    }
}
