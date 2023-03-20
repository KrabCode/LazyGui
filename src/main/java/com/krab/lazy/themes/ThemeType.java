package com.krab.lazy.themes;

/**
 * Shorthands for some pre-defined themes with a custom option.
 */
public enum ThemeType {
    DARK,
    LIGHT,
    PINK,
    BLUE,
    CUSTOM;

    static String[] getAllNames() {
        return new String[]{"dark", "light", "pink", "blue", "custom"};
    }

    static ThemeType[] getAllValues() {
        return new ThemeType[]{DARK, LIGHT, PINK, BLUE, CUSTOM};
    }

    public static ThemeType getValue(String name) {
        switch (name) {
            case "dark": {
                return DARK;
            }
            case "light": {
                return LIGHT;
            }
            case "pink": {
                return PINK;
            }
            case "blue": {
                return BLUE;
            }
            case "custom": {
                return CUSTOM;
            }
        }
        return null;
    }

    static String getName(ThemeType query) {
        switch (query) {
            case DARK: {
                return "dark";
            }
            case LIGHT: {
                return "light";
            }
            case PINK: {
                return "pink";
            }
            case BLUE: {
                return "blue";
            }
            case CUSTOM: {
                return "custom";
            }
        }
        return null;
    }

    static Theme getPalette(ThemeType query) {
        switch (query) {
            case DARK:
            case CUSTOM: {
                return new Theme(0xFF787878,
                        0xFF0B0B0B,
                        0xFF2F2F2F,
                        0xFFB0B0B0,
                        0xFFFFFFFF);
            }
            case LIGHT: {
                return new Theme(0xFF000000,
                        0xFFD9D9D9,
                        0xFFAFAFAF,
                        0xFF000000,
                        0xFF000000
                );
            }
            case PINK: {
                return new Theme(
                        0xFFfcd3e7,
                        0xFF916b99,
                        0xFF532e6a,
                        0xFFFFFFFF,
                        0xFFFFFFFF
                );
            }
            case BLUE: {
                return new Theme(
                        0xFF000000,
                        0xFFc9d1ef,
                        0xFF6271c4,
                        0xFF000000,
                        0xFFFFFFFF
                );
            }
        }
        return null;
    }
}
