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
        size(1600,800,P2D);

    }

    @Override
    public void setup() {
        gui = new Gui(this);
    }

    @Override
    public void draw() {
        background(gui.colorPicker("background", 0xFF000000).hex);
        Palette.windowBorderStroke = gui.colorPicker("windowBorderStroke", 0xFF787878).hex;
        Palette.windowBorderStrokeFocused = gui.colorPicker("windowBorderStrokeFocused", 0xFF787878).hex;
        Palette.standardWindowTitleFill = gui.colorPicker("standardWindowTitleFill", 0xFF000000).hex;
        Palette.draggedWindowTitleFill = gui.colorPicker("draggedWindowTitleFill", 0xFF2F2F2F).hex;
        Palette.contentBackgroundFill = gui.colorPicker("contentBackgroundFill", 0xFF000000).hex;
        Palette.contentBackgroundFocusedFill = gui.colorPicker("contentBackgroundFocusedFill", 0xFF2F2F2F).hex;
        Palette.standardTextFill = gui.colorPicker("standardTextFill", 0xFFB0B0B0).hex;
        Palette.standardContentFill = gui.colorPicker("standardContentFill", 0xFFB0B0B0).hex;
        Palette.standardContentStroke = gui.colorPicker("standardContentStroke", 0xFFFFFFFF).hex;
        Palette.selectedTextFill = gui.colorPicker("selectedTextFill", 0xFFE2E2E2).hex;
        Palette.selectedContentFill = gui.colorPicker("selectedContentFill", 0xFFE2E2E2).hex;
        Palette.selectedContentStroke = gui.colorPicker("selectedContentStroke", 0xFFE2E2E2).hex;
        gui.update();
    }
}
