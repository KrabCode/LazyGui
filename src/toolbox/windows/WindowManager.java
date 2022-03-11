package toolbox.windows;

import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.global.State;
import toolbox.windows.nodes.FolderNode;

import java.util.concurrent.CopyOnWriteArrayList;

import static toolbox.global.State.cell;

public class WindowManager {
    private static WindowManager singleton;
    private final CopyOnWriteArrayList<Window> windows = new CopyOnWriteArrayList<>();
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

    public static void uncoverOrCreateWindow(FolderNode folderNode) {
        float mouseX = State.app.mouseX;
        float mouseY = State.app.mouseY;
        PVector pos = new PVector(mouseX - cell * 0.5f, mouseY-cell * 0.5f);
        boolean windowFound = false;
        for (Window w : singleton.windows) {
            if(w.parentNode.path.equals(folderNode.path)){
                w.uncover();
                w.setFocusOnThis();
                windowFound = true;
                break;
            }
        }
        if(!windowFound){
            Window window = new FolderWindow(pos,folderNode, true);
            singleton.windows.add(window);
            window.uncover();
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
