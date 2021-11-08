package toolbox;

import processing.core.PVector;

public class Math {
    public static boolean isPointInRect(float x, float y, PVector pos, PVector size) {
        return isPointInRect(x, y, pos.x, pos.y, size.x, size.y);
    }

    public static boolean isPointInRect(float px, float py, float rx, float ry, float rw, float rh) {
        return px >= rx && px <= rx + rw && py >= ry && py <= ry + rh;
    }
}
