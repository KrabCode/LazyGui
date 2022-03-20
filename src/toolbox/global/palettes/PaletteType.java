package toolbox.global.palettes;

public enum PaletteType {
    DARK,
    LIGHT,
    PINK,
    BLUE,
    CUSTOM;

    public static String[] getAllNames() {
        return new String[]{"dark", "light", "pink", "blue", "custom"};
    }

    public static String[] getAllNamesStartingWithCustom() {
        return new String[]{"custom", "dark", "light", "pink", "blue"};
    }

    public static PaletteType[] getAllValues() {
        return new PaletteType[]{DARK, LIGHT, PINK, BLUE, CUSTOM};
    }

    public static PaletteType getValue(String name) {
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

    public static String getName(PaletteType query) {
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

    public static Palette getPalette(PaletteType query) {
        switch (query) {
            case DARK:
            case CUSTOM: {
                return new Palette(0xFF787878,
                        0xFF0B0B0B,
                        0xFF2F2F2F,
                        0xFFB0B0B0,
                        0xFFFFFFFF);
            }
            case LIGHT: {
                return new Palette(0xFF000000,
                        0xFFD9D9D9,
                        0xFFAFAFAF,
                        0xFF000000,
                        0xFF000000
                );
            }
            case PINK: {
                return new Palette(
                        0xFFfcd3e7,
                        0xFF916b99,
                        0xFF532e6a,
                        0xFFFFFFFF,
                        0xFFFFFFFF
                );
            }
            case BLUE: {
                return new Palette(
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
