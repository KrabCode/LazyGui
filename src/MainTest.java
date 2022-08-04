import processing.core.PApplet;
import processing.core.PGraphics;
import toolbox.Gui;

public class MainTest extends PApplet {
    Gui gui;
    PGraphics pg;
    float shaderTime;
    float rotationTime;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P3D);
//        fullScreen(P2D);
    }

    public void setup() {
        gui = new Gui(this);
        pg = createGraphics(width, height, P3D);
    }

    @SuppressWarnings("DuplicatedCode")
    public void draw() {
        pg.beginDraw();
        pg.noStroke();
        pg.image(gui.gradient("bg"), 0, 0);
        pg.stroke(gui.colorPicker("stroke").hex);
        pg.strokeWeight(gui.slider("weight", 5));
        if(gui.mousePressedOutsideGui()){
            pg.line(pmouseX, pmouseY, mouseX, mouseY);
        }
        float boxSize = gui.slider("box/size", 120);
        pg.stroke(gui.colorPicker("box/stroke").hex);
        pg.fill(gui.colorPicker("box/fill").hex);
        pg.strokeWeight(gui.slider("box/stroke weight", 2));
        pg.translate(width/2f, height/2f);
        pg.translate(gui.slider("box/pos x"),
                gui.slider("box/pos y"),
                gui.slider("box/pos z"));
        float rotationTimeDelta = gui.slider("box/rot speed");
        rotationTime += rotationTimeDelta;
        pg.rotateX(gui.slider("box/rot x") * rotationTime);
        pg.rotateY(gui.slider("box/rot y")* rotationTime);
        pg.rotateZ(gui.slider("box/rot z")* rotationTime);
        pg.box(boxSize);

        pg.endDraw();
        clear();
        image(pg, 0, 0);
        gui.draw();

        /*
        if(gui.toggle("record")){
            saveFrame("rec5/" + i++ + ".jpg");
        }
        */
    }
}
