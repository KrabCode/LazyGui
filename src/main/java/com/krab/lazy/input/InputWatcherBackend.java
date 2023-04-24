package com.krab.lazy.input;

import com.krab.lazy.KeyState;
import processing.core.PVector;
import processing.event.KeyEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.krab.lazy.stores.GlobalReferences.app;
import static processing.core.PApplet.println;
import static processing.core.PConstants.CODED;

/**
 * User-facing utility class that watches and serves individual key and mouse state at runtime.
 */
public class InputWatcherBackend {

    private static InputWatcherBackend singleton;
    private static final ArrayList<Integer> codesToClearPressedOn = new ArrayList<>();
    private static final ArrayList<Integer> codesToClearReleasedOn = new ArrayList<>();
    private static final Map<Integer, KeyState> codeStates = new HashMap<>();
    private static final Map<Character, Integer> charsCodes = new HashMap<>();
    private static boolean debugKeys = false;
    private static final KeyState nullState = new KeyState(false, false, false);

    public static void initSingleton() {
        if (singleton == null) {
            singleton = new InputWatcherBackend();
        }
    }

    private InputWatcherBackend() {
        registerListeners();
    }

    private void registerListeners() {
        // the reference passed here is the only reason to have this be a singleton instance rather than a fully static class with no instance
        app.registerMethod("keyEvent", this);
        app.registerMethod("post", this);
    }

    @SuppressWarnings("unused")
    public void post() {
        postHandleKeyPresses();
        postHandleKeyReleases();
    }

    @SuppressWarnings("unused")
    public void keyEvent(KeyEvent event) {
        if (debugKeys) {
            println(keyEventString(event));
        }
        char key = event.getKey();
        int keyCode = event.getKeyCode();
        if (key != CODED && !charsCodes.containsKey(key)) {
            charsCodes.put(key, keyCode);
        }
        if (!codeStates.containsKey(keyCode)) {
            codeStates.put(keyCode, new KeyState(false, false, false));
        }
        KeyState state = codeStates.get(keyCode);
        if (event.getAction() == KeyEvent.PRESS) {
            state.down = true;
            state.pressed = true;
        } else if (event.getAction() == KeyEvent.RELEASE) {
            state.down = false;
            state.released = true;
        }
    }

    public static KeyState getKeyStateByChar(char keyChar) {
        Integer code = charsCodes.get(keyChar);
        return getKeyStateByCode(code);
    }

    public static KeyState getKeyStateByCode(Integer keyCode) {
        if (keyCode == null || !codeStates.containsKey(keyCode)) {
            return nullState;
        }
        return codeStates.get(keyCode);
    }

    private void postHandleKeyPresses() {
        for (int keyCode : codesToClearPressedOn) {
            unpress(keyCode);
        }
        codesToClearPressedOn.clear();
        for (Integer keyCode : codeStates.keySet()) {
            KeyState state = codeStates.get(keyCode);
            if (state.pressed) {
                codesToClearPressedOn.add(keyCode);
                state.framePressed = app.frameCount;
            }
        }
    }

    private void postHandleKeyReleases() {
        for (int keyCode : codesToClearReleasedOn) {
            unrelease(keyCode);
        }
        codesToClearReleasedOn.clear();
        for (Integer keyCode : codeStates.keySet()) {
            KeyState state = codeStates.get(keyCode);
            if (state.released) {
                codesToClearReleasedOn.add(keyCode);
                state.frameReleased = app.frameCount;
            }
        }
    }

    private void unrelease(int keyCode) {
        if (codeStates.containsKey(keyCode)) {
            KeyState state = codeStates.get(keyCode);
            state.released = false;
        }
    }

    private void unpress(int keyCode) {
        if (codeStates.containsKey(keyCode)) {
            codeStates.get(keyCode).pressed = false;
        }
    }

    public static void setDebugKeys(boolean shouldDebugKeys) {
        InputWatcherBackend.debugKeys = shouldDebugKeys;
    }

    String keyEventString(KeyEvent event) {
        String actionString;
        switch (event.getAction()) {
            case KeyEvent.PRESS:
                actionString = "PRESS";
                break;
            case KeyEvent.TYPE:
                actionString = "TYPE";
                break;
            case KeyEvent.RELEASE:
                actionString = "RELEASE";
                break;
            default:
                actionString = "UNKNOWN (" + event.getAction() + ")";
        }
        return String.format("<KeyEvent | action: %s| key: %s| code: %s>",
                padString(actionString, "RELEASE ".length()),
                padString(String.valueOf(event.getKey()), 2),
                padString(String.valueOf(event.getKeyCode()), 3));
    }

    String padString(String toPad, int count) {
        StringBuilder sb = new StringBuilder(toPad);
        for (int i = sb.length(); i < count; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }


    public static PVector mousePos(){
        return new PVector(app.mouseX, app.mouseY);
    }

    public static PVector mouseDelta() {
        return new PVector(app.mouseX - app.pmouseX, app.mouseY - app.pmouseY);
    }
}
