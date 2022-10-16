package lazy;

class Theme {
    int windowBorder;
    int normalBackground;
    int focusBackground;
    int normalForeground;
    int focusForeground;

    private Theme(){

    }

    Theme(int windowBorder, int normalBackground, int focusBackground, int normalForeground, int focusForeground) {
        this.windowBorder = windowBorder;
        this.normalBackground = normalBackground;
        this.focusBackground = focusBackground;
        this.normalForeground = normalForeground;
        this.focusForeground = focusForeground;
    }
}
