package com.krab.lazy.stores;

import processing.core.PFont;
import processing.core.PGraphics;

import static processing.core.PApplet.println;

public class FontStore {

    public static float textMarginX = 5;

    public final static String sideFontPathDefault = "JetBrainsMono-Regular.ttf";
    public final static String mainFontPathDefault = "JetBrainsMono-Regular.ttf";
    public static int mainFontSizeDefault = 16;
    public static int sideFontSizeDefault = 15;
    private static String lastMainFontPath = "";
    private static String lastSideFontPath = "";
    private static int lastMainFontSize = -1;
    private static int lastSideFontSize = -1;
    private static PFont mainFont = null;
    private static PFont sideFont = null;

    private static PGraphics mainFontUtilsProvider;

    public static PGraphics getMainFontUtilsProvider(){
        if(mainFontUtilsProvider == null){
            mainFontUtilsProvider = GlobalReferences.app.createGraphics(64, 64);
        }
        mainFontUtilsProvider.beginDraw();
        mainFontUtilsProvider.textFont(mainFont);
        mainFontUtilsProvider.endDraw();
        return mainFontUtilsProvider;
    }

    public static void updateFontOptions() {
        GlobalReferences.gui.pushFolder("font");
        lazyUpdateFont(
                GlobalReferences.gui.text("main font", mainFontPathDefault),
                GlobalReferences.gui.text("side font", sideFontPathDefault),
                GlobalReferences.gui.sliderInt("main font size", mainFontSizeDefault, 2, Integer.MAX_VALUE),
                GlobalReferences.gui.sliderInt("side font size", sideFontSizeDefault, 2, Integer.MAX_VALUE)
        );
        textMarginX = GlobalReferences.gui.slider("x offset", textMarginX);
        if(GlobalReferences.gui.button("print font list")){
            printAvailableFonts();
        }
        GlobalReferences.gui.popFolder();
    }

    /**
     * Processing's textAlign(_, CENTER) positions text using only textAscent(), which leaves it a few pixels too low
     * and only looks right at the default font size. Deriving the baseline from both textAscent() and textDescent()
     * keeps text visually centered around rowCenterY at any font size, without a hardcoded pixel offset.
     * Use with textAlign(_, BASELINE).
     * See https://github.com/processing/processing4/issues/739 and https://processing.org/reference/text_
     */
    public static float getCenteredTextBaselineY(PGraphics pg, float rowCenterY) {
        return rowCenterY + (pg.textAscent() - pg.textDescent()) * 0.5f;
    }

    public static PFont getMainFont() {
        return mainFont;
    }

    public static PFont getSideFont() {
        return sideFont;
    }

    public static void lazyUpdateFont() {
        lazyUpdateFont(mainFontPathDefault, sideFontPathDefault, mainFontSizeDefault, sideFontSizeDefault);
    }

    private static void lazyUpdateFont(String _mainFontPath, String _sideFontPath, int _mainFontSize, int _sideFontSize) {
        boolean mainFontPathChanged = !lastMainFontPath.equals(_mainFontPath);
        boolean sideFontPathChanged = !lastSideFontPath.equals(_sideFontPath);
        boolean mainSizeChanged = lastMainFontSize != _mainFontSize;
        boolean sideSizeChanged = lastSideFontSize != _sideFontSize;
        lastMainFontSize = _mainFontSize;
        lastSideFontSize = _sideFontSize;
        if(mainSizeChanged || mainFontPathChanged){
            lastMainFontPath = _mainFontPath;
            try {
                mainFont = GlobalReferences.app.createFont(lastMainFontPath, lastMainFontSize);
            } catch (RuntimeException ex) {
                if (ex.getMessage().contains("createFont() can only be used inside setup() or after setup() has been called")) {
                    throw new RuntimeException("the new Gui(this) constructor can only be used inside setup() or after setup() has been called");
                }
            }
        }
        if(sideSizeChanged || sideFontPathChanged){
            lastSideFontPath = _sideFontPath;
            try {
                sideFont = GlobalReferences.app.createFont(lastSideFontPath, lastSideFontSize);
            } catch (RuntimeException ex) {
                if (ex.getMessage().contains("createFont() can only be used inside setup() or after setup() has been called")) {
                    throw new RuntimeException("the new Gui(this) constructor can only be used inside setup() or after setup() has been called");
                }
            }
        }
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
            if(character == '\n'){
                // no new lines allowed in a one-line row name
                break;
            }
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
