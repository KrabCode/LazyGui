package toolbox.windows;

import processing.core.PGraphics;

import java.util.ArrayList;

public class WindowManager {
    private static WindowManager manager;
    int currentMaxFocus = 0;

    private final ArrayList<Window> windows = new ArrayList<>();
    Window windowToSetFocusOn = null;
    private final ArrayList<Window> windowsToAdd = new ArrayList<>();

    public WindowManager() {

    }

    public static void createSingleton() {
        if (manager == null) {
            manager = new WindowManager();
        }
    }

    public static void uncoverOrAddWindow(FolderWindow window) {
        // TODO
        boolean windowFound = false;
        for (Window w : manager.windows) {
            if(w.node.path.equals(window.node.path)){
                w.uncover();
                windowFound = true;
                break;
            }
        }
        if(!windowFound){
            manager.windowsToAdd.add(window);
        }
    }

    public static void updateAndDrawWindows(PGraphics pg) {
        manager.windows.addAll(manager.windowsToAdd);
        manager.windowsToAdd.clear();
        if(manager.windowToSetFocusOn != null){
            manager.windows.remove(manager.windowToSetFocusOn);
            manager.windows.add(manager.windowToSetFocusOn);
            manager.windowToSetFocusOn = null;
        }

        for (Window win : manager.windows) {
            win.drawWindow(pg);
        }
    }

    public static boolean isFocused(Window window) {
        return manager.windows.get(manager.windows.size()-1).equals(window);
    }

    public static void setFocus(Window window) {
        manager.windowToSetFocusOn = window;
    }
}
