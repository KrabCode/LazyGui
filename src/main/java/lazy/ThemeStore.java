package lazy;

import java.util.HashMap;
import java.util.Map;


class ThemeStore {

    private static final Map<ThemeType, Theme> paletteMap = new HashMap<>();
    static ThemeType currentSelection = ThemeType.DARK;

    static void initSingleton() {
        ThemeType[] allTypes = ThemeType.getAllValues();
        for (ThemeType type : allTypes) {
            paletteMap.put(type, ThemeType.getPalette(type));
        }
    }

    static int getColor(ThemeColorType type) {
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

    static void setCustomColor(ThemeColorType type, int val) {
        switch (type) {
            case WINDOW_BORDER:
                paletteMap.get(ThemeType.CUSTOM).windowBorder = val;
                break;
            case NORMAL_BACKGROUND:
                paletteMap.get(ThemeType.CUSTOM).normalBackground = val;
                break;
            case FOCUS_BACKGROUND:
                paletteMap.get(ThemeType.CUSTOM).focusBackground = val;
                break;
            case NORMAL_FOREGROUND:
                paletteMap.get(ThemeType.CUSTOM).normalForeground = val;
                break;
            case FOCUS_FOREGROUND:
                paletteMap.get(ThemeType.CUSTOM).focusForeground = val;
                break;
        }
    }

    static void setCustomPalette(Theme theme) {
        paletteMap.put(ThemeType.CUSTOM, theme);
    }
}
