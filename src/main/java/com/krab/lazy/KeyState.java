package com.krab.lazy;

/**
 * Data transfer object for representing a keyboard button state.
 */
public class KeyState {
    /**
     * True during any frame the button is physically down in the active position.
     */
    public boolean down;

    /**
     * True for one frame after the button is pushed down to its active position.
     */
    public boolean pressed;

    /**
     * True for one frame after the button is released up from its active position into the idle position.
     */
    public boolean released;

    /**
     * What the frameCount was equal to last time <code>pressed</code> was true for this key or keyCode.
     */
    public int frameReleased = -Integer.MAX_VALUE;

    /**
     * What the frameCount was equal to last time <code>released</code> was true for this key or keyCode.
     */
    public int framePressed = -Integer.MAX_VALUE;

    /**
     * Used internally by the InputWatcherBackend. Not meant to be user-facing.
     * @param down is the key currently down?
     * @param pressed was the key just pressed?
     * @param released was the key just released?
     */
    public KeyState(boolean down, boolean pressed, boolean released) {
        this.down = down;
        this.pressed = pressed;
        this.released = released;
    }
}
