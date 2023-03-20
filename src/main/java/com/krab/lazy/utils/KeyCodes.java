package com.krab.lazy.utils;


import java.util.ArrayList;

@SuppressWarnings("CommentedOutCode")
public class KeyCodes {
    public static final int CTRL = 17;
    public static final int ALT = 18;
    public static final int SHIFT = 16;
    private static final ArrayList<Integer> textInputIgnoredKeyCodes = new ArrayListBuilder<Integer>().add(CTRL).add(ALT).add(SHIFT).build();
    public static final int DELETE = 147;
    public static final int C = 67;
    public static final int V = 86;
    public static final int Z = 89;
    public static final int Y = 90;
    public static final int S = 83;
    public static final int F = 70;

    public static boolean shouldIgnoreForTextInput(int keyCode){
        return textInputIgnoredKeyCodes.contains(keyCode);
    }

/*
 // find keycodes in processing:
 // note that a key is not in the usual char form when a modifier key is down, using keycodes is better

    String defaultText = "...";
    String toPrint = defaultText;

    void setup() {
      size(640, 480);
      rectMode(CENTER);
      textSize(64);
      textAlign(CENTER, CENTER);
      stroke(25);
      strokeWeight(5);
    }

    void draw() {
      background(100);
      translate(width * 0.5, height * 0.5);
      if (!toPrint.equals(defaultText)) {
        fill(0);
        rect(0, 0, width * 0.5, height * 0.5);
      }
      fill(255);
      text(toPrint, 0, -10);
    }

    void keyPressed() {
      println(keyCode);
      toPrint = "code: " + keyCode + "\n" +
        "key: " + key;
    }

 */
}
