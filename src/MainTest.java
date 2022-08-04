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
        drawBg();
        drawBrush();
        drawBox();
        pg.endDraw();
        clear();
        image(pg, 0, 0);
        gui.draw();

        /*
        if(gui.toggle("record")){
            saveFrame("rec/" + i++ + ".jpg");
        }
        */
    }

    private void drawBg() {
        pg.noStroke();
        pg.fill(gui.colorPicker("bg").hex);
        pg.rect(0,0,width,height);
    }

    private void drawBrush() {
        pg.fill(gui.colorPicker("brush/color").hex);
        float brushWeight = gui.slider("brush/weight", 5);
        if(gui.mousePressedOutsideGui()){
            float dist = dist(pmouseX, pmouseY, mouseX, mouseY);
            for (int i = 0; i <= dist; i++) {
                float iNorm = norm(i, 0, dist);
                pg.ellipse(lerp(mouseX, pmouseX, iNorm), lerp(mouseY, pmouseY, iNorm), brushWeight, brushWeight);
            }

        }
    }

    private void drawBox() {
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

    }
}
