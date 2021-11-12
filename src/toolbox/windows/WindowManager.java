package toolbox.windows;

import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.font.GlobalState;

import java.util.ArrayList;

public class WindowManager {
    private static WindowManager singleton;
    private static Window focusedWindow = null;
    private static TreeWindow treeWindow = null;
    private final ArrayList<Window> windows = new ArrayList<>();
    private final ArrayList<Window> windowsToAdd = new ArrayList<>();

    private WindowManager() {

    }

    public static void createSingleton() {
        if (singleton == null) {
            singleton = new WindowManager();
            treeWindow = WindowFactory.createMainTreeWindow();
            WindowManager.registerOrUncoverWindow(treeWindow);
        }
    }

    public static void registerOrUncoverWindow(Window window) {
        Window nullableWindow = singleton.getWindow(window.path);
        if (nullableWindow == null) {
            singleton.windowsToAdd.add(window);
        } else {
            // throw away the parameter window, use existing instead
            nullableWindow.uncover();
        }
    }

    public boolean windowExists(Window window) {
        return getWindow(window.path) != null;
    }

    public Window getWindow(String path) {
        for (Window w : windows) {
            if (w.path.equals(path)) {
                return w;
            }
        }
        for (Window w : windowsToAdd) {
            if (w.path.equals(path)) {
                return w;
            }
        }
        return null;
    }

    public static void updateAndDrawWindows(PGraphics pg) {
        for (Window win : singleton.windows) {
            if(focusedWindow != null && win.path.equals(focusedWindow.path)){
                continue;
            }
            win.drawWindow(pg);
        }
        if(focusedWindow != null){
            focusedWindow.drawWindow(pg);
        }
        singleton.windows.addAll(singleton.windowsToAdd);
        singleton.windowsToAdd.clear();
    }

    protected static void setFocus(Window window) {
        focusedWindow = window;
    }

    public static boolean isFocused(Window window) {
        return window.equals(focusedWindow);
    }
}
