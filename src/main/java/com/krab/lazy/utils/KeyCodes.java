package com.krab.lazy.utils;


import com.jogamp.newt.event.KeyEvent;

import java.util.ArrayList;

public class KeyCodes {
    public static final int DELETE = KeyEvent.VK_DELETE;
    public static final int C = KeyEvent.VK_C;
    public static final int V = KeyEvent.VK_V;
    public static final int Z = KeyEvent.VK_Z;
    public static final int Y = KeyEvent.VK_Y;
    public static final int S = KeyEvent.VK_S;
    public static final int CTRL = KeyEvent.VK_CONTROL;
    public static final int ALT = KeyEvent.VK_ALT;
    public static final int SHIFT = KeyEvent.VK_SHIFT;
    public static final int SHIFT_TWO = 16;
    private static final ArrayList<Integer> textInputIgnoredKeyCodes = new ArrayListBuilder<Integer>().add(CTRL, ALT, SHIFT, SHIFT_TWO).build();
    public static boolean shouldIgnoreForTextInput(int keyCode){
        return textInputIgnoredKeyCodes.contains(keyCode);
    }
}
