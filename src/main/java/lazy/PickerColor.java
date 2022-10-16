package lazy;

public class PickerColor {
    public final int hex;

    // [0,1]
    public final float hue;
    public final float saturation;
    public final float brightness;
    public final float alpha;

    PickerColor(int hex, float hue, float sat, float br, float alpha){
        this.hex = hex;
        this.hue = hue;
        this.saturation = sat;
        this.brightness = br;
        this.alpha = alpha;
    }
}
