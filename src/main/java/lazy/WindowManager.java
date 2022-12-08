package lazy;

import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import static lazy.State.cell;
import static processing.core.PApplet.floor;
import static processing.core.PApplet.sin;

class WindowManager {
    private static final CopyOnWriteArrayList<Window> windows = new CopyOnWriteArrayList<>();
    private static final ArrayList<Window> windowsToSetFocusOn = new ArrayList<>();
    static boolean showPathTooltips = false;

    static void addWindow(Window window) {
        windows.add(window);
    }

    static void uncoverOrCreateWindow(FolderNode folderNode){
        uncoverOrCreateWindow(folderNode, true, null, null, null);
    }

    static void uncoverOrCreateWindow(FolderNode folderNode, boolean setFocus, Float nullablePosX, Float nullablePosY, Float nullableSizeX) {
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
        for (Window w : windows) {
            if(w.folder.path.equals(folderNode.path)){
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
            Window window = new Window(folderNode, true, pos.x, pos.y, nullableSizeX);
            windows.add(window);
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
        if(!windowsToSetFocusOn.isEmpty()){
            for (Window w : windowsToSetFocusOn){
                windows.remove(w);
                windows.add(w);
            }
            windowsToSetFocusOn.clear();
        }
        for (Window win : windows) {
            win.drawWindow(pg);
        }
    }

    static boolean isFocused(Window window) {
        return windows.get(windows.size()-1).equals(window);
    }

    static void setFocus(Window window) {
        windowsToSetFocusOn.add(window);
    }

    static void closeAllWindows() {
        for(Window win : windows){
            if(win.isCloseable){
                win.close();
            }
        }
    }

    public static void snapAllStaticWindowsToGrid() {
        for (Window w : windows) {
          if(w.closed || w.isBeingDraggedAround){
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
        State.setShouldKeepWindowsInBounds(State.gui.toggle("keep in bounds", State.getShouldKeepWindowsInBounds()));
        State.setWindowResizeEnabled(State.gui.toggle("allow resize", State.getWindowResizeEnabled()));
        State.setCellSize(State.gui.sliderInt("cell size", floor(cell), 12, Integer.MAX_VALUE));
        State.tryUpdateFont(
                State.gui.sliderInt("font size", State.getLastFontSize(), 1, Integer.MAX_VALUE),
                State.gui.slider("font x offset", State.textMarginX),
                State.gui.slider("font y offset", State.textMarginY)
        );
        State.gui.popFolder();
    }
}
