import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.Gui;

public class SketchExample extends PApplet {

    // TODO publish an exe with jpackage that installs and runs SketchExample

    Gui gui;
    PGraphics pg;
    PVector rotationTime;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
//        size(1000, 1000, P3D);
        fullScreen(P3D);
    }

    public void setup() {
        gui = new Gui(this);
        pg = createGraphics(width, height, P3D);
        colorMode(HSB,1,1,1,1);
        rotationTime = new PVector();
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
    }

    private void drawBg() {
        boolean disableDepthTest = gui.toggle("background/disable depth test", true);
        if(disableDepthTest){
            pg.hint(PConstants.DISABLE_DEPTH_TEST);
        }
        pg.noStroke();
        pg.blendMode(getBlendMode());
        if(gui.toggle("background/use gradient")){
            pg.image(gui.gradient("background/gradient colors"), 0, 0);
        }else{
            pg.fill(gui.colorPicker("background/solid color", color(0xFF081014)).hex);
            pg.rect(0,0,width,height);
        }
        if(disableDepthTest){
            pg.hint(PConstants.ENABLE_DEPTH_TEST);
        }
        pg.blendMode(BLEND);
    }

    private int getBlendMode() {
        String selectedMode = gui.stringPicker("background/blend mode",
                new String[]{"blend", "add", "subtract"}, "subtract");
        switch (selectedMode) {
            case "add": return ADD;
            case "subtract": return SUBTRACT;
            default: return BLEND;
        }
    }

    private void drawBrush() {
        pg.fill(gui.colorPicker("mouse brush/color", color(1)).hex);
        float brushWeight = gui.slider("mouse brush/weight", 5);
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
        pg.strokeWeight(gui.slider("box/stroke weight", 2));
        pg.stroke(gui.colorPicker("box/stroke color", color(1)).hex);
        int fillColor = gui.colorPicker("box/fill color", color(0.05f)).hex;
        if(gui.toggle("box/no fill", true)) {
            pg.noFill();
        }else{
            pg.fill(fillColor);
        }
        pg.translate(width/2f, height/2f);
        pg.translate(gui.slider("box/pos x"),
                gui.slider("box/pos y"),
                gui.slider("box/pos z", 500));
        float rotationTimeDelta = gui.slider("box/rotate multiplier", 0.5f);
        rotationTime.x += radians(gui.slider("box/rotate x", 0.25f) * rotationTimeDelta);
        rotationTime.y += radians(gui.slider("box/rotate y", 0.25f) * rotationTimeDelta);
        rotationTime.z += radians(gui.slider("box/rotate z", 0.25f) * rotationTimeDelta);
        pg.rotateX(rotationTime.x);
        pg.rotateY(rotationTime.y);
        pg.rotateZ(rotationTime.z);
        pg.box(boxSize);

    }
}
