package com.krab.lazy;

import com.krab.lazy.input.InputWatcherBackend;
import processing.core.PVector;

public class Input {
    public static InputKeyState getKey(char key){
        return InputWatcherBackend.getKeyStateByChar(key);
    }

    public static InputKeyState getKey(int keyCode){
        return InputWatcherBackend.getKeyStateByCode(keyCode);
    }

    public static void debugPrintKeyEvents(boolean shouldDebugKeys) {
        InputWatcherBackend.setDebugKeys(shouldDebugKeys);
    }

    public static PVector mousePos(){
        return InputWatcherBackend.mousePos();
    }

    public static PVector mouseDelta(){
        return InputWatcherBackend.mouseDelta();
    }
}
