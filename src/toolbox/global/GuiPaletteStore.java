package toolbox.global;


import toolbox.global.themes.GuiPalette;
import toolbox.global.themes.GuiPaletteColorType;
import toolbox.global.themes.GuiPaletteDark;
import toolbox.global.themes.GuiPalettePink;

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
            case windowBorder:
                return palettes.get(currentPaletteIndex).windowBorder;
            case normalBackground:
                return palettes.get(currentPaletteIndex).normalBackground;
            case focusBackground:
                return palettes.get(currentPaletteIndex).focusBackground;
            case normalForeground:
                return palettes.get(currentPaletteIndex).normalForeground;
            case focusForeground:
                return palettes.get(currentPaletteIndex).focusForeground;
        }
        return 0xFFFF0000;
    }

    public static void set(GuiPaletteColorType type, int val) {
        switch(type) {
            case windowBorder:
                palettes.get(currentPaletteIndex).windowBorder = val;
                break;
            case normalBackground:
                palettes.get(currentPaletteIndex).normalBackground = val;
                break;
            case focusBackground:
                palettes.get(currentPaletteIndex).focusBackground = val;
                break;
            case normalForeground:
                palettes.get(currentPaletteIndex).normalForeground = val;
                break;
            case focusForeground:
                palettes.get(currentPaletteIndex).focusForeground = val;
                break;
        }
    }
}
