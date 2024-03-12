package com.krab.lazy.stores;

import processing.core.PApplet;

import java.util.ArrayList;

/**
 * Static class for tracking value changes in nodes. Backend for gui.hasChanged() in the main API.
 * Paths mentioned here are gui paths, not file paths.
 *
 */
public class ChangeStore {
    static ArrayList<String> pathsThatChanged = new ArrayList<>();

    public static void onChange(String path) {
        pathsThatChanged.add(path);
        PApplet.println("frame " + GlobalReferences.app.frameCount +" changed \"" + path + "\"");
    }

    public static void onDrawFinished(){
        pathsThatChanged.clear();
    }

    public static boolean hasChanged(String path){
        return pathsThatChanged.contains(path);
    }
}
