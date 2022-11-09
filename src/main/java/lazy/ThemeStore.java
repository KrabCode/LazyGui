package lazy;

import java.util.HashMap;
import java.util.Map;


class ThemeStore {

    private static final Map<ThemeType, Theme> paletteMap = new HashMap<>();
    static ThemeType currentSelection = ThemeType.CUSTOM;

    static void initSingleton() {
        ThemeType[] allTypes = ThemeType.getAllValues();
        for (ThemeType type : allTypes) {
            paletteMap.put(type, ThemeType.getPalette(type));
        }
    }

    static int getColor(ThemeColorType type) {
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

    static void setCustomPalette(Theme theme) {
        paletteMap.put(ThemeType.CUSTOM, theme);
    }

    public static void updateThemePicker(String path) {
            String defaultPaletteName = "dark";
            Theme defaultTheme = ThemeType.getPalette(ThemeType.DARK);
            assert defaultTheme != null;

            String userSelection = State.gui.stringPicker(path + "/theme presets", ThemeType.getAllNames(), defaultPaletteName);
            String editPath = path + "/theme editor/";
            if (!userSelection.equals(ThemeType.getName(ThemeStore.currentSelection))) {
                ThemeType newSelectionToCopy = ThemeType.getValue(userSelection);
                State.gui.colorPickerSet(editPath + "focus foreground", ThemeStore.getColor(ThemeColorType.FOCUS_FOREGROUND, newSelectionToCopy));
                State.gui.colorPickerSet(editPath + "focus background", ThemeStore.getColor(ThemeColorType.FOCUS_BACKGROUND, newSelectionToCopy));
                State.gui.colorPickerSet(editPath + "normal foreground", ThemeStore.getColor(ThemeColorType.NORMAL_FOREGROUND, newSelectionToCopy));
                State.gui.colorPickerSet(editPath + "normal background", ThemeStore.getColor(ThemeColorType.NORMAL_BACKGROUND, newSelectionToCopy));
                State.gui.colorPickerSet(editPath + "window border", ThemeStore.getColor(ThemeColorType.WINDOW_BORDER, newSelectionToCopy));
                ThemeStore.currentSelection = newSelectionToCopy;
            }
            ThemeStore.setCustomColor(ThemeColorType.FOCUS_FOREGROUND,
                    State.gui.colorPicker(editPath + "focus foreground", defaultTheme.focusForeground).hex);
            ThemeStore.setCustomColor(ThemeColorType.FOCUS_BACKGROUND,
                    State.gui.colorPicker(editPath + "focus background", defaultTheme.focusBackground).hex);
            ThemeStore.setCustomColor(ThemeColorType.NORMAL_FOREGROUND,
                    State.gui.colorPicker(editPath + "normal foreground", defaultTheme.normalForeground).hex);
            ThemeStore.setCustomColor(ThemeColorType.NORMAL_BACKGROUND,
                    State.gui.colorPicker(editPath + "normal background", defaultTheme.normalBackground).hex);
            ThemeStore.setCustomColor(ThemeColorType.WINDOW_BORDER,
                    State.gui.colorPicker(editPath + "window border", defaultTheme.windowBorder).hex);

    }
}
