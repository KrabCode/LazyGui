package com.krab.lazy.stores;

public class DelayStore {
    private static int keyboardBufferDelayMillis = 500;

    public static int getKeyboardBufferDelayMillis() {
        return keyboardBufferDelayMillis;
    }

    public static void setKeyboardBufferDelayMillis(int keyboardBufferDelayMillis) {
        DelayStore.keyboardBufferDelayMillis = keyboardBufferDelayMillis;
    }

    public static void updateInputDelay() {
        GlobalReferences.gui.pushFolder("keyboard delay");
        DelayStore.setKeyboardBufferDelayMillis(
                GlobalReferences.gui.sliderInt("delay (ms)", DelayStore.getKeyboardBufferDelayMillis(), 100, 5000));

        if( GlobalReferences.gui.button("read more")){
            GlobalReferences.gui.textSet("", "the buffer delay slider sets the time\n" +
                    "that must elapse after the last keyboard input has been made\n" +
                    "before the new value overwrites any previous content\n" +
                    "in the currently moused over text / slider\n" +
                    "in order to avoid jarring sketch changes with each keystroke"
            );
        }
        GlobalReferences.gui.popFolder();
    }
}
