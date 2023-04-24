package com.krab.lazy;

public class InputKeyState {
    public boolean down;
    public boolean pressed;
    public boolean released;
    public int frameReleased = -Integer.MAX_VALUE;
    public int framePressed = -Integer.MAX_VALUE;

    public InputKeyState(boolean down, boolean pressed, boolean released) {
        this.down = down;
        this.pressed = pressed;
        this.released = released;
    }
}
