
LazyGui gui;
PGraphics pg;

void settings() {
  size(800, 800, P2D);
}

void setup() {
  gui = new LazyGui(this);
  gui.hide("options");
  pg = createGraphics(width, height, P2D);
}

void draw() {
  pg.beginDraw();
  pg.clear();
  updateGlslFilter();
  pg.endDraw();
  image(pg, 0, 0);
}

void updateGlslFilter() {
  float time = gui.slider("time");
  float timeSpeed = gui.slider("time ++", radians(1));
  gui.sliderSet("time", time + timeSpeed);


  // change something in data/template.glsl
  //  and then save the myShader.glsl file in any text editor
  //  to see it re-compiled and displayed in real-time
  String shaderPath = "template.glsl";
  PShader shader = ShaderReloader.getShader(shaderPath);
  shader.set("time", time);
  ShaderReloader.filter(shaderPath);
}
