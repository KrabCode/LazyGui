package toolbox.windows;

import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.tree.rows.FolderRow;

import java.util.ArrayList;

public class WindowManager {
    private static WindowManager singleton;
    private final ArrayList<Window> windows = new ArrayList<>();
    private Window windowToSetFocusOn = null;

    public WindowManager() {

    }

    public static void createSingleton() {
        if (singleton == null) {
            singleton = new WindowManager();
        }
    }

    public synchronized static void addWindow(FolderWindow explorer) {
        singleton.windows.add(explorer);
    }

    public synchronized static void uncoverOrCreateWindow(FolderRow folderNode, PVector pos) {
        boolean windowFound = false;
        for (Window w : singleton.windows) {
            if(w.row.path.equals(folderNode.path)){
                w.uncover();
                windowFound = true;
                break;
            }
        }
        if(!windowFound){
            singleton.windows.add(new FolderWindow(
                    pos,
                    folderNode, true));
        }

    }

    public synchronized static void updateAndDrawWindows(PGraphics pg) {
        if(singleton.windowToSetFocusOn != null){
            singleton.windows.remove(singleton.windowToSetFocusOn);
            singleton.windows.add(singleton.windowToSetFocusOn);
            singleton.windowToSetFocusOn = null;
        }

        for (Window win : singleton.windows) {
            win.drawWindow(pg);
        }
    }

    public synchronized static boolean isFocused(Window window) {
        return singleton.windows.get(singleton.windows.size()-1).equals(window);
    }

    public synchronized static void setFocus(Window window) {
        singleton.windowToSetFocusOn = window;
    }
}
