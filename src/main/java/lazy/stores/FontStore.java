package lazy.stores;

import processing.core.PFont;
import processing.core.PGraphics;

import static lazy.stores.GlobalReferences.app;
import static lazy.stores.GlobalReferences.gui;
import static processing.core.PApplet.main;
import static processing.core.PApplet.println;

public class FontStore {

    public static float textMarginX = 5;
    public static float textMarginY = 13;

    private static final int defaultFontSize = 16;
    private static int lastFontSize = -1;
    private static PFont mainFont = null;
    private static PFont sideFont = null;
    private final static String mainFontPathDefault = "JetBrainsMono-2.242/fonts/ttf/JetBrainsMono-Regular.ttf";
    private final static String sideFontPathDefault = "JetBrainsMono-2.242/fonts/ttf/JetBrainsMono-Light.ttf";
    private static String lastMainFontPath = "";
    private static String lastSideFontPath = "";

    public static void updateFontOptions() {
        gui.pushFolder("font");
        lazyUpdateFont(
                gui.textInput("side font path", mainFontPathDefault),
                gui.textInput("main font path", sideFontPathDefault),
                gui.sliderInt("font size", getLastFontSize(), 1, Integer.MAX_VALUE)
        );
        textMarginX = gui.slider("font x offset", textMarginX);
        textMarginY = gui.slider("font y offset", textMarginY);
        if(gui.button("print fonts")){
            printAvailableFonts();
        }
        gui.popFolder();
    }

    public static PFont getMainFont() {
        return mainFont;
    }

    public static PFont getSideFont() {
        return sideFont;
    }

    public static void lazyUpdateFont() {
        lazyUpdateFont(mainFontPathDefault, sideFontPathDefault, defaultFontSize);
    }

    private static void lazyUpdateFont(String _mainFontPath, String _sideFontPath, int _fontSize) {
        boolean mainFontPathChanged = !lastMainFontPath.equals(_mainFontPath);
        boolean sideFontPathChanged = !lastSideFontPath.equals(_sideFontPath);
        boolean sizeChanged = lastFontSize != _fontSize;
        lastFontSize = _fontSize;
        if(sizeChanged || mainFontPathChanged){
            lastMainFontPath = _mainFontPath;
            try {
                println("main font rebuilt at " + lastFontSize);
                mainFont = app.createFont(lastMainFontPath, lastFontSize);
            } catch (RuntimeException ex) {
                if (ex.getMessage().contains("createFont() can only be used inside setup() or after setup() has been called")) {
                    throw new RuntimeException("the new Gui(this) constructor can only be used inside setup() or after setup() has been called");
                }
            }
        }
        if(sizeChanged || sideFontPathChanged){
            lastSideFontPath = _sideFontPath;
            try {
                println("side font rebuilt at " + lastFontSize);
                sideFont = app.createFont(lastSideFontPath, lastFontSize);
            } catch (RuntimeException ex) {
                if (ex.getMessage().contains("createFont() can only be used inside setup() or after setup() has been called")) {
                    throw new RuntimeException("the new Gui(this) constructor can only be used inside setup() or after setup() has been called");
                }
            }
        }
    }

    private static int getLastFontSize() {
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
