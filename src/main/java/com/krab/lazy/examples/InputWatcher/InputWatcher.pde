import com.krab.lazy.*;
import java.util.List;

LazyGui gui;
int nextBackgroundColor = 0xFF0F0F0F;
int currentBackgroundColor = 0xFF0F0F0F;

@Override
  public void settings() {
  size(800, 800, P2D);
}

@Override
  public void setup() {
  gui = new LazyGui(this);
  colorMode(HSB, 1, 1, 1, 1);
}

@Override
  public void draw() {
  Input.debugPrintKeyEvents(gui.toggle("debug keys"));
  drawBackground();
  fill(1);
  debugViewKeysDown();
  detectCtrlSpacePress();
}

private void debugViewKeysDown() {
  List<String> downChars = Input.getAllDownChars();
  List<Integer> downCodes = Input.getAllDownCodes();
  String textContent = "chars: " + downChars + "\ncodes: " + downCodes;
  fill(0, 0.5f);
  noStroke();
  rectMode(CORNER);
  float bgWidth = textWidth(textContent) + 25;
  float bgHeight = 75;
  rect(0, height - bgHeight, bgWidth, bgHeight);
  fill(0.75f);
  textFont(gui.getMainFont());
  textAlign(LEFT, BOTTOM);
  text(textContent, 10, height - 10);
}

private void detectCtrlSpacePress() {
  KeyState control = Input.getCode(CONTROL);
  KeyState space = Input.getChar(' ');
  translate(width / 2f, height / 2f);
  fill(1);
  noStroke();
  rectMode(CENTER);
  if (control.down) {
    rect(-10, 8, 40, 2);
  }
  if (space.down) {
    rect(65, 8, 50, 2);
  }
  fill(1);
  if (control.down && space.pressed) {
    nextBackgroundColor = color(random(1), random(1), random(0.1f, 0.5f));
  }
  if (control.down && space.framePressed > 0) {
    float fadeoutDuration = 60;
    float timeSinceCtrlSpacePressedNormalized = constrain(
      norm(frameCount, space.framePressed, space.framePressed + fadeoutDuration),
      0, 1
      );
    currentBackgroundColor = lerpColor(
      currentBackgroundColor,
      nextBackgroundColor,
      timeSinceCtrlSpacePressedNormalized
      );
  }
  String guide = "Press CTRL + Space";
  textAlign(CENTER);
  text(guide, 0, 0);
}

private void drawBackground() {
  fill(currentBackgroundColor);
  noStroke();
  rectMode(CORNER);
  rect(0, 0, width, height);
}