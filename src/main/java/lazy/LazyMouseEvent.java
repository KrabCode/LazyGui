package lazy;

import static lazy.Globals.app;

class LazyMouseEvent {
    private boolean consumed;
    private final float x, y, px, py;
    private final int scrollWheelRotation;

    LazyMouseEvent(float x, float y, float px, float py) {
        scrollWheelRotation = 0;
        this.x = x;
        this.y = y;
        this.px = px;
        this.py = py;
    }

    LazyMouseEvent(int scrollWheelRotation) {
        this.scrollWheelRotation = scrollWheelRotation;
        x = app.mouseX;
        y = app.mouseY;
        px = app.pmouseX;
        py = app.pmouseY;
    }

    boolean isConsumed() {
        return consumed;
    }

    float getX() {
        return x;
    }

    float getY() {
        return y;
    }

    float getPrevX(){
        return px;
    }

    float getPrevY(){
        return py;
    }

    void setConsumed(boolean valueToSet) {
        consumed = valueToSet;
    }

    int getRotation() {
        return scrollWheelRotation;
    }

    @Override
    public String toString() {
        return "x " + x + " | " + "px " + px + " | " + "y " + y + " | " + "py " + py;
    }
}
