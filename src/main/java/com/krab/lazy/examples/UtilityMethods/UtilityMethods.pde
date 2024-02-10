import com.krab.lazy.*;

LazyGui gui;

void setup() {
  size(800, 800, P2D);
  gui = new LazyGui(this);
}

void draw() {
  background(gui.colorPicker("background", color(36)).hex);
  translate(width/2, height/2);

  gui.pushFolder("rect");
  pushMatrix();
  transform();
  PVector size = gui.plotXY("size", 300, 100);
  style();
  rect(0, 0, size.x, size.y);
  popMatrix();
  gui.popFolder();

  gui.pushFolder("text");
  pushMatrix();
  transform();
  font();
  text(gui.text("text", "hello"), 0, 0);
  popMatrix();
  gui.popFolder();
}

// Change position and rotation without creating a new gui folder.
void transform() {
  gui.pushFolder("transform");
  PVector pos = gui.plotXY("pos");
  translate(pos.x, pos.y);
  rotate(gui.slider("rotate"));
  PVector scale = gui.plotXY("scale", 1.00);
  scale(scale.x, scale.y);
  gui.popFolder();
}

// Change drawing style
void style() {
  gui.pushFolder("style");
  strokeWeight(gui.slider("weight", 4));
  stroke(gui.colorPicker("stroke", color(0)).hex);
  fill(gui.colorPicker("fill", color(200)).hex);
  String rectMode = gui.radio("rect mode", new String[]{"center", "corner"});
  if("center".equals(rectMode)){
    rectMode(CENTER);
  }else{
    rectMode(CORNER);
  }
  gui.popFolder();
}

// font() related fields
HashMap<String, PFont> fontCache = new HashMap<String, PFont>();
HashMap<String, Integer> xAligns;
HashMap<String, Integer> yAligns;

// Select from lazily created, cached fonts.
void font() {
  gui.pushFolder("font");
  fill(gui.colorPicker("fill", color(0)).hex);
  int size = gui.sliderInt("size", 64, 1, 256);
  if (xAligns == null || yAligns == null) {
    xAligns = new HashMap<String, Integer>();
    xAligns.put("left", LEFT);
    xAligns.put("center", CENTER);
    xAligns.put("right", RIGHT);
    yAligns = new HashMap<String, Integer>();
    yAligns.put("top", TOP);
    yAligns.put("center", CENTER);
    yAligns.put("bottom", BOTTOM);
  }
  String xAlignSelection = gui.radio("align x", xAligns.keySet().toArray(new String[0]), "center");
  String yAlignSelection = gui.radio("align y", yAligns.keySet().toArray(new String[0]), "center");
  textAlign(xAligns.get(xAlignSelection), yAligns.get(yAlignSelection));
  String fontName = gui.text("font name", "Arial");
  if (gui.button("list fonts")) {
    String[] fonts = PFont.list();
    for (String font : fonts) {
      println(font + "                 "); // some spaces to avoid copying newlines from the console
    }
  }
  String fontKey = fontName + " | size: " + size;
  if (!fontCache.containsKey(fontKey)) {
    PFont loadedFont = createFont(fontName, size);
    fontCache.put(fontKey, loadedFont);
    println("Loaded font: " + fontKey);
  }
  PFont cachedFont = fontCache.get(fontKey);
  textFont(cachedFont);
  gui.popFolder();
}
