package lazy.windows;

import processing.core.PGraphics;
import processing.core.PVector;
import lazy.State;
import lazy.windows.nodes.NodeFolder;

import java.util.concurrent.CopyOnWriteArrayList;

import static lazy.State.cell;

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

    public static void addWindow(FolderWindow window) {
        singleton.windows.add(window);
    }

    public static void uncoverOrCreateWindow(NodeFolder nodeFolder){
        uncoverOrCreateWindow(nodeFolder, null, null, true);
    }

    public static void uncoverOrCreateWindow(NodeFolder nodeFolder, Float posX, Float posY, boolean setFocus) {
        float mouseX = State.app.mouseX;
        float mouseY = State.app.mouseY;
        PVector pos = new PVector(mouseX - cell * 0.5f, mouseY-cell * 0.5f);
        if(posX != null){
            pos.x = posX;
        }
        if(posY != null){
            pos.y = posY;
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

    public static void closeAllWindows() {
        for(Window win : singleton.windows){
            if(win.isCloseable){
                win.close();
            }
        }
    }
}
