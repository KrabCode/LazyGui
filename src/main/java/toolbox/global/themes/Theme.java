package toolbox.global.themes;

public class Theme {
    public int windowBorder;
    public int normalBackground;
    public int focusBackground;
    public int normalForeground;
    public int focusForeground;

    private Theme(){

    }

    public Theme(int windowBorder, int normalBackground, int focusBackground, int normalForeground, int focusForeground) {
        this.windowBorder = windowBorder;
        this.normalBackground = normalBackground;
        this.focusBackground = focusBackground;
        this.normalForeground = normalForeground;
        this.focusForeground = focusForeground;
    }
}
