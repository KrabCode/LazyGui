package lazy.stores;

import processing.core.PFont;
import processing.core.PGraphics;

import static lazy.stores.GlobalReferences.app;
import static processing.core.PApplet.println;

public class FontStore {

    static final int defaultFontSize = 16;
    static private int lastFontSize = -1;
    private static final String fontPath = "JetBrainsMono-Regular.ttf";
    static PFont font = null;

    public static float textMarginX = 5;
    public static float textMarginY = 14;

    public static PFont getFont() {
        return font;
    }

    public static void tryUpdateFont() {
        tryUpdateFont(defaultFontSize, textMarginX, textMarginY);
    }

    public static void tryUpdateFont(int _fontSize, float _textMarginX, float _textMarginY) {
        textMarginX = _textMarginX;
        textMarginY = _textMarginY;
        if (_fontSize == lastFontSize) {
            return;
        }
        try {
            font = app.createFont(fontPath, _fontSize);
        } catch (RuntimeException ex) {
            if (ex.getMessage().contains("createFont() can only be used inside setup() or after setup() has been called")) {
                throw new RuntimeException("the new Gui(this) constructor can only be used inside setup() or after setup() has been called");
            }
        }
        lastFontSize = _fontSize;
    }

    public static int getLastFontSize() {
        return lastFontSize;
    }


    @SuppressWarnings("unused")
    private static void printAvailableFonts() {
        String[] fontList = PFont.list();
        for (String s :
                fontList) {
            println(s);
        }
    }

    public static String getSubstringFromStartToFit(PGraphics pg, String text, float availableWidth) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            float textWidthAfterNewChar = pg.textWidth(result.toString() + character);
            if (textWidthAfterNewChar >= availableWidth) {
                break;
            }
            result.append(character);
        }
        return result.toString();
    }

    public static String getSubstringFromEndToFit(PGraphics pg, String text, float availableWidth){
        StringBuilder result = new StringBuilder();
        for (int i = text.length() - 1; i >= 0; i--) {
            char character = text.charAt(i);
            float textWidthAfterNewChar = pg.textWidth(result.toString() + character);
            if (textWidthAfterNewChar >= availableWidth) {
                break;
            }
            result.insert(0, character);
        }
        return result.toString();
    }
}
