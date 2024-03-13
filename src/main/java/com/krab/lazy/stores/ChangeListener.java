package com.krab.lazy.stores;

import com.krab.lazy.utils.NodePaths;

import java.util.ArrayList;

/**
 * Static class for tracking value changes in nodes. Backend for gui.hasChanged() in the main API.
 * Paths mentioned here are gui paths, not file paths.
 *
 */
public class ChangeListener {
    static ArrayList<String> pathsThatChangedThisFrame = new ArrayList<>();
    static ArrayList<String> pathsThatChangedLastFrame = new ArrayList<>();

    public static void onValueChangingActionEnded(String path) {
        pathsThatChangedThisFrame.add(path);
    }

    public static void onFrameFinished(){
        pathsThatChangedLastFrame.clear();
        if(!pathsThatChangedThisFrame.isEmpty()){
            UndoRedoStore.onUndoableActionEnded();
        }
        pathsThatChangedLastFrame.addAll(pathsThatChangedThisFrame);
        pathsThatChangedThisFrame.clear();
    }

    public static boolean hasChangeFinishedLastFrame(String path){
        String pathWithoutTrailingSlash = NodePaths.getPathWithoutTrailingSlash(path);
        return pathsThatChangedLastFrame.contains(pathWithoutTrailingSlash);
    }
}
