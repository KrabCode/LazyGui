
LazyGui gui;

void setup() {
  size(600, 600, P2D);
  gui = new LazyGui(this);
  textSize(64);
}

void draw() {
  int value = gui.sliderInt("pick a number");
  background(50);
  fill(255);
  text(value, 400, 500);
}
