package com.krab.lazy;

import com.krab.lazy.nodes.ColorPickerFolderNode;
import com.krab.lazy.stores.NormColorStore;
import processing.core.PApplet;

/**
 * Data transfer object for ColorPicker value.
 * Hue, saturation, brightness and alpha are normalized to a range of [0,1].
 * The hex value can be used in any processing colorMode, it will always show the correct color.
 * This object's fields are final, if you want to change the ColorPicker from code, use LazyGui.colorPickerSet instead.
 * Please note that ColorPicker tries to avoid hex values of exactly 0 because
 * they are not transparent in processing even though the alpha is 0, which is probably a bug.
 * It returns 0x00010101 instead which looks perfectly transparent in processing.
 * @see ColorPickerFolderNode
 */
public class PickerColor {
    /**
     * Integer representation of a processing hex color. Can be passed to fill(), stroke() directly in any colorMode.
     * See: <a href="https://processing.org/reference/color_datatype.html">Processing color datatype</a>
     */
    public final int hex;

    /**
     * Hue in range of [0,1]
     */
    public final float hue;

    /**
     * Saturation in range of [0,1]
     */
    public final float saturation;

    /**
     * Brightness in range of [0,1]
     */
    public final float brightness;

    /**
     * Alpha in range of [0,1]
     */
    public final float alpha;

    /**
     * Simple constructor that just assigns the parameters into the corresponding fields.
     * @param hex processing int color
     * @param hue hue in range of [0,1]
     * @param sat saturation in range of [0,1]
     * @param br brightness in range of [0,1]
     * @param alpha alpha in range of [0,1]
     */
    public PickerColor(int hex, float hue, float sat, float br, float alpha){
        this.hex = hex;
        this.hue = hue;
        this.saturation = sat;
        this.brightness = br;
        this.alpha = alpha;
    }

    /**
     * Utility constructor that gets all the other HSB color data from the hex int color,
     * but this can be CPU intensive when done too much.
     *
     * @param hex processing integer color to parse as HSBA
     */
    public PickerColor(int hex) {
        this.hex = hex;
        this.hue = NormColorStore.hue(hex);
        this.saturation = NormColorStore.sat(hex);
        this.brightness = NormColorStore.br(hex);
        this.alpha = NormColorStore.alpha(hex);
    }

    public String toString() {
        return "PickerColor{" +
                "hexInt=" + hex +
                ", hexString=" + PApplet.hex(hex) +
                ", hue=" + hue +
                ", saturation=" + saturation +
                ", brightness=" + brightness +
                ", alpha=" + alpha +
                '}';
    }
}
