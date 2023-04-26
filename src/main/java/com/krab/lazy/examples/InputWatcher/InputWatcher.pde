import java.util.List;

LazyGui gui;

void setup() {
    size(800, 800, P2D);
    colorMode(HSB,1,1,1,1);
    gui = new LazyGui(this);
}

void draw() {
    Input.debugPrintKeyEvents(gui.toggle("debug keys"));
    drawBackground();
    drawTexts();
    detectCtrlSpacePress();
}

void detectCtrlSpacePress() {
    boolean isControlDown = Input.getCode(CONTROL).down;
    boolean spaceWasJustPressed = Input.getChar(' ').pressed;
    if(isControlDown && spaceWasJustPressed){
        println("ctrl + space pressed");
    }
}

void drawTexts() {
    fill(0.75f);
    textFont(gui.getMainFont());
    textAlign(LEFT, BOTTOM);
    List<String> downChars = Input.getAllDownChars();
    List<Integer> downCodes = Input.getAllDownCodes();
    String textContent = "chars: " + downChars + "\ncodes: " + downCodes;
    text(textContent, 10, height-10);
}

void drawBackground() {
    fill(gui.colorPicker("background", 0xFF0F0F0F).hex);
    noStroke();
    rectMode(CORNER);
    rect(0, 0, width, height);
}