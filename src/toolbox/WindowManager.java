package toolbox;

import processing.core.PGraphics;
import toolbox.window.TreeExplorer;
import toolbox.window.Window;

import java.util.ArrayList;

public class WindowManager {
    private static WindowManager singleton;

    private final ArrayList<Window> windows = new ArrayList<>();
    private final ArrayList<Window> windowsToAdd = new ArrayList<>();

    public WindowManager() {

    }

    public static void createSingleton() {
        if (singleton == null) {
            singleton = new WindowManager();
        }
    }

    public static void addWindow(Window window) {
        singleton.windowsToAdd.add(window);
    }

    public static void updateAndDrawWindows(PGraphics pg) {
        singleton.windows.addAll(singleton.windowsToAdd);
        singleton.windowsToAdd.clear();

        for (Window win : singleton.windows) {
            win.drawWindow(pg);
        }
    }

    public static boolean isFocused(Window window) {
        return false;
    }

    public static void setFocus(Window window) {

    }
}
