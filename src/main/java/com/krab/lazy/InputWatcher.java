package com.krab.lazy;

import com.krab.lazy.input.InputWatcherBackend;

public class InputWatcher {
    public static boolean getKey(char key){
        return InputWatcherBackend.getKeyStateByChar(key);
    }

    public static boolean getKeyCoded(int keyCode){
        return InputWatcherBackend.getKeyStateByCode(keyCode);
    }
}
