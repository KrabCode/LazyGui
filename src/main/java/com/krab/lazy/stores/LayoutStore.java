package com.krab.lazy.stores;

import static com.krab.lazy.stores.GlobalReferences.gui;
import static processing.core.PApplet.floor;

public class LayoutStore {
    public static float cell = 22; // cell size but shorter because used everywhere
    public static final float defaultWindowWidthInCells = 10;
    private static boolean shouldFolderRowClickCloseWindowIfOpen;
    private static float resizeRectangleSize = 4;
    private static boolean shouldKeepWindowsInBounds = true;
    private static boolean isWindowResizeEnabled = true;
    private static boolean shouldDrawResizeIndicator = true;
    private static boolean showHorizontalSeparators = false;
    private static float horizontalSeparatorStrokeWeight = 1;
    private static boolean showPathTooltips = false;
    private static boolean isGuiHidden = false;
    private static boolean autosuggestWindowWidth = true;
    private static boolean hideRadioValue = false;
    private static int smoothingValue = 4;
    private static String overridingSketchName = null;
    private static boolean displaySquigglyEquals = false;

    public static void updateWindowOptions() {
        gui.pushFolder("windows");
        setCellSize(gui.sliderInt("cell size", floor(cell), 12, Integer.MAX_VALUE));
        setShowPathTooltips(gui.toggle("show path tooltips", LayoutStore.getShowPathTooltips()));
        setShouldKeepWindowsInBounds(gui.toggle("keep in bounds", LayoutStore.getShouldKeepWindowsInBounds()));
        setAutosuggestWindowWidth(gui.toggle("autosuggest width", LayoutStore.getAutosuggestWindowWidth()));
        setFolderRowClickClosesWindowIfOpen(gui.toggle("click closes window", doesFolderRowClickCloseWindowIfOpen()));

        gui.pushFolder("resize");
        setWindowResizeEnabled(gui.toggle("allow resize", LayoutStore.getWindowResizeEnabled()));
        setShouldDrawResizeIndicator(gui.toggle("show handle", LayoutStore.getShouldDrawResizeIndicator()));
        setResizeRectangleSize(gui.slider("handle width", LayoutStore.getResizeRectangleSize()));
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

    public static void setShouldDrawResizeIndicator(boolean valueToSet) {
        shouldDrawResizeIndicator = valueToSet;
    }

    public static float getResizeRectangleSize() {
        return resizeRectangleSize;
    }

    public static void setResizeRectangleSize(float valueToSet) {
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

    public static void setIsGuiHidden(boolean shouldGuiBeHidden){
        isGuiHidden = shouldGuiBeHidden;
    }

    public static boolean isGuiHidden() {
        return isGuiHidden;
    }

    public static void hideGuiToggle(){
        isGuiHidden = !isGuiHidden;
    }

    public static boolean getAutosuggestWindowWidth() {
        return autosuggestWindowWidth;
    }

    public static void setAutosuggestWindowWidth(boolean autosuggestWindowWidth) {
        LayoutStore.autosuggestWindowWidth = autosuggestWindowWidth;
    }

    public static String getOverridingSketchName() {
        return overridingSketchName;
    }

    public static void setOverridingSketchName(String overridingSketchName) {
        LayoutStore.overridingSketchName = overridingSketchName;
    }

    public static boolean doesFolderRowClickCloseWindowIfOpen() {
        return shouldFolderRowClickCloseWindowIfOpen;
    }

    public static void setFolderRowClickClosesWindowIfOpen(boolean folderClickTogglesWindow) {
        LayoutStore.shouldFolderRowClickCloseWindowIfOpen = folderClickTogglesWindow;
    }

    public static int getSmoothingValue() {
        return smoothingValue;
    }

    public static void setSmoothingValue(int smoothingValue) {
        LayoutStore.smoothingValue = smoothingValue;
    }

    public static void setHideRadioValue(boolean hideRadioValue) {
        LayoutStore.hideRadioValue = hideRadioValue;
    }

    public static boolean shouldHideRadioValue() {
        return hideRadioValue;
    }

    public static void setDisplaySquigglyEquals(boolean displaySquigglyEquals) {
        LayoutStore.displaySquigglyEquals = displaySquigglyEquals;
    }

    public static boolean shouldDisplaySquigglyEquals() {
        return displaySquigglyEquals;
    }
}
