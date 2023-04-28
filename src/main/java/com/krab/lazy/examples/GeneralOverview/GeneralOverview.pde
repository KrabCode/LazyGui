import com.krab.lazy.*;

LazyGui gui;

void setup() {
  size(1200, 800, P2D);
  smooth(8);
  gui = new LazyGui(this);
}

void draw() {
  // change the current GUI folder to go into the "scene" folder
  gui.pushFolder("scene");

  drawBackground();
  drawForegroundShape();
  drawForegroundText();

  // go one level up from the current folder
  gui.popFolder();
}

void drawForegroundShape() {
  // go into a new "shape" folder nested inside the current folder
  gui.pushFolder("shape");

  // get various values from the GUI using a unique path and an optional default value parameter
  PVector pos = gui.plotXY("position");
  PVector size = gui.plotXY("size", 250);
  float rotationAngle = gui.slider("rotation");

  // enforce a minimum and maximum value on sliders with the min/max parameters (-10, 10) here
  float rotateDelta = gui.slider("rotation ++", 0.1, -10, 10);

  // change GUI values from code
  gui.sliderSet("rotation", rotationAngle + rotateDelta);
  fill(gui.colorPicker("fill", color(0xFF689FC8)).hex);
  gui.colorPickerHueAdd("fill", radians(gui.slider("fill hue ++", 0.1f)));
  if (gui.button("fill = black")) {
    gui.colorPickerSet("fill", color(0));
  }

  // plug GUI values directly into where they get consumed
  stroke(gui.colorPicker("stroke", 0xFF666666).hex);
  strokeWeight(gui.slider("stroke weight", 10));
  if (gui.toggle("no stroke")) {
    noStroke();
  }

  rectMode(CENTER);
  pushMatrix();
  translate(width/2f, height/2f);
  translate(pos.x, pos.y);
  rotate(radians(rotationAngle));

  // pick one string from an array using gui.radio()
  String selectedShape = gui.radio("shape type", new String[]{"rectangle", "ellipse"});
  boolean shouldDrawEllipse = selectedShape.equals("ellipse");
  if (shouldDrawEllipse) {
    ellipse(0, 0, size.x, size.y);
  } else {
    rect(0, 0, size.x, size.y);
  }
  popMatrix();

  // go up one level back into the "scene" folder
  gui.popFolder();
}

void drawForegroundText() {
  // pushFolder() and popFolder() is not the only way to control folder placement
  // we can use forward slash separators '/' to achieve the same effect
  String labelText = gui.text("text/content", "editable text");
  int textSize = gui.sliderInt("text/size", 64);
  PVector pos = gui.plotXY("text/pos", width*0.1, height*0.9);
  fill(gui.colorPicker("text/fill", color(255)).hex);
  textSize(textSize);
  text(labelText, pos.x, pos.y);
}

void drawBackground() {
  gui.pushFolder("background");
  // the controls are ordered on screen by which gets called first
  // so it can be better to ask for all the values before any if-statement branching
  // because this way you can enforce any given ordering of them in the GUI
  // and avoid control elements appearing suddenly at runtime at unexpected places
  int solidBackgroundColor = gui.colorPicker("solid", color(0xFF252525)).hex;
  PGraphics gradient = gui.gradient("gradient");
  boolean useGradient = gui.toggle("solid\\/gradient"); // here '\\' escapes the '/' path separator
  if (useGradient) {
    image(gradient, 0, 0);
  } else {
    background(solidBackgroundColor);
  }
  gui.popFolder();
}
