package com.krab.lazy.stores;

import com.krab.lazy.utils.KeyCodes;
import com.krab.lazy.windows.WindowManager;
import com.krab.lazy.input.LazyKeyEvent;

public class HotkeyStore {

    private static boolean hotkeyHideActive, hotkeyUndoActive, hotkeyRedoActive, hotkeyScreenshotActive,
            hotkeyCloseAllWindowsActive, hotkeySaveActive;
    private static boolean isScreenshotRequestedOnMainThread = false;

    public static void  updateHotkeyToggles() {
        GlobalReferences.gui.pushFolder("hotkeys");
        hotkeyHideActive = GlobalReferences.gui.toggle("h: hide\\/show gui", true);
        hotkeyCloseAllWindowsActive = GlobalReferences.gui.toggle("d: close windows", true);
        hotkeyScreenshotActive = GlobalReferences.gui.toggle("i: screenshot", true);
        hotkeyUndoActive = GlobalReferences.gui.toggle("ctrl + z: undo", true);
        hotkeyRedoActive = GlobalReferences.gui.toggle("ctrl + y: redo", true);
        hotkeySaveActive = GlobalReferences.gui.toggle("ctrl + s: new save", true);
        GlobalReferences.gui.textSet("mouseover specific hotkeys",
                "r: reset control element to default value\n" +
                        "ctrl + c: copy from (single value or folder)\n" +
                        "ctrl + v: paste to (single value or folder)\n" +
                        "these hotkeys cannot be turned off for now"
        );
        GlobalReferences.gui.popFolder();
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
        if(keyEvent.isControlDown() && keyCode == KeyCodes.Z && hotkeyUndoActive){
            UndoRedoStore.undo();
        }
        if(keyEvent.isControlDown() && keyCode == KeyCodes.Y && hotkeyRedoActive){
            UndoRedoStore.redo();
        }
        if(keyEvent.isControlDown() && keyCode == KeyCodes.S && hotkeySaveActive){
            JsonSaveStore.createNextSaveInGuiFolder();
        }
    }

    public static boolean isScreenshotRequestedOnMainThread(){
        return isScreenshotRequestedOnMainThread;
    }

    public static void setScreenshotRequestedOnMainThread(boolean val){
        isScreenshotRequestedOnMainThread = val;
    }


}
