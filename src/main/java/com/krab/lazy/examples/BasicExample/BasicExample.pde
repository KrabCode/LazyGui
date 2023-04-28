import com.krab.lazy.*;

LazyGui gui;

void setup() {
  size(800, 800, P2D);
  gui = new LazyGui(this);
}

void draw() {
  background(gui.colorPicker("background").hex);
  int number = gui.sliderInt("pick a number", 7);
  fill(255);
  textSize(64);
  text(number, 400, 500);
}
