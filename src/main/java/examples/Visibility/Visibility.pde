
LazyGui gui;

public void setup() {
  size(1200, 800, P2D);
  smooth(8);
  gui = new LazyGui(this);

  // hide the gui options if you don't need to see them
  gui.hide("options");

  // change the gui options by setting their values from code
  gui.toggleSet("options/saves/autosave on exit", false);

  textSize(64);
}

public void draw() {
  background(gui.colorPicker("background", color(50)).hex);
  drawRects();
  drawText();
}

void drawRects() {
  gui.pushFolder("rects");
  int maxRectCount = 20;
  int rectCount = gui.sliderInt("count", 10, 0, maxRectCount);
  String dynamicFolderPrefix = "#";

  for (int i = 0; i < rectCount; i++) {
    // make a dynamic list of rects each with its own folder
    gui.pushFolder(dynamicFolderPrefix + i);
    PVector pos = gui.plotXY("pos", 600, 80 + i * 22);
    PVector size = gui.plotXY("size", 5);
    fill(gui.colorPicker("fill", color(200)).hex);
    noStroke();
    rect(pos.x, pos.y, size.x, size.y);
    // show the current folder in case it was hidden by lower rectCount but then the count went back up again
    gui.showCurrentFolder();
    gui.popFolder();
  }

  for (int i = 0; i < rectCount; i++) {
    // alternative way to show the folders without going into them with pushFolder()
    gui.show(dynamicFolderPrefix + i);
  }

  // hide any unused folders when the count gets lowered
  for (int i = rectCount; i < maxRectCount; i++) {
    gui.pushFolder(dynamicFolderPrefix + i);
    gui.hideCurrentFolder();
    gui.popFolder();
  }

  // OR hide any unused folders without going into them with pushFolder()
  for (int i = rectCount; i < maxRectCount; i++) {
    gui.hide(dynamicFolderPrefix + i);
  }

  gui.popFolder();
}

private void drawText() {
  gui.pushFolder("text");
  boolean show = gui.toggle("editable?");
  String content = gui.text("content", "hello");
  text(content, 800, 600);

  // You can also hide any single control element by path
  if (show) {
    gui.show("content");
  } else {
    gui.hide("content");
  }

  gui.popFolder();
}
