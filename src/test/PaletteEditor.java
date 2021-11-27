package test;

import processing.core.PApplet;
import toolbox.Gui;
import toolbox.global.GuiPaletteStore;

import static toolbox.global.themes.GuiPaletteColorType.*;

public class PaletteEditor extends PApplet {
    Gui gui;


    public static void main(String[] args) {
        PApplet.main("test.PaletteEditor");
    }


    @Override
    public void settings() {
//        size(1600,800,P2D);
//        size(600, 600, P2D);
        fullScreen(P2D);
    }

    @Override
    public void setup() {
        gui = new Gui(this);
    }

    @Override
    public void draw() {
        background(gui.colorPicker("sketch background", 0xFF000000).hex);
        boolean switchPalette = gui.button("switch palette");
        GuiPaletteStore.set(windowBorder, gui.colorPicker    ("palette/window border"       , GuiPaletteStore.get(windowBorder)).hex);
        GuiPaletteStore.set(normalBackground, gui.colorPicker("palette/normal background"   , GuiPaletteStore.get(normalBackground )).hex);
        GuiPaletteStore.set(focusBackground, gui.colorPicker( "palette/focus background"    , GuiPaletteStore.get(focusBackground)).hex);
        GuiPaletteStore.set(normalForeground, gui.colorPicker("palette/normal foreground"   , GuiPaletteStore.get(normalForeground)).hex);
        GuiPaletteStore.set(focusForeground, gui.colorPicker( "palette/focus foreground"    , GuiPaletteStore.get(focusForeground)).hex);
        if(switchPalette){
            GuiPaletteStore.nextPalette();
            gui.colorPickerSet("palette/window border"    ,  GuiPaletteStore.get(windowBorder));
            gui.colorPickerSet("palette/normal background",  GuiPaletteStore.get(normalBackground ));
            gui.colorPickerSet("palette/focus background" ,  GuiPaletteStore.get(focusBackground));
            gui.colorPickerSet("palette/normal foreground",  GuiPaletteStore.get(normalForeground));
            gui.colorPickerSet("palette/focus foreground" ,  GuiPaletteStore.get(focusForeground));
        }
        gui.update();
        gui.recorder();
    }
}
