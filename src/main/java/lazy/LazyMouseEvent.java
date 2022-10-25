package lazy;

public class LazyMouseEvent {
    private boolean consumed;
    private final float x, y, px, py;
    private final int scrollWheelRotation;

    public LazyMouseEvent() {
        scrollWheelRotation = 0;
        x = State.app.mouseX;
        y = State.app.mouseY;
        px = State.app.pmouseX;
        py = State.app.pmouseY;
    }

    public LazyMouseEvent(int scrollWheelRotation) {
        this.scrollWheelRotation = scrollWheelRotation;
        x = State.app.mouseX;
        y = State.app.mouseY;
        px = State.app.pmouseX;
        py = State.app.pmouseY;
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
}
