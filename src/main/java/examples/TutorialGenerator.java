package examples;

import lazy.LazyGui;
import processing.core.PApplet;

@SuppressWarnings("IntegerDivisionInFloatingPointContext")
public class TutorialGenerator extends PApplet {
    LazyGui gui;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000,1000,P2D);
        smooth(16);
    }

    public void setup() {
        gui = new LazyGui(this);
    }

    public void draw() {
        background(gui.colorPicker("background").hex);
        float x = gui.slider("my circle/x");
        fill(gui.colorPicker("circle/fill").hex);
        stroke(gui.colorPicker("circle/stroke").hex);
        strokeWeight(gui.slider("circle/weight", 3));
        ellipse(x, height/2, 50, 50);

    }

    public void keyPressed() {
        if(key == 's'){
            image(gui.getGuiCanvas(), 0, 0);
//            save("C:\\Projects\\LazyGui\\readme_assets\\slider_2.png");
        }
    }
}
