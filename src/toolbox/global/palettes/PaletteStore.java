package toolbox.global.palettes;

import java.util.HashMap;
import java.util.Map;


public class PaletteStore {

    private static final Map<PaletteType, Palette> paletteMap = new HashMap<>();
    public static PaletteType currentSelection = PaletteType.DARK;

    public static void initSingleton() {
        PaletteType[] allTypes = PaletteType.getAllValues();
        for (PaletteType type : allTypes) {
            paletteMap.put(type, PaletteType.getPalette(type));
        }
    }

    public static int getColor(PaletteColorType type) {
        switch (type) {
            case WINDOW_BORDER:
                return paletteMap.get(currentSelection).windowBorder;
            case NORMAL_BACKGROUND:
                return paletteMap.get(currentSelection).normalBackground;
            case FOCUS_BACKGROUND:
                return paletteMap.get(currentSelection).focusBackground;
            case NORMAL_FOREGROUND:
                return paletteMap.get(currentSelection).normalForeground;
            case FOCUS_FOREGROUND:
                return paletteMap.get(currentSelection).focusForeground;
        }
        return 0xFFFF0000;
    }

    public static void setCustomColor(PaletteColorType type, int val) {
        switch (type) {
            case WINDOW_BORDER:
                paletteMap.get(PaletteType.CUSTOM).windowBorder = val;
                break;
            case NORMAL_BACKGROUND:
                paletteMap.get(PaletteType.CUSTOM).normalBackground = val;
                break;
            case FOCUS_BACKGROUND:
                paletteMap.get(PaletteType.CUSTOM).focusBackground = val;
                break;
            case NORMAL_FOREGROUND:
                paletteMap.get(PaletteType.CUSTOM).normalForeground = val;
                break;
            case FOCUS_FOREGROUND:
                paletteMap.get(PaletteType.CUSTOM).focusForeground = val;
                break;
        }
    }

    public static void setCustomPalette(Palette palette) {
        paletteMap.put(PaletteType.CUSTOM, palette);
    }
}
