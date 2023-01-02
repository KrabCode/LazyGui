package lazy;

import processing.core.PGraphics;

import static processing.core.PConstants.HSB;
import static processing.core.PConstants.P2D;

public class ColorStore {

    private static PGraphics colorStore = null;

    static void init() {
        colorStore = Globals.app.createGraphics(256, 256, P2D);
        colorStore.colorMode(HSB, 1, 1, 1, 1);
    }

    static int normColor(float br) {
        return normColor(0, 0, br, 1);
    }

    @SuppressWarnings("SameParameterValue")
    static int normColor(float br, float alpha) {
        return normColor(0, 0, br, alpha);
    }

    @SuppressWarnings("unused")
    static int normColor(float hue, float sat, float br) {
        return normColor(hue, sat, br, 1);
    }

    static int normColor(float hue, float sat, float br, float alpha) {
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
