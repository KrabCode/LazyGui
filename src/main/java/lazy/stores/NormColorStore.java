package lazy.stores;

import processing.core.PGraphics;

import static processing.core.PConstants.HSB;
import static processing.core.PConstants.P2D;

public class NormColorStore {

    private static PGraphics colorStore = null;

    public static void init() {
        colorStore = GlobalReferences.app.createGraphics(256, 256, P2D);
        colorStore.colorMode(HSB, 1, 1, 1, 1);
    }

    public static int color(float br) {
        return color(0, 0, br, 1);
    }

    public static int color(float br, float alpha) {
        return color(0, 0, br, alpha);
    }

    public static int color(float hue, float sat, float br) {
        return color(hue, sat, br, 1);
    }

    public static int color(float hue, float sat, float br, float alpha) {
        return colorStore.color(hue, sat, br, alpha);
    }

    public static float red(int hex){
        return colorStore.red(hex);
    }

    public static float green(int hex){
        return colorStore.green(hex);
    }

    public static float blue(int hex){
        return colorStore.blue(hex);
    }

    public static float hue(int hex){ return colorStore.hue(hex); }
    public static float sat(int hex){ return colorStore.saturation(hex); }
    public static float br(int hex){ return colorStore.brightness(hex); }

    public static float alpha(int hex) {
        return colorStore.alpha(hex);
    }

    public static PGraphics getColorStore() {
        return colorStore;
    }
}
