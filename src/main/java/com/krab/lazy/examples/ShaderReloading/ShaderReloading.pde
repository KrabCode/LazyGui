import com.krab.lazy.*;

LazyGui gui;

void setup() {
  size(800, 800, P2D);
  gui = new LazyGui(this, new LazyGuiSettings().setStartGuiHidden(true));
}

void draw() {
  String shaderPath = "template.glsl";
  PShader shader = ShaderReloader.getShader(shaderPath);
  shader.set("time", (float) 0.001 * millis());
  ShaderReloader.filter(shaderPath);
}