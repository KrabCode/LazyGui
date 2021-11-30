package test;

import processing.core.PApplet;
import toolbox.Gui;
import toolbox.global.PaletteStore;

import java.util.ArrayList;

import static toolbox.global.palettes.PaletteColorType.*;

public class PaletteEditor extends PApplet {
    Gui gui;


    public static void main(String[] args) {
        PApplet.main("test.PaletteEditor");
    }


    @Override
    public void settings() {
        size(1000,1000,P2D);
//        size(600, 600, P2D);
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
            PaletteStore.setPaletteIndex(0);
            switchPalette = true;
        }else if(gui.button("palette/> pink")){
            PaletteStore.setPaletteIndex(1);
            switchPalette = true;
        }else if(gui.button("palette/> blue")){
            PaletteStore.setPaletteIndex(2);
            switchPalette = true;
        }
        if(switchPalette){
            gui.colorPickerSet("palette/window border"    ,  PaletteStore.get(WINDOW_BORDER));
            gui.colorPickerSet("palette/normal background",  PaletteStore.get(NORMAL_BACKGROUND));
            gui.colorPickerSet("palette/focus background" ,  PaletteStore.get(FOCUS_BACKGROUND));
            gui.colorPickerSet("palette/normal foreground",  PaletteStore.get(NORMAL_FOREGROUND));
            gui.colorPickerSet("palette/focus foreground" ,  PaletteStore.get(FOCUS_FOREGROUND));
        }
        PaletteStore.set(WINDOW_BORDER, gui.colorPicker    ("palette/window border"       , PaletteStore.get(WINDOW_BORDER)).hex);
        PaletteStore.set(NORMAL_BACKGROUND, gui.colorPicker("palette/normal background"   , PaletteStore.get(NORMAL_BACKGROUND)).hex);
        PaletteStore.set(FOCUS_BACKGROUND, gui.colorPicker( "palette/focus background"    , PaletteStore.get(FOCUS_BACKGROUND)).hex);
        PaletteStore.set(NORMAL_FOREGROUND, gui.colorPicker("palette/normal foreground"   , PaletteStore.get(NORMAL_FOREGROUND)).hex);
        PaletteStore.set(FOCUS_FOREGROUND, gui.colorPicker( "palette/focus foreground"    , PaletteStore.get(FOCUS_FOREGROUND)).hex);
        gui.update();
        gui.recorder();
    }
}
