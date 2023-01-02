package lazy.windows;

import lazy.nodes.FolderNode;
import lazy.stores.FontStore;
import lazy.stores.LayoutStore;
import lazy.utils.SnapToGrid;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import static lazy.stores.Globals.gui;
import static lazy.stores.LayoutStore.cell;
import static processing.core.PApplet.floor;

public class WindowManager {
    private static final CopyOnWriteArrayList<Window> windows = new CopyOnWriteArrayList<>();
    private static final ArrayList<Window> windowsToSetFocusOn = new ArrayList<>();
    static boolean showPathTooltips = false;

    public static void addWindow(Window window) {
        windows.add(window);
    }

    public static void uncoverOrCreateWindow(FolderNode folderNode){
        uncoverOrCreateWindow(folderNode, true, null, null, null);
    }

    public static void uncoverOrCreateWindow(FolderNode folderNode, boolean setFocus, Float nullablePosX, Float nullablePosY, Float nullableSizeX) {
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
            if(nullableSizeX != null){
                folderNode.window.windowSizeX = nullableSizeX;
            }
        }
    }

    public static void updateAndDrawWindows(PGraphics pg) {
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

    public static void setFocus(Window window) {
        windowsToSetFocusOn.add(window);
    }

    public static void closeAllWindows() {
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
          PVector newPos = SnapToGrid.trySnapToGrid(w.posX, w.posY);
          w.posX = newPos.x;
          w.posY = newPos.y;
          w.windowSizeX = SnapToGrid.trySnapToGrid(w.windowSizeX, 0).x;
        }
    }

    public static void updateWindowOptions() {
        gui.pushFolder("windows");
        LayoutStore.setCellSize(gui.sliderInt("cell size", floor(cell), 12, Integer.MAX_VALUE));
        FontStore.tryUpdateFont(
                gui.sliderInt("font size", FontStore.getLastFontSize(), 1, Integer.MAX_VALUE),
                gui.slider("font x offset", FontStore.textMarginX),
                gui.slider("font y offset", FontStore.textMarginY)
        );
        showPathTooltips = gui.toggle("show path tooltips", true);
        LayoutStore.setShouldKeepWindowsInBounds(gui.toggle("keep in bounds", LayoutStore.getShouldKeepWindowsInBounds()));
        gui.pushFolder("resize");
        LayoutStore.setWindowResizeEnabled(gui.toggle("allow resize", LayoutStore.getWindowResizeEnabled()));
        LayoutStore.setShouldDrawResizeIndicator(gui.toggle("show indicator", LayoutStore.getShouldDrawResizeIndicator()));
        LayoutStore.setResizeRectangleSize(gui.slider("indicator width", LayoutStore.getResizeRectangleSize()));
        gui.popFolder();
        gui.popFolder();
    }
}
