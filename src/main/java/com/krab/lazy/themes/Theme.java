package com.krab.lazy.themes;

/**
 * Data container for individual theme colors.
 */
public class Theme {
    int windowBorder;
    int normalBackground;
    int focusBackground;
    int normalForeground;
    int focusForeground;

    @SuppressWarnings("unused")
    private Theme(){

    }

    /**
     * The only available constructor for this class.
     * Enforces specifying all the available values as parameters.
     *
     * @param windowBorderColor color of the border lines
     * @param normalBackgroundColor background of idle elements
     * @param focusBackgroundColor background of currently selected elements
     * @param normalForegroundColor foreground of idle elements
     * @param focusForegroundColor foreground of currently selected elements
     */
    public Theme(int windowBorderColor, int normalBackgroundColor, int focusBackgroundColor, int normalForegroundColor, int focusForegroundColor) {
        this.windowBorder = windowBorderColor;
        this.normalBackground = normalBackgroundColor;
        this.focusBackground = focusBackgroundColor;
        this.normalForeground = normalForegroundColor;
        this.focusForeground = focusForegroundColor;
    }
}
