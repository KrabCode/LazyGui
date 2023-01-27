package lazy.stores;

import static lazy.stores.GlobalReferences.gui;

public class LayoutStore {
    public static float cell = 22; // cell size but shorter because used everywhere
    public static final float defaultWindowWidthInCells = 10;
    private static float resizeRectangleSize = 4;
    private static boolean shouldKeepWindowsInBounds = true;
    private static boolean isWindowResizeEnabled = true;
    private static boolean shouldDrawResizeIndicator = true;
    private static boolean showHorizontalSeparators = false;
    private static float horizontalSeparatorStrokeWeight = 1;

    public static void setCellSize(float inputCellSize) {
        cell = inputCellSize;
    }

    public static void setShouldKeepWindowsInBounds(boolean valueToSet) {
        shouldKeepWindowsInBounds = valueToSet;
    }

    public static boolean getShouldKeepWindowsInBounds() {
        return shouldKeepWindowsInBounds;
    }

    public static void setWindowResizeEnabled(boolean valueToSet) {
        isWindowResizeEnabled = valueToSet;
    }

    public static boolean getWindowResizeEnabled() {
        return isWindowResizeEnabled;
    }

    public static boolean getShouldDrawResizeIndicator() {
        return shouldDrawResizeIndicator;
    }

    public static void setShouldDrawResizeIndicator(boolean valueToSet){
        shouldDrawResizeIndicator = valueToSet;
    }

    public static float getResizeRectangleSize(){
        return resizeRectangleSize;
    }

    public static void setResizeRectangleSize(float valueToSet){
        resizeRectangleSize = valueToSet;
    }

    public static boolean isShowHorizontalSeparators() {
        return showHorizontalSeparators;
    }

    public static void setShowHorizontalSeparators(boolean showHorizontalSeparators) {
        LayoutStore.showHorizontalSeparators = showHorizontalSeparators;
    }

    public static float getHorizontalSeparatorStrokeWeight() {
        return horizontalSeparatorStrokeWeight;
    }

    public static void setHorizontalSeparatorStrokeWeight(float horizontalSeparatorStrokeWeight) {
        LayoutStore.horizontalSeparatorStrokeWeight = horizontalSeparatorStrokeWeight;
    }

    public static void updateSeparators() {
        gui.pushFolder("separators");
        setShowHorizontalSeparators(gui.toggle("show"));
        setHorizontalSeparatorStrokeWeight(gui.slider("weight", 0.5f));
        gui.popFolder();
    }
}
