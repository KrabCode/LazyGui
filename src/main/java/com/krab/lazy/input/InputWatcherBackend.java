package com.krab.lazy.input;

import com.krab.lazy.KeyState;
import processing.core.PVector;
import processing.event.Event;
import processing.event.KeyEvent;

import java.util.*;

import static com.krab.lazy.stores.GlobalReferences.app;
import static processing.core.PApplet.println;
import static processing.core.PConstants.CODED;

/**
 * Class that watches and serves individual key and mouse state at runtime.
 * This is a user-facing utility meant to be used through its Input.java wrapper
 * The GUI uses a different paralell UserInputPublisher to pass the processing events into the windows and control elements.
 */
public class InputWatcherBackend {

    private static InputWatcherBackend singleton;
    private static final List<Integer> codesToClearPressedOn = new ArrayList<>();
    private static final List<Integer> codesToClearReleasedOn = new ArrayList<>();
    private static final Map<Integer, KeyState> codeStates = new HashMap<>();
    private static final Map<Character, Integer> namedCodes = new HashMap<>();
    private static boolean debugKeys = false;
    private static final KeyState nullState = new KeyState(false, false, false);

    private InputWatcherBackend() {
        registerListeners();
        setupBasicCodeNames();
    }

    public static void initSingleton() {
        if (singleton == null) {
            singleton = new InputWatcherBackend();
        }
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
        recoverFromFocusLoss();
    }

    private void recoverFromFocusLoss() {
        if(!app.keyPressed){
            for(int code : codeStates.keySet()){
                KeyState state = codeStates.get(code);
                state.down = false;
            }
        }
    }

    public static PVector mousePos(){
        return new PVector(app.mouseX, app.mouseY);
    }

    public static PVector mousePosLastFrame(){
        return new PVector(app.pmouseX, app.pmouseY);
    }

    public static PVector mouseDelta() {
        return new PVector(app.mouseX - app.pmouseX, app.mouseY - app.pmouseY);
    }

    @SuppressWarnings("unused")
    public void keyEvent(KeyEvent event) {
        if (debugKeys) {
            println(keyEventString(event));
        }
        int action = event.getAction();
        if(action != KeyEvent.PRESS && event.getAction() != KeyEvent.RELEASE){
            return; // we only care about these two events, not the TYPE event
        }
        char key = event.getKey();
        int keyCode = event.getKeyCode();
        if (keyCode != 0 && key != CODED &&
                event.getModifiers() != Event.CTRL && // ctrl changes the reported key into some three letter abbrev
                !namedCodes.containsKey(key)) {
            namedCodes.put(key, keyCode);
        }
        if (!codeStates.containsKey(keyCode)) {
            codeStates.put(keyCode, new KeyState(false, false, false));
        }
        KeyState state = codeStates.get(keyCode);
        if (action == KeyEvent.PRESS) {
            state.down = true;
            state.pressed = true;
        } else if (action == KeyEvent.RELEASE) {
            state.down = false;
            state.released = true;
        }
    }

    public static KeyState getKeyStateByChar(Character keyName) {
        Integer code = namedCodes.get(keyName);
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

    public static List<String> getAllDownChars() {
        List<String> downChars = new ArrayList<>();
        List<Integer> knownDownCodes = new ArrayList<>();
        for (Character s : namedCodes.keySet()) {
            int code = namedCodes.get(s);
            if(getKeyStateByChar(s).down && !knownDownCodes.contains(code)){
                downChars.add(s.toString().toLowerCase(Locale.getDefault()));
                knownDownCodes.add(code);
            }
        }
        downChars.sort(String::compareToIgnoreCase);
        return downChars;
    }

    public static List<Integer> getAllDownCodes() {
        ArrayList<Integer> downCodes = new ArrayList<>();
        for (int code : codeStates.keySet()) {
            if(getKeyStateByCode(code).down){
                downCodes.add(code);
            }
        }
        downCodes.sort(Integer::compareTo);
        return downCodes;
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

    private void setupBasicCodeNames() {
        namedCodes.put(' ', (int) com.jogamp.newt.event.KeyEvent.VK_SPACE);
        namedCodes.put('a', (int) com.jogamp.newt.event.KeyEvent.VK_A);
        namedCodes.put('b', (int) com.jogamp.newt.event.KeyEvent.VK_B);
        namedCodes.put('c', (int) com.jogamp.newt.event.KeyEvent.VK_C);
        namedCodes.put('d', (int) com.jogamp.newt.event.KeyEvent.VK_D);
        namedCodes.put('e', (int) com.jogamp.newt.event.KeyEvent.VK_E);
        namedCodes.put('f', (int) com.jogamp.newt.event.KeyEvent.VK_F);
        namedCodes.put('g', (int) com.jogamp.newt.event.KeyEvent.VK_G);
        namedCodes.put('h', (int) com.jogamp.newt.event.KeyEvent.VK_H);
        namedCodes.put('i', (int) com.jogamp.newt.event.KeyEvent.VK_I);
        namedCodes.put('j', (int) com.jogamp.newt.event.KeyEvent.VK_J);
        namedCodes.put('k', (int) com.jogamp.newt.event.KeyEvent.VK_K);
        namedCodes.put('l', (int) com.jogamp.newt.event.KeyEvent.VK_L);
        namedCodes.put('m', (int) com.jogamp.newt.event.KeyEvent.VK_M);
        namedCodes.put('n', (int) com.jogamp.newt.event.KeyEvent.VK_N);
        namedCodes.put('o', (int) com.jogamp.newt.event.KeyEvent.VK_O);
        namedCodes.put('p', (int) com.jogamp.newt.event.KeyEvent.VK_P);
        namedCodes.put('q', (int) com.jogamp.newt.event.KeyEvent.VK_Q);
        namedCodes.put('r', (int) com.jogamp.newt.event.KeyEvent.VK_R);
        namedCodes.put('s', (int) com.jogamp.newt.event.KeyEvent.VK_S);
        namedCodes.put('t', (int) com.jogamp.newt.event.KeyEvent.VK_T);
        namedCodes.put('u', (int) com.jogamp.newt.event.KeyEvent.VK_U);
        namedCodes.put('v', (int) com.jogamp.newt.event.KeyEvent.VK_V);
        namedCodes.put('w', (int) com.jogamp.newt.event.KeyEvent.VK_W);
        namedCodes.put('x', (int) com.jogamp.newt.event.KeyEvent.VK_X);
        namedCodes.put('y', (int) com.jogamp.newt.event.KeyEvent.VK_Y);
        namedCodes.put('z', (int) com.jogamp.newt.event.KeyEvent.VK_Z);
    }
}
