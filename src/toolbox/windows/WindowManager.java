package toolbox.windows;

import processing.core.PGraphics;

import java.util.ArrayList;
import java.util.Collections;

public class WindowManager {
    private static WindowManager s;
    private final ArrayList<Window> windows = new ArrayList<>();
    private Window windowToSetFocusOn = null;
    private final ArrayList<Window> windowsToAdd = new ArrayList<>();

    public WindowManager() {

    }

    public static void createSingleton() {
        if (s == null) {
            s = new WindowManager();
        }
    }

    public static void uncoverOrAddWindow(FolderWindow window) {
        boolean windowFound = false;
        for (Window w : s.windows) {
            if(w.node.path.equals(window.node.path)){
                w.uncover();
                windowFound = true;
                break;
            }
        }
        if(!windowFound){
            s.windowsToAdd.add(window);
        }
    }

    public static void updateAndDrawWindows(PGraphics pg) {
        s.windows.addAll(s.windowsToAdd);
        s.windowsToAdd.clear();
        if(s.windowToSetFocusOn != null){
            s.windows.remove(s.windowToSetFocusOn);
            s.windows.add(s.windowToSetFocusOn);
            s.windowToSetFocusOn = null;
        }

        for (Window win : s.windows) {
            win.drawWindow(pg);
        }
    }

    public static boolean isFocused(Window window) {
        return s.windows.get(s.windows.size()-1).equals(window);
    }

    public static void setFocus(Window window) {
        s.windowToSetFocusOn = window;
    }
}
