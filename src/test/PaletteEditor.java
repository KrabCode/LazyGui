package test;

import processing.core.PApplet;
import toolbox.Gui;
import toolbox.global.GuiPaletteStore;

import static toolbox.global.palettes.GuiPaletteColorType.*;

public class PaletteEditor extends PApplet {
    Gui gui;


    public static void main(String[] args) {
        PApplet.main("test.PaletteEditor");
    }


    @Override
    public void settings() {
//        size(1600,800,P2D);
        size(600, 600, P2D);
//        fullScreen(P2D);
    }

    @Override
    public void setup() {
        gui = new Gui(this);
    }

    @Override
    public void draw() {
        background(gui.colorPicker("sketch background", 0xFF000000).hex);
        boolean switchPalette = false;
        if(gui.button("palette/> dark")){
            GuiPaletteStore.setPaletteIndex(0);
            switchPalette = true;
        }else if(gui.button("palette/> pink")){
            GuiPaletteStore.setPaletteIndex(1);
            switchPalette = true;
        }else if(gui.button("palette/> new")){
            GuiPaletteStore.setPaletteIndex(2);
            switchPalette = true;
        }
        if(switchPalette){
            gui.colorPickerSet("palette/window border"    ,  GuiPaletteStore.get(WINDOW_BORDER));
            gui.colorPickerSet("palette/normal background",  GuiPaletteStore.get(NORMAL_BACKGROUND));
            gui.colorPickerSet("palette/focus background" ,  GuiPaletteStore.get(FOCUS_BACKGROUND));
            gui.colorPickerSet("palette/normal foreground",  GuiPaletteStore.get(NORMAL_FOREGROUND));
            gui.colorPickerSet("palette/focus foreground" ,  GuiPaletteStore.get(FOCUS_FOREGROUND));
        }
        GuiPaletteStore.set(WINDOW_BORDER, gui.colorPicker    ("palette/window border"       , GuiPaletteStore.get(WINDOW_BORDER)).hex);
        GuiPaletteStore.set(NORMAL_BACKGROUND, gui.colorPicker("palette/normal background"   , GuiPaletteStore.get(NORMAL_BACKGROUND)).hex);
        GuiPaletteStore.set(FOCUS_BACKGROUND, gui.colorPicker( "palette/focus background"    , GuiPaletteStore.get(FOCUS_BACKGROUND)).hex);
        GuiPaletteStore.set(NORMAL_FOREGROUND, gui.colorPicker("palette/normal foreground"   , GuiPaletteStore.get(NORMAL_FOREGROUND)).hex);
        GuiPaletteStore.set(FOCUS_FOREGROUND, gui.colorPicker( "palette/focus foreground"    , GuiPaletteStore.get(FOCUS_FOREGROUND)).hex);
        gui.update();
        gui.recorder();
    }
}
