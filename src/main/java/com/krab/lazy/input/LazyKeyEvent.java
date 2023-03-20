package com.krab.lazy.input;

import processing.event.KeyEvent;

@SuppressWarnings("unused")
public class LazyKeyEvent {
    private boolean consumed = false;
    private final boolean isShiftDown;
    private final boolean isControlDown;
    private final boolean isAltDown;
    private final int keyCode;
    private final char key;

    public LazyKeyEvent(KeyEvent e) {
        this.isShiftDown = e.isShiftDown();
        this.isControlDown = e.isControlDown();
        this.isAltDown = e.isAltDown();
        this.keyCode = e.getKeyCode();
        this.key = e.getKey();
//        println(toString());
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void consume() {
        consumed = true;
    }

    public char getKey() {
        return key;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public boolean isShiftDown() {
        return isShiftDown;
    }

    public boolean isControlDown() {
        return isControlDown;
    }

    public boolean isAltDown() {
        return isAltDown;
    }


    public String toString() {
        return super.toString() + "\t| keyCode: " + keyCode + "\t| key: " + key + "\t| consumed: " + consumed + "\t| " +
                (isShiftDown ? "shift " : "") + (isControlDown ? "control " : "") + (isAltDown ? "alt " : "");
    }
}
