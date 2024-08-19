package com.krab.lazy.nodes;

/**
 * Enum for gradient blend types.
 * DO NOT CHANGE THE INDEXES OF THE ENUMS.
 * Changing the indexes will affect (break) the corresponding gradient.glsl shader which is doing the actual gradient blending.
 * The indexes are sent to the shader as integer uniforms to determine the blend type.
 */
public enum GradientBlendType {
    MIX(0, "mix"),
    RGB(1, "rgb"),
    HSV(2, "hsv"),
    OKLAB(3, "oklab");

    public final String name;
    public final int index;

    GradientBlendType(int index, String name) {
        this.index = index;
        this.name = name;
    }

    static String[] getNamesAsList() {
        return new String[]{
                MIX.name,
                RGB.name,
                HSV.name,
                OKLAB.name
        };
    }

    static int getIndexByName(String valueString) {
        for (GradientBlendType value : values()) {
            if (value.name.equals(valueString)) {
                return value.index;
            }
        }
        return -1;
    }
}
