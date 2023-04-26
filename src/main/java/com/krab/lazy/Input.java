package com.krab.lazy;

import com.krab.lazy.input.InputWatcherBackend;
import processing.core.PVector;
import java.util.List;

/**
 * Interface to a utility class that keeps track of all currently pressed keys to simplify the code for complex keyboard interaction, because Processing doesn't offer that.
 * Ask it for individual key states using chars or keyCodes and see if the key was pressed this frame, whether it is now down or whether it was just released.
 * Also includes two simple mouse position functions.
 * Mouse events are considered well supported by Processing so there is no alternative offered here (at least for now).
 */
@SuppressWarnings("unused")
public class Input {
    /**
     * Get the current keyboard button state by using its char value.
     * Meant to be used like <code>getChar('n').down</code>
     *
     * @param key char representation of the key
     * @return current state of the keyboard button
     */
    public static KeyState getChar(char key){
        return InputWatcherBackend.getKeyStateByChar(key);
    }

    /**
     * Get the current keyboard button state by using its keyCode value.
     * Meant to be used like <code>getCode(LEFT).down</code>
     *
     * @param keyCode keyCode representation of the key
     * @return current state of the keyboard button
     */
    public static KeyState getCode(int keyCode){
        return InputWatcherBackend.getKeyStateByCode(keyCode);
    }

    /**
     * Gets all currently pressed chars as lowercase strings in a list.
     * Does not include special keys for which <code>key == CODED</code> is true like CTRL and DELETE.
     * The list is sorted in alphabetical order.
     * @return list of all currently pressed chars as lowercase strings in a list
     * @see <a href="https://processing.org/reference/key.html">processing key reference page</a>
     * @see <a href="https://processing.org/reference/keyCode.html">Processing keyCode reference</a>
     */
    public static List<String> getAllDownChars(){
        return InputWatcherBackend.getAllDownChars();
    }

    /**
     * Gets all currently pressed keys as integer keyCodes in a list. Includes all standard chars as well as special keys like CTRL and DELETE,
     * which you can compare with processing constants or com.jogamp.newt.event.KeyEvent int constants.
     * The list is sorted in natural ascending order.
     * @see <a href="https://processing.org/reference/keyCode.html">processing keyCode reference page</a>
     * @return list of all currently pressed keys as integer keyCodes
     */
    public static List<Integer> getAllDownCodes(){
        return InputWatcherBackend.getAllDownCodes();
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
     * Get pmouseX and pmouseY as a PVector describing the last frame's mouse position.
     * This vector plus <code>mouseDelta()</code> should equal the current <code>mousePos()</code>
     *
     * @return a PVector with the previous frame's mouse position
     */
    public static PVector mousePosLastFrame(){
        return InputWatcherBackend.mousePosLastFrame();
    }

    /**
     * Gets the difference between current frame mouse position and previous frame mouse position as a PVector.
     * This is equivalent to <code>new PVector(app.mouseX - app.pmouseX, app.mouseY - app.pmouseY);</code>
     *
     * @return a PVector with the difference between the last known mouse position and the current one
     */
    public static PVector mouseDelta(){
        return InputWatcherBackend.mouseDelta();
    }

}
