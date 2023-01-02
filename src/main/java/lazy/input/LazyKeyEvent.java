package lazy.input;

public class LazyKeyEvent {
    private boolean consumed = false;
    private final int keyCode;
    private final char keyChar;

    public LazyKeyEvent(int keyCode, char keyChar) {
        this.keyCode = keyCode;
        this.keyChar = keyChar;
    }

    boolean isConsumed() {
        return consumed;
    }

    public void consume(){
        consumed = true;
    }

    public char getKeyChar() {
        return keyChar;
    }

    public int getKeyCode(){
        return keyCode;
    }

    public String toString(){
        return super.toString() + "\t| keyCode: " + keyCode + " | keyChar: " + keyChar + " | consumed: " + consumed;
    }
}
