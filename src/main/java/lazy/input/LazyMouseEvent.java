package lazy.input;

import static lazy.stores.GlobalReferences.app;
import static processing.core.PConstants.CENTER;

public class LazyMouseEvent {
    private boolean consumed;
    private final float x, y, px, py;
    private final int scrollWheelRotation;
    private final int button;

    LazyMouseEvent(float x, float y, float px, float py, int button) {
        scrollWheelRotation = 0;
        this.x = x;
        this.y = y;
        this.px = px;
        this.py = py;
        this.button = button;
    }

    LazyMouseEvent(int scrollWheelRotation) {
        this.scrollWheelRotation = scrollWheelRotation;
        x = app.mouseX;
        y = app.mouseY;
        px = app.pmouseX;
        py = app.pmouseY;
        button = CENTER;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getPrevX(){
        return px;
    }

    public float getPrevY(){
        return py;
    }

    public void setConsumed(boolean valueToSet) {
        consumed = valueToSet;
    }

    public int getRotation() {
        return scrollWheelRotation;
    }

    @Override
    public String toString() {
        return "x " + x + " | " + "px " + px + " | " + "y " + y + " | " + "py " + py;
    }

    public int getButton() {
        return button;
    }
}
