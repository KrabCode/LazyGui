package toolbox.windows;

import processing.core.PGraphics;

import java.util.ArrayList;

public class WindowManager {
    private static WindowManager singleton;
    private static Window focusedWindow = null;

    @SuppressWarnings("FieldCanBeLocal") // no it can't, I want to be able to debug it globally
    public static TreeWindow treeWindow = null;

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
        Window nullableWindow = singleton.findWindowByPath(window.node.path);
        if (nullableWindow == null) {
            singleton.windowsToAdd.add(window);
        } else {
            // throw away the parameter window, use existing instead
            nullableWindow.uncover();
        }
    }

    public static boolean isHidden(String path) {
        Window nullableWindow = singleton.findWindowByPath(path);
        return nullableWindow == null || nullableWindow.hidden;
    }

    public boolean windowExists(Window window) {
        return findWindowByPath(window.node.path) != null;
    }

    public Window findWindowByPath(String path) {
        for (Window w : windows) {
            if (w.node.path.equals(path)) {
                return w;
            }
        }
        for (Window w : windowsToAdd) {
            if (w.node.path.equals(path)) {
                return w;
            }
        }
        return null;
    }

    public static void updateAndDrawWindows(PGraphics pg) {
        for (Window win : singleton.windows) {
            if(focusedWindow != null && win.node.path.equals(focusedWindow.node.path)){
                continue;
            }
            win.drawWindow(pg);
        }
        if(focusedWindow != null){
            focusedWindow.drawWindow(pg);
        }

//        treeWindow.debugHitboxes(pg, treeWindow.root);

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
