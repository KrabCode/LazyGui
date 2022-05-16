package toolbox.global;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PVector;
import toolbox.windows.nodes.AbstractNode;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

import static processing.core.PApplet.*;

public class Utils {

    public static void setClipboardString(String data) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(data);
        clipboard.setContents(selection, selection);
    }

    public static String getClipboardString() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            return (String) clipboard.getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static boolean isPointInRect(float x, float y, PVector pos, PVector size) {
        return isPointInRect(x, y, pos.x, pos.y, size.x, size.y);
    }

    public static boolean isPointInRect(float px, float py, float rx, float ry, float rw, float rh) {
        return px > rx && px < rx + rw && py >= ry && py <= ry + rh;
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
    public static float hash(float seed) {
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


    public static String getLibraryPath() {
        URL url = State.app.getClass().getResource(State.class.getSimpleName() + ".class");
        if (url != null) {
            // Convert URL to string, taking care of spaces represented by the "%20"
            // string.
            String path = url.toString().replace("%20", " ");
            println("0: " + path);
            if (!path.contains(".jar"))
                return State.app.sketchPath();

            int n0 = path.indexOf('/');

            int n1;

            // read jar file name
            String fullJarPath = Utils.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath();
            println("1: " + fullJarPath);
            if (PApplet.platform == PConstants.WINDOWS) {

                // remove leading slash in windows path
                fullJarPath = fullJarPath.substring(1);
                println("2: " + fullJarPath);
            }

            String jar = Paths.get(fullJarPath).getFileName().toString();
            println("3: " + jar);
            n1 = path.indexOf(jar);
            if (PApplet.platform == PConstants.WINDOWS) {
                // remove leading slash in windows path
                println("4");
                n0++;
            }


            if ((-1 < n0) && (-1 < n1)) {
                println("5: " + path.substring(n0, n1));
                return path.substring(n0, n1);
            } else {
                return State.app.sketchPath();
            }
        }
        return State.app.sketchPath();
    }

    public static String getPathWithoutName(String pathWithName) {
        String[] split = pathWithName.split("/");
        StringBuilder sum = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) {
            sum.append(split[i]);
            if (i < split.length - 2) {
                sum.append("/");
            }
        }
        return sum.toString();
    }

    @SuppressWarnings("unused")
    private static void printAvailableFonts() {
        String[] fontList = PFont.list();
        for (String s :
                fontList) {
            println(s);
        }
    }

    public static String timestamp() {
        return year() + "-"
                + nf(month(), 2) + "-"
                + nf(day(), 2) + "_"
                + nf(hour(), 2) + "."
                + nf(minute(), 2) + "."
                + nf(second(), 2);
    }

    public static String inputDialog(String msg) {
        JFrame frame = new JFrame();
        frame.setAlwaysOnTop(true);
        frame.requestFocus();
        return JOptionPane.showInputDialog(frame, msg);
    }
}
