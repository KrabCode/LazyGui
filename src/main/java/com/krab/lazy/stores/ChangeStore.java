package com.krab.lazy.stores;

import processing.core.PApplet;

import java.util.ArrayList;

/**
 * Static class for tracking value changes in nodes. Backend for gui.hasChanged() in the main API.
 * Paths mentioned here are gui paths, not file paths.
 *
 */
public class ChangeStore {
    static ArrayList<String> pathsThatChangedThisFrame = new ArrayList<>();
    static ArrayList<String> pathsThatChangedLastFrame = new ArrayList<>();

    public static void onValueChangingActionEnded(String path) {
        pathsThatChangedThisFrame.add(path);
        PApplet.println("frame " + GlobalReferences.app.frameCount +" changed \"" + path + "\"");
    }

    public static void onDrawFinished(){
        pathsThatChangedLastFrame.clear();
        pathsThatChangedLastFrame.addAll(pathsThatChangedThisFrame);
        pathsThatChangedThisFrame.clear();
    }

    public static boolean hasChanged(String path){
        boolean result = pathsThatChangedLastFrame.contains(path);
        if(result){
            PApplet.println("frame " + GlobalReferences.app.frameCount +" queried \"" + path + "\" = " + result);
        }
        return result;
    }
}
