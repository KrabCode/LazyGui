package lazy.stores;

import lazy.utils.JsonSaves;

import java.util.ArrayList;

public class UndoRedoStore {

    static ArrayList<String> undoStack = new ArrayList<>();
    static ArrayList<String> redoStack = new ArrayList<>();

    public static void init(){
        addCurrentStateToUndoStack();
    }

    public static void addCurrentStateToUndoStack(){
        redoStack.clear();
        undoStack.add(0, JsonSaves.getTreeAsJsonString());
    }

    public static void undo(){
        String newState = transplantHead(undoStack, redoStack);
        if(newState != null){
            JsonSaves.loadStateFromJsonString(newState);
        }
    }

    public static void redo(){
        String newState = transplantHead(redoStack, undoStack);
        if(newState != null){
            JsonSaves.loadStateFromJsonString(newState);
        }
    }

    private static String transplantHead(ArrayList<String> sourceStack, ArrayList<String> targetStack){
        if(sourceStack.isEmpty()){
            return null;
        }
        if(sourceStack.size() >= 2){
            targetStack.add(0, sourceStack.remove(0));
        }
        return sourceStack.get(0);
    }
}
