import com.krab.lazy.*;

LazyGui gui;

void setup() {
  size(1200, 800, P2D);
  smooth(8);
  rectMode(CENTER);
  textAlign(CENTER, CENTER);
  textSize(32);
  gui = new LazyGui(this);
}

void draw() {
  background(gui.colorPicker("background", color(0xFF252525)).hex);
  drawForeground();
}

void drawForeground() {
  gui.pushFolder("foreground");
  int rectCount = gui.sliderInt("rect count", 6, 2, 12);
  translate(width/2, height/2);
  for (int i = 0; i < rectCount; i++) {
    pushMatrix();
    gui.pushFolder("global settings");

    // you can ask for the same control element inside loops
    // like in "rect fill" here, which applies the same color to all rectangles
    fill(gui.colorPicker("rect fill", color(0xFFB2B0B0)).hex);
    float groupWidth = gui.slider("total width", 900, 20, 2400);
    float rectSize = gui.slider("rect size", 100);
    float x = map(i, 0, rectCount-1, -groupWidth/2, groupWidth/2);
    translate(x, 0);
    rect(0, 0, rectSize, rectSize);
    PVector globalTextPos = gui.plotXY("global offset");
    translate(globalTextPos.x, globalTextPos.y);
    gui.popFolder();

    // or you can put 'i' inside the path to make a new folder for each of them
    // with new folders growing automatically as you increase the i limit
    gui.pushFolder("#" + i);
    float rotation = gui.slider("rotation");
    rotate(rotation);
    PVector localTextPos = gui.plotXY("local offset");

    // when a toggle starts with 'active' it also lights up the folder icon
    if(gui.toggle("active shake")){
      localTextPos.add(PVector.random2D().mult(3));
    }
    translate(localTextPos.x, localTextPos.y);
    int defaultLabel = floor(pow(2, i+1));

    // when the text path starts with 'label' this also sets a display name for the folder
    String label = gui.text("label text", "" + defaultLabel);
    fill(gui.colorPicker("text color", color(24)).hex);
    text(label, 0, 0);

    popMatrix();
    gui.popFolder();
  }
  gui.popFolder();
}
