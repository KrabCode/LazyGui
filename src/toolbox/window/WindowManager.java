package toolbox.window;

import processing.core.PGraphics;

import java.util.ArrayList;

public class WindowManager {
    static WindowManager singleton;
    ArrayList<Window> windows = new ArrayList<>();
    ArrayList<Window> windowsToAdd = new ArrayList<>();
    Window windowToSetFocusOn = null;

    private WindowManager(){

    }

    public static void createSingleton(){
        if(singleton == null){
            singleton = new WindowManager();
        }
    }

    public static void createOrUncoverWindow(Window window) {
        Window nullableWindow = singleton.findWindowByPath(window.path);
        if(nullableWindow == null){
            singleton.windowsToAdd.add(window);
        }else{
            nullableWindow.uncover();
        }
    }

    public boolean windowExists(Window window){
        return findWindowByPath(window.path) != null;
    }

    public Window findWindowByPath(String path){
        for(Window w : windows){
            if(w.path.equals(path)){
                return w;
            }
        }
        return null;
    }

    public static void updateAndDrawWindows(PGraphics pg) {
        for(Window win : singleton.windows){
            win.drawWindow(pg);
        }
        if(singleton.windowToSetFocusOn != null){
            setFocus(singleton.windowToSetFocusOn);
            singleton.windowToSetFocusOn = null;
        }
        singleton.windows.addAll(singleton.windowsToAdd);
        singleton.windowsToAdd.clear();
    }

    public static void requestFocus(Window window){
        singleton.windowToSetFocusOn = window;
    }

    private static void setFocus(Window window) {
        ArrayList<Window> windows = singleton.windows;
        if(windows.indexOf(window) < windows.size() - 1){
            windows.remove(window);
            windows.add(window);
        }
    }

    public static boolean isFocused(Window window){
        return singleton.windows.indexOf(window) == singleton.windows.size() - 1;
    }
}
