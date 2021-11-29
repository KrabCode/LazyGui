package toolbox.global;


import toolbox.global.palettes.*;

import java.util.ArrayList;

import static processing.core.PApplet.constrain;

public class PaletteStore extends GuiPalette {
    private static ArrayList<GuiPalette> palettes = new ArrayList<>();
    private static int currentPaletteIndex = 0;

    private PaletteStore(){

    }

    public static void initSingleton(){
        palettes.add(new GuiPaletteDark());
        palettes.add(new GuiPalettePink());
        palettes.add(new GuiPaletteBlue());
    }

    public static void setPaletteIndex(int index){
        currentPaletteIndex = index;
        currentPaletteIndex = constrain(currentPaletteIndex, 0, palettes.size()-1);
    }

    public static int paletteCount(){
        return palettes.size();
    }

    public static int get(PaletteColorType type){
        switch(type) {
            case WINDOW_BORDER:
                return palettes.get(currentPaletteIndex).windowBorder;
            case NORMAL_BACKGROUND:
                return palettes.get(currentPaletteIndex).normalBackground;
            case FOCUS_BACKGROUND:
                return palettes.get(currentPaletteIndex).focusBackground;
            case NORMAL_FOREGROUND:
                return palettes.get(currentPaletteIndex).normalForeground;
            case FOCUS_FOREGROUND:
                return palettes.get(currentPaletteIndex).focusForeground;
        }
        return 0xFFFF0000;
    }

    public static void set(PaletteColorType type, int val) {
        switch(type) {
            case WINDOW_BORDER:
                palettes.get(currentPaletteIndex).windowBorder = val;
                break;
            case NORMAL_BACKGROUND:
                palettes.get(currentPaletteIndex).normalBackground = val;
                break;
            case FOCUS_BACKGROUND:
                palettes.get(currentPaletteIndex).focusBackground = val;
                break;
            case NORMAL_FOREGROUND:
                palettes.get(currentPaletteIndex).normalForeground = val;
                break;
            case FOCUS_FOREGROUND:
                palettes.get(currentPaletteIndex).focusForeground = val;
                break;
        }
    }
}
