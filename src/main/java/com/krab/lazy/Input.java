package com.krab.lazy;

import com.krab.lazy.input.InputWatcherBackend;

public class Input {
    public static KeyState getKey(char key){
        return InputWatcherBackend.getKeyStateByChar(key);
    }

    public static KeyState getKey(int keyCode){
        return InputWatcherBackend.getKeyStateByCode(keyCode);
    }

    public static void debug(boolean shouldDebug) {
        InputWatcherBackend.setDebugInput(shouldDebug);
    }
}
