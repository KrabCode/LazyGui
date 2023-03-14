package lazy.stores;

import lazy.input.LazyKeyEvent;
import lazy.utils.KeyCodes;
import lazy.windows.WindowManager;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static lazy.stores.GlobalReferences.app;
import static lazy.stores.GlobalReferences.gui;

public class HotkeyStore {

    private static boolean hotkeyHideActive, hotkeyUndoActive, hotkeyRedoActive, hotkeyScreenshotActive,
            hotkeyCloseAllWindowsActive, hotkeySaveActive, hotkeyOpenSketchFolderActive;
    private static boolean isScreenshotRequestedOnMainThread = false;

    public static void  updateHotkeyToggles() {
        gui.pushFolder("hotkeys");
        hotkeyHideActive = gui.toggle("h: hide\\/show gui", true);
        hotkeyCloseAllWindowsActive = gui.toggle("d: close windows", true);
        hotkeyScreenshotActive = gui.toggle("i: screenshot", true);
        hotkeyUndoActive = gui.toggle("ctrl + z: undo", true);
        hotkeyRedoActive = gui.toggle("ctrl + y: redo", true);
        hotkeySaveActive = gui.toggle("ctrl + s: new save", true);
        hotkeyOpenSketchFolderActive = gui.toggle("k: open sketch folder", true);
        gui.textSet("mouseover specific hotkeys",
                "r: reset control element to default value\n" +
                        "ctrl + c: copy from (single value or folder)\n" +
                        "ctrl + v: paste to (single value or folder)\n" +
                        "these hotkeys cannot be turned off for now"
        );
        gui.popFolder();
    }

    public static void handleHotkeyInteraction(LazyKeyEvent keyEvent) {
        char key = keyEvent.getKey();
        int keyCode = keyEvent.getKeyCode();
        if (key == 'h' && hotkeyHideActive) {
            LayoutStore.hideGuiToggle();
        }
        isScreenshotRequestedOnMainThread = (key == 'i' && hotkeyScreenshotActive);
        if(key == 'd' && hotkeyCloseAllWindowsActive){
            WindowManager.closeAllWindows();
        }
        if(key == 'k' && hotkeyOpenSketchFolderActive){
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(new File(app.dataPath("")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(keyEvent.isControlDown() && keyCode == KeyCodes.Z && hotkeyUndoActive){
            UndoRedoStore.undo();
        }
        if(keyEvent.isControlDown() && keyCode == KeyCodes.Y && hotkeyRedoActive){
            UndoRedoStore.redo();
        }
        if(keyEvent.isControlDown() && keyCode == KeyCodes.S && hotkeySaveActive){
            JsonSaveStore.createNewSave();
        }
    }

    public static boolean isScreenshotRequestedOnMainThread(){
        return isScreenshotRequestedOnMainThread;
    }

    public static void setScreenshotRequestedOnMainThread(boolean val){
        isScreenshotRequestedOnMainThread = val;
    }


}
