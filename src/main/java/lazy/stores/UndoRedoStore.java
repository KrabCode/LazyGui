package lazy.stores;

import static processing.core.PApplet.println;

public class UndoRedoStore {
    public static void undo(){
        println("undoing");
    }

    public static void redo(){
        println("re-doing");
    }

    public static void onUndoableActionEnded(){
        println("action ended");
    }
}
