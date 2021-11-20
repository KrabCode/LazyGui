package toolbox.windows;

import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.tree.nodes.FolderNode;

import java.util.ArrayList;

import static toolbox.GlobalState.cell;

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

    public static void addWindow(FolderWindow explorer) {
        singleton.windows.add(explorer);
    }

    public static void uncoverOrCreateWindow(FolderNode folderNode, PVector pos) {
        boolean windowFound = false;
        for (Window w : singleton.windows) {
            if(w.node.path.equals(folderNode.path)){
                w.uncover();
                windowFound = true;
                break;
            }
        }
        if(!windowFound){
            singleton.windows.add(new FolderWindow(
                    pos,
                    new PVector(cell * 8, cell * 1),
                    folderNode, true));
        }
    }

    public static void updateAndDrawWindows(PGraphics pg) {
        if(singleton.windowToSetFocusOn != null){
            singleton.windows.remove(singleton.windowToSetFocusOn);
            singleton.windows.add(singleton.windowToSetFocusOn);
            singleton.windowToSetFocusOn = null;
        }

        for (Window win : singleton.windows) {
            win.drawWindow(pg);
        }
    }

    public static boolean isFocused(Window window) {
        return singleton.windows.get(singleton.windows.size()-1).equals(window);
    }

    public static void setFocus(Window window) {
        singleton.windowToSetFocusOn = window;
    }
}
