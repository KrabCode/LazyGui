package lazy;

import processing.core.PApplet;
import processing.core.PGraphics;

import static processing.core.PApplet.*;

class State {
    static float cell = 22; // cell size but shorter because used everywhere
    static float previewRectSize = cell * 0.6f;
    static int keyboardInputAppendCooldownMillis = 500;
    static PApplet app = null;
    static LazyGui gui = null;
    static final float defaultWindowWidthInCells = 10;
    private static float resizeRectangleSize = 4;
    private static boolean shouldKeepWindowsInBounds = true;
    private static boolean isWindowResizeEnabled = true;
    private static boolean shouldDrawResizeIndicator = true;
    private static PGraphics colorStore = null;

    static void init(LazyGui gui, PApplet app) {
        State.gui = gui;
        State.app = app;
        colorStore = app.createGraphics(256, 256, P2D);
        colorStore.colorMode(HSB, 1, 1, 1, 1);
    }

    public static void setCellSize(float inputCellSize) {
        cell = inputCellSize;
        previewRectSize = cell * 0.6f;
    }

    static int normColor(float br) {
        return normColor(0, 0, br, 1);
    }

    @SuppressWarnings("SameParameterValue")
    static int normColor(float br, float alpha) {
        return normColor(0, 0, br, alpha);
    }

    @SuppressWarnings("unused")
    static int normColor(float hue, float sat, float br) {
        return normColor(hue, sat, br, 1);
    }

    static int normColor(float hue, float sat, float br, float alpha) {
        return colorStore.color(hue, sat, br, alpha);
    }

    static float red(int hex){
        return colorStore.red(hex);
    }

    static float green(int hex){
        return colorStore.green(hex);
    }

    static float blue(int hex){
        return colorStore.blue(hex);
    }

    public static PGraphics getColorStore() {
        return colorStore;
    }

    static void setShouldKeepWindowsInBounds(boolean valueToSet) {
        shouldKeepWindowsInBounds = valueToSet;
    }

    static boolean getShouldKeepWindowsInBounds() {
        return shouldKeepWindowsInBounds;
    }

    static void setWindowResizeEnabled(boolean valueToSet) {
        isWindowResizeEnabled = valueToSet;
    }

    static boolean getWindowResizeEnabled() {
        return isWindowResizeEnabled;
    }

    static boolean getShouldDrawResizeIndicator() {
        return shouldDrawResizeIndicator;
    }

    static void setShouldDrawResizeIndicator(boolean valueToSet){
        shouldDrawResizeIndicator = valueToSet;
    }

    static float getResizeRectangleSize(){
        return resizeRectangleSize;
    }

    static void setResizeRectangleSize(float valueToSet){
        resizeRectangleSize = valueToSet;
    }
}
