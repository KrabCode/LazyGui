package lazy;

import processing.core.PGraphics;

import static processing.core.PConstants.HSB;
import static processing.core.PConstants.P2D;

public class NormColorStore {

    private static PGraphics colorStore = null;

    static void init() {
        colorStore = Globals.app.createGraphics(256, 256, P2D);
        colorStore.colorMode(HSB, 1, 1, 1, 1);
    }

    static int color(float br) {
        return color(0, 0, br, 1);
    }

    static int color(float br, float alpha) {
        return color(0, 0, br, alpha);
    }

    static int color(float hue, float sat, float br) {
        return color(hue, sat, br, 1);
    }

    static int color(float hue, float sat, float br, float alpha) {
        return colorStore.color(hue, sat, br, alpha);
    }

    static float red(int hex){
        return colorStore.red(hex);
    }

    static float green(int hex){
        return colorStore.green(hex);
    }

    static float blue(int hex){
        return colorStore.blue(hex);
    }

    public static PGraphics getColorStore() {
        return colorStore;
    }

}
