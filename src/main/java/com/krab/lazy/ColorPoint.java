package com.krab.lazy;

/**
 * Used for initializing a gradient with a set of default colors at default positions.
 * See LazyGui.gradient(String, ColorPoint...) for more information.
 */
public class ColorPoint{
    int color; // processing hex color, e.g. 0xFF00FF00
    float position; // expected to be normalized to [0, 1]

    ColorPoint(int color, float position){
        this.color = color;
        this.position = position;
    }
}