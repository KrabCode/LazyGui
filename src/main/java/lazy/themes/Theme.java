package lazy.themes;

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
     * @param windowBorder color of the border lines
     * @param normalBackground background of idle elements
     * @param focusBackground background of currently selected elements
     * @param normalForeground foreground of idle elements
     * @param focusForeground foreground of currently selected elements
     */
    public Theme(int windowBorder, int normalBackground, int focusBackground, int normalForeground, int focusForeground) {
        this.windowBorder = windowBorder;
        this.normalBackground = normalBackground;
        this.focusBackground = focusBackground;
        this.normalForeground = normalForeground;
        this.focusForeground = focusForeground;
    }
}
