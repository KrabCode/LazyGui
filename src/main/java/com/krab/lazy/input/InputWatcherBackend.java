package com.krab.lazy.input;

import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.HashMap;

import static com.krab.lazy.stores.GlobalReferences.app;

public class InputWatcherBackend {
    
    private static InputWatcherBackend singleton;

    static HashMap<Integer, Boolean> stateByCodes = new HashMap<>();
    static HashMap<Character, Boolean> stateByChars = new HashMap<>();

    public static void initSingleton() {
        if (singleton == null) {
            singleton = new InputWatcherBackend();
        }
    }

    private InputWatcherBackend() {
        registerListeners();
    }


    public static boolean getKeyStateByChar(char keyChar) {
        if(!stateByChars.containsKey(keyChar)){
            return false;
        }
        return stateByChars.get(keyChar);
    }

    public static boolean getKeyStateByCode(int keyCode) {
        if(!stateByCodes.containsKey(keyCode)){
            return false;
        }
        return stateByCodes.get(keyCode);
    }

    private void registerListeners() {
        // the reference passed here is the only reason to have this be a singleton instance rather than a fully static class with no instance
        // app.registerMethod("mouseEvent", this);
        app.registerMethod("keyEvent", this);
    }

    @SuppressWarnings("unused")
    public void mouseEvent(MouseEvent event) {
        // PApplet.println(event);
    }

    @SuppressWarnings("unused")
    public void keyEvent(KeyEvent event){
        // PApplet.println(keyEventString(event));
        if(event.getAction() == KeyEvent.PRESS){
            stateByCodes.put(event.getKeyCode(), true);
            stateByChars.put(event.getKey(), true);
        }
        if(event.getAction() == KeyEvent.RELEASE){
            stateByCodes.put(event.getKeyCode(), false);
            stateByChars.put(event.getKey(), false);
        }
    }

    String keyEventString(KeyEvent event){
        String actionString;
        switch(event.getAction()){
            case KeyEvent.PRESS: actionString = "PRESS"; break;
            case KeyEvent.TYPE: actionString = "TYPE"; break;
            case KeyEvent.RELEASE: actionString = "RELEASE"; break;
            default: actionString = "UNKNOWN (" + event.getAction() + ")";
        }
        return String.format("<KeyEvent | action: %s| key: %s| code: %s>",
                padString(actionString, "RELEASE ".length()),
                padString(String.valueOf(event.getKey()), 2),
                padString(String.valueOf(event.getKeyCode()), 3));
    }

    String padString(String toPad, int count){
        StringBuilder sb = new StringBuilder(toPad);
        for (int i = sb.length(); i < count; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }
}
