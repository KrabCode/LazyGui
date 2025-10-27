import com.krab.lazy.*;

LazyGui gui;
PGraphics pgInvert;
PGraphics pgHue;
PImage img;

String SHADER_HUE_SHIFT_PATH = "hueShift.glsl";
PShader shaderHueShift;

void setup() {
  size(800, 800, P2D);
  gui = new LazyGui(this);
  pgInvert = createGraphics(400, 400, P2D);
  pgHue = createGraphics(400, 400, P2D);
  img = loadImage("picsum_dog.jpg");
  shaderHueShift = loadShader(SHADER_HUE_SHIFT_PATH);
}

public void draw() {
    background(gui.colorPicker("background").hex);

    gui.image("source image", img);

    pgInvert.beginDraw();
    pgInvert.image(img, 0, 0);
    pgInvert.filter(INVERT);
    pgInvert.endDraw();
    gui.image("inverted", pgInvert);

    pgHue.beginDraw();
    pgHue.image(img, 0, 0);
    shaderHueShift.set("hueShiftAmount", frameCount * 0.01f);
    pgHue.filter(shaderHueShift);
    pgHue.endDraw();
    gui.image("hue shifted", pgHue);

    imageMode(CENTER);
    image(pgHue, 400, 400);
}