package lazy;

class LayoutStore {
    static float cell = 22; // cell size but shorter because used everywhere
    static float previewRectSize = cell * 0.6f;
    static int keyboardInputAppendCooldownMillis = 500;
    static final float defaultWindowWidthInCells = 10;
    private static float resizeRectangleSize = 4;
    private static boolean shouldKeepWindowsInBounds = true;
    private static boolean isWindowResizeEnabled = true;
    private static boolean shouldDrawResizeIndicator = true;

    public static void setCellSize(float inputCellSize) {
        cell = inputCellSize;
        previewRectSize = cell * 0.6f;
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
