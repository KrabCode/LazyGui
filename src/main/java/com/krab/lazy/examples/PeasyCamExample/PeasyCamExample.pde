import com.krab.lazy.*;
import peasy.PeasyCam;

// adapted from the HelloPeasy example to show how it can work with LazyGui
// requires the PeasyCam library, tested on PeasyCam version 302

PeasyCam cam;
LazyGui gui;

public void settings() {
  size(800, 600, P3D);
}

public void setup() {
  cam = new PeasyCam(this, 400);
  gui = new LazyGui(this);
}

public void draw() {
  rotateX(-.5f);
  rotateY(-.5f);
  lights();
  scale(10);
  strokeWeight(gui.slider("stroke weight", 0.1f));
  background(gui.colorPicker("background", color(0)).hex);

  fill(gui.colorPicker("big box", color(255,0,0)).hex);
  box(30);
  pushMatrix();
  translate(0, 0, 20);

  fill(gui.colorPicker("small box", color(0,0,255)).hex);
  box(5);
  popMatrix();


  cam.beginHUD(); // makes the gui visual exempt from the PeasyCam controlled 3D scene
  gui.draw(); // displays the gui manually here - rather than automatically when draw() ends
  cam.endHUD();

  // PeasyCam ignores mouse input while the mouse interacts with the GUI
  cam.setMouseControlled(gui.isMouseOutsideGui());
}