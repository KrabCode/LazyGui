package toolbox.global;


import toolbox.global.palettes.GuiPalette;
import toolbox.global.palettes.GuiPaletteColorType;
import toolbox.global.palettes.GuiPaletteDark;
import toolbox.global.palettes.GuiPalettePink;

import java.util.ArrayList;

public class GuiPaletteStore extends GuiPalette {
    private static ArrayList<GuiPalette> palettes = new ArrayList<>();

    static int currentPaletteIndex = 0;
    private static GuiPalette singleton;

    private GuiPaletteStore(){

    }

    public static void initSingleton(){
        singleton = new GuiPaletteStore();
        palettes.add(new GuiPaletteDark());
        palettes.add(new GuiPalettePink());
    }

    public static void nextPalette(){
        currentPaletteIndex++;
        currentPaletteIndex %= palettes.size();
    }

    public static int get(GuiPaletteColorType type){
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

    public static void set(GuiPaletteColorType type, int val) {
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
