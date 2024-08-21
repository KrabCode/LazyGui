package com.krab.lazy.nodes;

/**
 * Enum for gradient blend types.
 * DO NOT CHANGE THE INDEXES OR NAMES OF THE ENUMS. IF YOU DO, DO THE SAME THING IN THE SHADER (data/shaders/gradient.glsl).
 * Changing the indexes will affect (break) the corresponding gradient.glsl shader which is doing the actual gradient blending.
 * Changing the names will break setting them from the constructor settings via the string parameter.
 * The indexes are sent to the shader as integer uniforms to determine the blend type.
 */
public enum GradientBlendType {
    MIX(0, "mix"),
    HSV(1, "hsv"),
    OKLAB(2, "oklab");

    public final String name;
    public final int index;

    GradientBlendType(int index, String name) {
        this.index = index;
        this.name = name;
    }

    static String[] getNamesAsList() {
        return new String[]{
                MIX.name,
                HSV.name,
                OKLAB.name
        };
    }

    static int getIndexByName(String name) {
        for (GradientBlendType value : values()) {
            if (value.name.equals(name)) {
                return value.index;
            }
        }
        return -1;
    }
}
