package com.krab.lazy.stores;

import java.util.ArrayList;

import static processing.core.PApplet.println;

/**
 * For internal use by LazyGui, it's fed new states by the ChangeListener and also triggered manually by the undo/redo hotkeys.
 * The stateStack is an ordered list of full json saves of the GUI state, which combines undo and redo into one stack
 *      and the stateIndex keeps track of the where we currently are inside the combined stack.
 * Indexes in the stack larger than stateIndex are for undo, indexes before it are redo.
 * undo() increments the index by 1 and tries to apply the state it found there.
 * redo() decrements the index by 1 and tries to apply the state it found there.
 * onUndoableActionEnded() clears the redo by removing all elements before the stateIndex,
 *      sets stateIndex to 0 and then inserts the new action at index 0.
 */
public class UndoRedoStore {
    private static final boolean debugPrint = false;

    static final ArrayList<String> stateStack = new ArrayList<>();
    static int stateIndex = 0;

    public static void init(){
        onUndoableActionEnded();
    }

    public static void onUndoableActionEnded(){
        String newState = JsonSaveStore.getTreeAsJsonString();
        trimStack(stateIndex);
        stateStack.add(0, newState);
        stateIndex = 0;
        if(debugPrint){
            println("undo/redo frame " + GlobalReferences.app.frameCount + " new action current list size:" + stateStack.size());
            printStack();
        }
    }

    private static void trimStack(int stateIndex) {
        if(stateStack.isEmpty()){
            return;
        }
        stateStack.removeAll(stateStack.subList(0, stateIndex));
    }

    public static void undo(){
        tryLoadStateAtIndex(stateIndex + 1);
        if(debugPrint) {
            println("undo (+1)", stateIndex, "/", stateStack.size() - 1);
            printStack();
        }
    }

    public static void redo(){
        tryLoadStateAtIndex(stateIndex - 1);
        if(debugPrint) {
            println("redo (-1)", stateIndex, "/", stateStack.size() - 1);
            printStack();
        }
    }

    private static void tryLoadStateAtIndex(int newIndex) {
        if(!validateNewIndex(newIndex)){
            return;
        }
        String newState = stateStack.get(newIndex);
        JsonSaveStore.loadStateFromJsonString(newState);
        stateIndex = newIndex;
    }

    private static boolean validateNewIndex(int newIndex) {
        if(newIndex == stateIndex){
            if(debugPrint){
                println("validation failed: can't apply the same index as is already selected (" + newIndex + ")");
            }
            return false;
        }
        if(newIndex < 0 || newIndex > stateStack.size() - 1){
            if(debugPrint) {
                println("validation failed: new index out of bounds (" + newIndex + ")");
            }
            return false;
        }
        return true;
    }

    private static void printStack() {
        for(int i = 0; i < stateStack.size(); i++){
            println(i == stateIndex ? "(" + i + ")" : " " + i + " ");
        }
        println("---\n");
    }
}
