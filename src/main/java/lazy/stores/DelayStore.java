package lazy.stores;

import static lazy.stores.GlobalReferences.gui;

public class DelayStore {
    private static int keyboardBufferDelayMillis = 500;

    public static int getKeyboardBufferDelayMillis() {
        return keyboardBufferDelayMillis;
    }

    public static void setKeyboardBufferDelayMillis(int keyboardBufferDelayMillis) {
        DelayStore.keyboardBufferDelayMillis = keyboardBufferDelayMillis;
    }

    public static void updateInputDelay() {
        gui.pushFolder("keyboard delay");
        DelayStore.setKeyboardBufferDelayMillis(
                gui.sliderInt("keyboard delay (ms)", DelayStore.getKeyboardBufferDelayMillis(), 100, 5000));

        if( gui.button("read more")){
            gui.textSet("", "the buffer delay slider sets the time\n" +
                    "that must elapse after the last keyboard input has been made\n" +
                    "before the new value overwrites any previous content\n" +
                    "in the currently moused over text / slider\n" +
                    "in order to avoid jarring sketch changes with each keystroke"
            );
        }
        gui.popFolder();
    }
}
