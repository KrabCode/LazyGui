package com.krab.lazy.input;

import com.krab.lazy.KeyState;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.HashMap;
import java.util.Map;

import static com.krab.lazy.stores.GlobalReferences.app;
import static processing.core.PApplet.println;

/**
 * User-facing utility class that watches and serves individual key and mouse state at runtime.
 */
public class InputWatcherBackend {

    private static InputWatcherBackend singleton;
    private static final Map<Integer, KeyState> stateMap = new HashMap<>();
    private static final Map<Character, Integer> codeMap = new HashMap<>();
    private static boolean debugInput = false;

    private static final KeyState nullState = new KeyState(false, false, false);

    public static void initSingleton() {
        if (singleton == null) {
            singleton = new InputWatcherBackend();
        }
    }

    private InputWatcherBackend() {
        registerListeners();
    }

    public static KeyState getKeyStateByChar(char keyChar) {
        Integer code = codeMap.get(keyChar);
        return getKeyStateByCode(code);
    }

    public static KeyState getKeyStateByCode(Integer keyCode) {
        if (keyCode == null || !stateMap.containsKey(keyCode)) {
            return nullState;
        }
        return stateMap.get(keyCode);
    }

    public static void setDebugInput(boolean debugInput) {
        InputWatcherBackend.debugInput = debugInput;
    }

    public void draw() {
        clearPressAndRelease();
    }

    private void clearPressAndRelease() {
        for (Integer code : stateMap.keySet()) {
            KeyState state = stateMap.get(code);
            state.press = false;
            state.release = false;
        }
    }

    private void registerListeners() {
        // the reference passed here is the only reason to have this be a singleton instance rather than a fully static class with no instance
        // app.registerMethod("mouseEvent", this);
        app.registerMethod("keyEvent", this);
        app.registerMethod("draw", this);
    }

    @SuppressWarnings("unused")
    public void mouseEvent(MouseEvent event) {
        if (debugInput) {
            println(event);
        }
    }

    @SuppressWarnings("unused")
    public void keyEvent(KeyEvent event) {
        if (debugInput) {
            println(keyEventString(event));
        }
        if (!codeMap.containsKey(event.getKey())) {
            codeMap.put(event.getKey(), event.getKeyCode());
        }
        // TODO fix, press/release booleans don't work
        if (event.getAction() == KeyEvent.PRESS) {
            stateMap.put(event.getKeyCode(), new KeyState(true, true, false));
        } else if (event.getAction() == KeyEvent.RELEASE) {
            stateMap.put(event.getKeyCode(), new KeyState(false, false, true));
        }
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

}
