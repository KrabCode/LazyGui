package com.krab.lazy;

/**
 * Used as a parameter for initializing a gradient with a set of default colors at default positions.
 * @see LazyGui#gradient(String, ColorPoint...) gradient function where it is used
 */
public class ColorPoint{
    int color; //
    float position; //

    /**
     * Constructor for ColorPoint
     * @param color processing hex color like 0xFF00FF00 or color(255, 0, 0)
     * @param position expected to be in the range [0, 1]
     * @see LazyGui#colorPoint(int, float) more convenient way to create a ColorPoint.
     */
    public ColorPoint(int color, float position){
        this.color = color;
        this.position = position;
    }
}