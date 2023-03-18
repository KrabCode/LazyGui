LazyGui gui;

public void setup() {
  size(1144, 880, P2D);
  smooth(8);
  gui = new LazyGui(this);
  frameRate(60);
}

public void draw() {
  gui.pushFolder("gradient");

  PGraphics gradient = gui.gradient("gradient", new int[]{
    //specify default colors
    unhex("FFF6E1C3"),
    unhex("FFE9A178"),
    unhex("FFA84448"),
    unhex("FF7A3E65")
  });
  // draw the background using the returned gradient texture
  image(gradient, 0, 0);

  // move the color position while keeping it between 0 and 1
  float colorPos = gui.slider("fill pos", 0, 0, 1);
  float colorPosNext = (colorPos + gui.slider("pos delta", 0.0006)) % 1;
  gui.sliderSet("fill pos", colorPosNext);

  // get the color at this position between 0 and 1
  PickerColor gradientColor = gui.gradientColorAt("gradient", colorPosNext);
  fill(gradientColor.hex);

  gui.popFolder();
  drawForegroundShape();
}

private void drawForegroundShape() {
  gui.pushFolder("shape");
  PVector pos = gui.plotXY("position", width / 2f, height / 2f);
  PVector size = gui.plotXY("size", 250, 50);
  float rotationAngle = gui.slider("rotation");
  float rotationAngleDelta = gui.slider("rotation delta", 1);
  gui.sliderSet("rotation", rotationAngle + rotationAngleDelta);
  if (gui.toggle("no stroke", true)) {
    noStroke();
  } else {
    stroke(gui.colorPicker("stroke").hex);
    strokeWeight(gui.slider("stroke weight", 10));
  }
  gui.popFolder();
  rectMode(CENTER);
  translate(pos.x, pos.y);
  rotate(radians(rotationAngle));
  rect(0, 0, size.x, size.y);
  gui.popFolder();
}
