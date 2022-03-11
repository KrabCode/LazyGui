import processing.core.PApplet;
import processing.core.PGraphics;
import toolbox.Gui;
import toolbox.ShaderReloader;

public class MainTest extends PApplet {
    Gui gui;
    PGraphics pg;

    public static void main(String[] args) {
        PApplet.main(java.lang.invoke.MethodHandles.lookup().lookupClass());
    }

    public void settings() {
        size(1000, 1000, P2D);
//        fullScreen(P2D);
    }

    public void setup() {
        gui = new Gui(this);
        pg = createGraphics(width, height, P2D);
    }

    @SuppressWarnings("DuplicatedCode")
    public void draw() {
        pg.beginDraw();
        pg.noStroke();
        pg.image(gui.gradient("bg"), 0, 0);
        pg.image(gui.imagePicker("image", "C:\\img\\doggo.jpg"), 0, 0);
        String shaderPath = "C:\\Users\\Krab\\Documents\\GitHub\\Toolbox\\src\\test.glsl";
        ShaderReloader.getShader(shaderPath).set("time", radians(frameCount));
        ShaderReloader.filter(shaderPath, pg);
        pg.endDraw();
        clear();
        image(pg, 0, 0);
        gui.draw();
        gui.record(pg);
    }
}
