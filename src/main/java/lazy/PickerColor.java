package lazy;

/**
 * Data transfer object for ColorPicker value.
 * Hue, saturation, brightness and alpha are normalized to a range of [0,1].
 * The hex value can be used in any processing colorMode, and it will show the correct color.
 * This object's fields are final, if you want to change the ColorPicker from code, use LazyGui.colorPickerSet instead.
 * Please note that ColorPicker tries to avoid hex values of exactly 0 because
 * they are not transparent in processing even though the alpha is 0, which is probably a bug.
 * It returns 0x00010101 instead which looks perfectly transparent in processing.
 * @see ColorPickerFolder
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

    PickerColor(int hex, float hue, float sat, float br, float alpha){
        this.hex = hex;
        this.hue = hue;
        this.saturation = sat;
        this.brightness = br;
        this.alpha = alpha;
    }
}
