import com.krab.lazy.*;

LazyGui gui;
PGraphics canvas;
PickerColor circleColor;
PickerColor lineColor;
float lineWeight, circleSize;

void setup() {
  size(800, 800, P2D);
  gui = new LazyGui(this);
  canvas = createGraphics(width, height);
  colorMode(HSB, 1, 1, 1, 1);
  clearCanvas();
  noStroke();
}

void draw() {
  gui.pushFolder("drawing");
  circleColor = gui.colorPicker("circle color", color(0));
  circleSize = gui.slider("circle size", 75);
  lineColor = gui.colorPicker("line color", color(1, 0.5, 1));
  gui.colorPickerHueAdd("line color", radians(gui.slider("line hue +", 0.5f)));
  lineWeight = gui.slider("line weight", 50);
  gui.popFolder();
  if (gui.button("clear")) {
    clearCanvas();
  }
  image(canvas, 0, 0);
}

void mousePressed() {
  if (gui.isMouseOutsideGui()) {
    drawCircleAtMouse();
  }
}

void mouseReleased() {
  if (gui.isMouseOutsideGui()) {
    drawCircleAtMouse();
  }
}

void mouseDragged() {
  if (gui.isMouseOutsideGui()) {
    drawLineAtMouse();
  }
}

void clearCanvas() {
  canvas.beginDraw();
  canvas.background(gui.colorPicker("background", color(50)).hex);
  canvas.endDraw();
}

void drawCircleAtMouse() {
  canvas.beginDraw();
  canvas.noStroke();
  canvas.fill(circleColor.hex);
  canvas.ellipse(mouseX, mouseY, circleSize, circleSize);
  canvas.endDraw();
}

void drawLineAtMouse() {
  canvas.beginDraw();
  canvas.stroke(lineColor.hex);
  canvas.strokeWeight(lineWeight);
  canvas.line(pmouseX, pmouseY, mouseX, mouseY);
  canvas.endDraw();
}
