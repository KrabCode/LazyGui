package com.krab.lazy;

import com.krab.lazy.input.InputWatcherBackend;
import processing.core.PVector;

/**
 * Interface to a utility class that keeps track of all currently pressed keys to simplify input code, because Processing doesn't offer that.
 * Ask it for individual key states using chars or keyCodes and see if the key was pressed this frame, whether it is now down or whether it was just released.
 * Also includes two simple mouse position functions.
 * Mouse events are considered well supported by Processing so there is no alternative offered here (at least for now).
 */
public class Input {
    /**
     * Get the current keyboard button state by using its char value.
     * Meant to be used like <code>getKey('n').down</code>
     *
     * @param key char representation of the key
     * @return current state of the keyboard button
     */
    public static KeyState getKey(char key){
        return InputWatcherBackend.getKeyStateByChar(key);
    }

    /**
     * Get the current keyboard button state by using its keyCode value.
     * Meant to be used like <code>getKey(LEFT).down</code>
     *
     * @param keyCode keyCode representation of the key
     * @return current state of the keyboard button
     */
    public static KeyState getKey(int keyCode){
        return InputWatcherBackend.getKeyStateByCode(keyCode);
    }

    /**
     * Prints all newly received key events to console when this is set to true until manually set to false.
     * False by default to not clutter the console.
     *
     * @param shouldDebugKeys should the InputWatcher print all the key events it receives to console?
     */
    public static void debugPrintKeyEvents(boolean shouldDebugKeys) {
        InputWatcherBackend.setDebugKeys(shouldDebugKeys);
    }

    /**
     * Get the current mouseX and mouseY as a PVector.
     *
     * @return a PVector with the current mouse position
     */
    public static PVector mousePos(){
        return InputWatcherBackend.mousePos();
    }

    /**
     * Get the current mouseX-pmouseX and mouseY-pmouseY as a PVector.
     *
     * @return a PVector with the difference between the last known mouse position and the current one
     */
    public static PVector mouseDelta(){
        return InputWatcherBackend.mouseDelta();
    }
}
