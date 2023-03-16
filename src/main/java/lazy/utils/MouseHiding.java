package lazy.utils;

import static lazy.stores.GlobalReferences.*;

public class MouseHiding {
    private static boolean shouldHideWhenDragging = true;
    private static boolean shouldConfineToWindow = false;
    private static int mouseHidePosX;
    private static int mouseHidePosY;
    private static boolean isMouseHidden = false;

    public static void updateSettings() {
        gui.pushFolder("mouse");
        shouldHideWhenDragging = gui.toggle("hide when dragged", shouldHideWhenDragging);
        shouldConfineToWindow = gui.toggle("confine to window", shouldConfineToWindow);
        appWindow.confinePointer(shouldConfineToWindow);
        gui.popFolder();
        if(isMouseHidden){
            app.mouseX = mouseHidePosX;
            app.mouseY = mouseHidePosY;
        }
    }

    public static void tryHideMouseForDragging() {
        if(!shouldHideWhenDragging){
            return;
        }
        app.noCursor();
        if(!isMouseHidden){
            mouseHidePosX = app.mouseX;
            mouseHidePosY = app.mouseY;
        }
        isMouseHidden = true;
    }

    public static void tryRevealMouseAfterDragging() {
        if(!shouldHideWhenDragging){
            return;
        }
        if(isMouseHidden){
            resetMousePos();
        }
        isMouseHidden = false;
        app.cursor();
    }

    private static void resetMousePos() {
        appWindow.warpPointer(mouseHidePosX, mouseHidePosY);
    }
}
