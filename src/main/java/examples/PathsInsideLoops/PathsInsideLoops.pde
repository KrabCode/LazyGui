
LazyGui gui;

void setup() {
  size(1200, 800, P2D);
  smooth(8);
  rectMode(CENTER);
  textAlign(LEFT, TOP);
  textSize(32);
  gui = new LazyGui(this);
}

void draw() {
  gui.pushFolder("scene");
  background(gui.colorPicker("background", color(0xFF252525)).hex);
  drawForeground();
  gui.popFolder();
}

void drawForeground() {
  gui.pushFolder("foreground");
  int rectCount = gui.sliderInt("rect count", 8, 2, 100);
  float groupWidth = gui.slider("group width", 800, 20, 2400);
  float rectSize = gui.slider("rect size", 50);
  translate(width/2, height/2);
  for (int i = 0; i < rectCount; i++) {
    float x = map(i, 0, rectCount-1, -groupWidth/2, groupWidth/2);
    pushMatrix();
    translate(x, 0);

    // you can always ask for the same control element inside loops
    // like in "rect fill" here, which applies the same color to all rectangles
    fill(gui.colorPicker("rect fill", color(255)).hex);
    rect(0, 0, rectSize, rectSize);

    fill(gui.colorPicker("text fill", color(24)).hex);
    gui.pushFolder("labels");

    // or you can change the path using i
    // to make a new control element (or a whole folder) for each of them
    String label = gui.text("label " + i, String.valueOf(i));

    text(label, -rectSize*0.35, -rectSize*0.5);
    popMatrix();
    gui.popFolder();
  }
  gui.popFolder();
}
