package toolbox.window;

import processing.core.PGraphics;

import java.util.ArrayList;

public class WindowManager {
    static WindowManager windowManager;
    ArrayList<Window> windows = new ArrayList<>();
    Window windowToSetFocusOn = null;

    private WindowManager(){

    }

    public static void lazyInitSingleton(){
        if(windowManager == null){
            windowManager = new WindowManager();
        }
    }

    public static void createWindow(Window window) {
        lazyInitSingleton();
        windowManager.windows.add(window);
    }

    public static void updateAndDrawWindows(PGraphics pg) {
        lazyInitSingleton();
        for(Window win : windowManager.windows){
            win.drawWindow(pg);
        }
        if(windowManager.windowToSetFocusOn != null){
            setFocus(windowManager.windowToSetFocusOn);
        }
    }

    public static void requestFocus(Window window){
        lazyInitSingleton();
        windowManager.windowToSetFocusOn = window;
    }

    private static void setFocus(Window window) {
        lazyInitSingleton();
        ArrayList<Window> windows = windowManager.windows;
        if(windows.indexOf(window) < windows.size() - 1){
            windows.remove(window);
            windows.add(window);
        }
    }

}
