
LazyGui gui;

void setup() {
  size(600, 600, P2D);
  gui = new LazyGui(this);
  textSize(64);
}

void draw() {
  background(gui.colorPicker("background").hex);
  int number = gui.sliderInt("pick a number");
  fill(255);
  text(number, 400, 500);
}
