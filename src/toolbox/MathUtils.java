package toolbox;

import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PApplet.*;

public class MathUtils {
    public static boolean isPointInRect(float x, float y, PVector pos, PVector size) {
        return isPointInRect(x, y, pos.x, pos.y, size.x, size.y);
    }

    public static boolean isPointInRect(float px, float py, float rx, float ry, float rw, float rh) {
        return px >= rx && px <= rx + rw && py >= ry && py <= ry + rh;
    }



    /**
     * Takes any float and returns the positive fractional part of it, so the result is always between 0 and 1.
     * For example -0.1 becomes 0.1 and 1.5 becomes 0.5. Used with hue due to its cyclical
     * nature.
     *
     * @param hue float to apply modulo to
     * @return float in the range [0,1)
     */
    protected static float hueModulo(float hue) {
        while (hue < 0) {
            hue += 1;
        }
        hue %= 1;
        return hue;
    }

    /**
     * Returns the number of digits in a floored number. Useful for approximating the most useful default precision
     * of a slider.
     *
     * @param inputNumber number to floor and check the size of
     * @return number of digits in floored number
     */
    public static int numberOfDigitsInFlooredNumber(float inputNumber) {
        return String.valueOf(Math.floor(inputNumber)).length();
    }

    /**
     * A random function that always returns the same number for the same seed.
     *
     * @param seed seed to use
     * @return hash value between 0 and 1
     */
    protected static  float hash(float seed) {
        return (float) (Math.abs(Math.sin(seed * 323.121f) * 454.123f) % 1);
    }


    /**
     * Constructs a random square image url with the specified size.
     *
     * @param size image width to request
     * @return random square image
     */
    public static String randomImageUrl(float size) {
        return randomImageUrl(size, size);
    }

    /**
     * Constructs a random image url with the specified size.
     *
     * @param width  image width to request
     * @param height image height to request
     * @return random image url
     */
    public static String randomImageUrl(float width, float height) {
        return "https://picsum.photos/" + Math.floor(width) + "/" + Math.floor(height) + ".jpg";
    }

    protected static float clampNorm(float x, float min, float max) {
        return constrain(PApplet.norm(x, min, max), 0, 1);
    }

    @SuppressWarnings("unused")
    protected static float clampMap(float x, float xMin, float xMax, float min, float max) {
        return constrain(map(x, xMin, xMax, min, max), min, max);
    }

    /**
     * Returns the angular diameter of a circle with radius 'r' on the edge of a circle with radius 'size'.
     *
     * @param r    the radius of the circle to check the angular diameter of
     * @param size the radius that the circle rests on the edge of
     * @return angular diameter of r at radius size
     */
    public static float angularDiameter(float r, float size) {
        return (float) Math.atan(2 * (size / (2 * r)));
    }


}
