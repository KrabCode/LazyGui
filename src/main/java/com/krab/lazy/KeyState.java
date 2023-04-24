package com.krab.lazy;

public class KeyState {
    public boolean down;
    public boolean press;
    public boolean release;

    public KeyState(boolean down, boolean press, boolean release) {
        this.down = down;
        this.press = press;
        this.release = release;
    }
}
