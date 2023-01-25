package lazy.stores;

public class LayoutStore {
    public static float cell = 22; // cell size but shorter because used everywhere
    public static final float defaultWindowWidthInCells = 10;
    private static int keyboardBufferDelayMillis = 500;
    private static float resizeRectangleSize = 4;
    private static boolean shouldKeepWindowsInBounds = true;
    private static boolean isWindowResizeEnabled = true;
    private static boolean shouldDrawResizeIndicator = true;

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

    public static int getKeyboardBufferDelayMillis() {
        return keyboardBufferDelayMillis;
    }

    public static void setKeyboardBufferDelayMillis(int keyboardBufferDelayMillis) {
        LayoutStore.keyboardBufferDelayMillis = keyboardBufferDelayMillis;
    }
}
