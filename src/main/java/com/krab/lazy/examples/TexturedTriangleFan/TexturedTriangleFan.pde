import com.krab.lazy.*;

LazyGui gui;

void setup() {
  size(800, 800, P2D);
  gui = new LazyGui(this);
}

void draw() {
  background(gui.colorPicker("background").hex);
  gui.pushFolder("shape");
  translate(width/2, height/2);
  rotate(gui.slider("rotation"));
  stroke(gui.colorPicker("stroke").hex);
  strokeWeight(gui.slider("weight", 1.5));
  beginShape(TRIANGLE_FAN);
  PGraphics fillTexture = gui.gradient("texture");
  textureMode(NORMAL);
  texture(fillTexture);
  vertex(0, 0, 0.5, 0);
  int vertexCount = gui.sliderInt("vertices", 6, 3, 10000);
  float radius = gui.slider("radius", 250);
  for (int i = 0; i <= vertexCount; i++) {
    float theta = map(i, 0, vertexCount, 0, TAU);
    float x = radius * cos(theta);
    float y = radius * sin(theta);
    vertex(x, y, 0.5, 1);
  }
  endShape();
  gui.popFolder();
}
