package lazy;

import processing.core.PGraphics;
import processing.core.PVector;

import java.util.concurrent.CopyOnWriteArrayList;

import static lazy.State.cell;
import static processing.core.PApplet.floor;

class WindowManager {
    private static WindowManager singleton;
    private final CopyOnWriteArrayList<Window> windows = new CopyOnWriteArrayList<>();
    private Window windowToSetFocusOn = null;
    static boolean showPathTooltips = false;

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

    static void uncoverOrCreateWindow(FolderNode folderNode){
        uncoverOrCreateWindow(folderNode, null, null, true);
    }

    static void uncoverOrCreateWindow(FolderNode folderNode, Float nullablePosX, Float nullablePosY, boolean setFocus) {
        PVector pos = new PVector(cell, cell);
        if(folderNode.parent != null){
            Window parentWindow = folderNode.parent.window;
            if(parentWindow != null){
                pos = new PVector(parentWindow.posX + parentWindow.windowSizeX + cell, parentWindow.posY);
            }
        }
        if(nullablePosX != null){
            pos.x = nullablePosX;
        }
        if(nullablePosY != null){
            pos.y = nullablePosY;
        }
        boolean windowFound = false;
        for (Window w : singleton.windows) {
            if(w.owner.path.equals(folderNode.path)){
                if(w.closed){
                    w.posX = pos.x;
                    w.posY = pos.y;
                }
                w.open(setFocus);
                windowFound = true;
                break;
            }
        }
        if(!windowFound){
            Window window = new FolderWindow(pos.x, pos.y, folderNode, true);
            singleton.windows.add(window);
            window.open(setFocus);
        }
        // the root window will always be initialized before this runs and thus always found,
        // so if we want to load its position from json, we need to do it manually
        if(windowFound && folderNode.parent == null){
            folderNode.window.posX = pos.x;
            folderNode.window.posY = pos.y;
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

    public static void snapAllStaticWindowsToGrid() {
        for (Window w : singleton.windows) {
          if(w.closed || w.isDraggedAround){
              continue;
          }
          PVector newPos = UtilGridSnap.trySnapToGrid(w.posX, w.posY);
          w.posX = newPos.x;
          w.posY = newPos.y;
        }
    }

    public static void updateWindowOptions() {
        State.gui.pushFolder("windows");
        showPathTooltips = State.gui.toggle("show path tooltips", true);
        State.setKeepWindowsInBounds(State.gui.toggle("keep in bounds", State.keepWindowsInBounds));
        State.setCellSize(State.gui.sliderInt("cell size", floor(cell), 12, Integer.MAX_VALUE));
        State.tryUpdateFont(
                State.gui.sliderInt("font size", State.getLastFontSize(), 1, Integer.MAX_VALUE),
                State.gui.slider("font x offset", State.textMarginX),
                State.gui.slider("font y offset", State.textMarginY)
        );
        State.gui.popFolder();
    }
}
