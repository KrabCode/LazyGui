package lazy;

import processing.core.PGraphics;
import processing.core.PVector;

import java.util.concurrent.CopyOnWriteArrayList;

import static lazy.State.cell;

class WindowManager {
    private static WindowManager singleton;
    private final CopyOnWriteArrayList<Window> windows = new CopyOnWriteArrayList<>();
    private Window windowToSetFocusOn = null;

    WindowManager() {

    }

    static void createSingleton() {
        if (singleton == null) {
            singleton = new WindowManager();
        }
    }

    static void addWindow(FolderWindow window) {
        singleton.windows.add(window);
    }

    static void uncoverOrCreateWindow(NodeFolder nodeFolder){
        uncoverOrCreateWindow(nodeFolder, null, null, true);
    }

    static void uncoverOrCreateWindow(NodeFolder nodeFolder, Float nullablePosX, Float nullablePosY, boolean setFocus) {
        float mouseX = State.app.mouseX;
        float mouseY = State.app.mouseY;
        PVector pos = new PVector(mouseX - cell * 0.5f, mouseY-cell * 0.5f);
        if(nullablePosX != null){
            pos.x = nullablePosX;
        }
        if(nullablePosY != null){
            pos.y = nullablePosY;
        }
        boolean windowFound = false;
        for (Window w : singleton.windows) {
            if(w.parentNode.path.equals(nodeFolder.path)){
                w.open(setFocus);
                float windowContentWidth = nodeFolder.idealWindowWidth;
                if(windowContentWidth > 0){
                    w.windowSizeX = windowContentWidth;
                }
                windowFound = true;
                break;
            }
        }
        if(!windowFound){
            Window window = new FolderWindow(pos.x, pos.y, nodeFolder, true, nodeFolder.idealWindowWidth);
            singleton.windows.add(window);
            window.open(setFocus);
        }
        // the root window will always be initialized before this runs and thus always found, so if we want to load its position from json, we need to do it manually
        if(windowFound && nodeFolder.parent == null){
            nodeFolder.window.posX = pos.x;
            nodeFolder.window.posY = pos.y;
        }
    }

    static void updateAndDrawWindows(PGraphics pg) {
        if(singleton.windowToSetFocusOn != null){
            singleton.windows.remove(singleton.windowToSetFocusOn);
            singleton.windows.add(singleton.windowToSetFocusOn);
            singleton.windowToSetFocusOn = null;
        }

        for (Window win : singleton.windows) {
            win.drawWindow(pg);
        }
    }

    static boolean isFocused(Window window) {
        return singleton.windows.get(singleton.windows.size()-1).equals(window);
    }

    static void setFocus(Window window) {
        singleton.windowToSetFocusOn = window;
    }

    static void closeAllWindows() {
        for(Window win : singleton.windows){
            if(win.isCloseable){
                win.close();
            }
        }
    }
}
