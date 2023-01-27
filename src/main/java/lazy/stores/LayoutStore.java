package lazy.stores;

import static lazy.stores.GlobalReferences.gui;
import static processing.core.PApplet.floor;

public class LayoutStore {
    public static float cell = 22; // cell size but shorter because used everywhere
    public static final float defaultWindowWidthInCells = 10;
    private static float resizeRectangleSize = 4;
    private static boolean shouldKeepWindowsInBounds = true;
    private static boolean isWindowResizeEnabled = true;
    private static boolean shouldDrawResizeIndicator = true;
    private static boolean showHorizontalSeparators = false;
    private static float horizontalSeparatorStrokeWeight = 1;

    private static boolean showPathTooltips = false;

    public static void updateWindowOptions() {
        gui.pushFolder("windows");
        LayoutStore.setCellSize(gui.sliderInt("cell size", floor(cell), 12, Integer.MAX_VALUE));
        LayoutStore.setShowPathTooltips(gui.toggle("show path tooltips", LayoutStore.getShowPathTooltips()));
        LayoutStore.setShouldKeepWindowsInBounds(gui.toggle("keep in bounds", LayoutStore.getShouldKeepWindowsInBounds()));
        gui.pushFolder("resize");
        LayoutStore.setWindowResizeEnabled(gui.toggle("allow resize", LayoutStore.getWindowResizeEnabled()));
        LayoutStore.setShouldDrawResizeIndicator(gui.toggle("show handle", LayoutStore.getShouldDrawResizeIndicator()));
        LayoutStore.setResizeRectangleSize(gui.slider("handle width", LayoutStore.getResizeRectangleSize()));
        gui.popFolder();
        gui.pushFolder("separators");
        setShowHorizontalSeparators(gui.toggle("show"));
        setHorizontalSeparatorStrokeWeight(gui.slider("weight", 0.5f));
        gui.popFolder();
        gui.popFolder();
    }

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

    public static boolean getShowPathTooltips() {
        return showPathTooltips;
    }

    public static void setShowPathTooltips(boolean showPathTooltips) {
        LayoutStore.showPathTooltips = showPathTooltips;
    }

}
