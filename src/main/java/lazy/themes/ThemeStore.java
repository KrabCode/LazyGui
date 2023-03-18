package lazy.themes;

import java.util.HashMap;
import java.util.Map;

import static lazy.stores.GlobalReferences.gui;


public class ThemeStore {
    private static final Map<ThemeType, Theme> paletteMap = new HashMap<>();
    public static ThemeType currentSelection = ThemeType.DARK;
    private static String defaultThemeType = currentSelection.name();

    public static void setCustomPaletteAndMakeDefaultBeforeInit(Theme theme) {
        defaultThemeType = ThemeType.getName(ThemeType.CUSTOM);
        paletteMap.put(ThemeType.CUSTOM, theme);
    }
    public static void selectThemeByTypeBeforeInit(ThemeType typeToSelect){
        defaultThemeType = ThemeType.getName(typeToSelect);
    }

    public static void init() {
        ThemeType[] allTypes = ThemeType.getAllValues();
        for (ThemeType type : allTypes) {
            if(!paletteMap.containsKey(type)){
                paletteMap.put(type, ThemeType.getPalette(type));
            }
        }
    }

    public static int getColor(ThemeColorType type) {
        switch (type) {
            case WINDOW_BORDER:
                return paletteMap.get(ThemeType.CUSTOM).windowBorder;
            case NORMAL_BACKGROUND:
                return paletteMap.get(ThemeType.CUSTOM).normalBackground;
            case FOCUS_BACKGROUND:
                return paletteMap.get(ThemeType.CUSTOM).focusBackground;
            case NORMAL_FOREGROUND:
                return paletteMap.get(ThemeType.CUSTOM).normalForeground;
            case FOCUS_FOREGROUND:
                return paletteMap.get(ThemeType.CUSTOM).focusForeground;
        }
        return 0xFFFF0000;
    }

    static int getColor(ThemeColorType colorType, ThemeType paletteType) {
        switch (colorType) {
            case WINDOW_BORDER:
                return paletteMap.get(paletteType).windowBorder;
            case NORMAL_BACKGROUND:
                return paletteMap.get(paletteType).normalBackground;
            case FOCUS_BACKGROUND:
                return paletteMap.get(paletteType).focusBackground;
            case NORMAL_FOREGROUND:
                return paletteMap.get(paletteType).normalForeground;
            case FOCUS_FOREGROUND:
                return paletteMap.get(paletteType).focusForeground;
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

    public static void updateThemePicker() {
            gui.pushFolder("themes");
            String userSelection = gui.radio("preset", ThemeType.getAllNames(), defaultThemeType);
            gui.pushFolder("editor");
            if (!userSelection.equals(ThemeType.getName(ThemeStore.currentSelection))) {
                ThemeType newSelectionToCopy = ThemeType.getValue(userSelection);
                gui.colorPickerSet("focus foreground", ThemeStore.getColor(ThemeColorType.FOCUS_FOREGROUND, newSelectionToCopy));
                gui.colorPickerSet("focus background", ThemeStore.getColor(ThemeColorType.FOCUS_BACKGROUND, newSelectionToCopy));
                gui.colorPickerSet("normal foreground", ThemeStore.getColor(ThemeColorType.NORMAL_FOREGROUND, newSelectionToCopy));
                gui.colorPickerSet("normal background", ThemeStore.getColor(ThemeColorType.NORMAL_BACKGROUND, newSelectionToCopy));
                gui.colorPickerSet("window border", ThemeStore.getColor(ThemeColorType.WINDOW_BORDER, newSelectionToCopy));
                ThemeStore.currentSelection = newSelectionToCopy;
            }
            assert currentSelection != null;
            Theme defaultTheme = ThemeType.getPalette(currentSelection);
            assert defaultTheme != null;
            ThemeStore.setCustomColor(ThemeColorType.FOCUS_FOREGROUND,
                    gui.colorPicker("focus foreground", defaultTheme.focusForeground).hex);
            ThemeStore.setCustomColor(ThemeColorType.FOCUS_BACKGROUND,
                    gui.colorPicker("focus background", defaultTheme.focusBackground).hex);
            ThemeStore.setCustomColor(ThemeColorType.NORMAL_FOREGROUND,
                    gui.colorPicker("normal foreground", defaultTheme.normalForeground).hex);
            ThemeStore.setCustomColor(ThemeColorType.NORMAL_BACKGROUND,
                    gui.colorPicker("normal background", defaultTheme.normalBackground).hex);
            ThemeStore.setCustomColor(ThemeColorType.WINDOW_BORDER,
                    gui.colorPicker("window border", defaultTheme.windowBorder).hex);
            gui.popFolder();
            gui.popFolder();
    }
}
