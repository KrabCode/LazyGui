package lazy;

import processing.core.PGraphics;
import processing.core.PVector;
import processing.opengl.PShader;

import static lazy.State.cell;
import static processing.core.PApplet.*;

public class GridSnapHelper {
    public static boolean snapToGridEnabled = true;
    public static boolean showGuideWhenDragging = false;
    private static PShader lineShader;
    private static final String lineShaderPathFrag = "shaders/gridLineFrag.glsl";
    private static final String lineShaderPathVert = "shaders/gridLineVert.glsl";

    private static float alpha = 0;
    private static final float alphaDelta = 0.1f;
    private static float alphaMax = 0.5f;
    private static int pointGridColor = -1;

    static void displayGuideAndApplyFilter(PGraphics pg, Window draggedWindow){
        if(!showGuideWhenDragging){
            return;
        }
        updateAlpha(draggedWindow);

        lineShader = ShaderReloader.getShader(lineShaderPathFrag);
        lineShader.set("alpha", alpha);
        if(draggedWindow != null){
            lineShader.set("window", draggedWindow.posX, draggedWindow.posY, draggedWindow.windowSizeX, draggedWindow.windowSizeY);
        }

        ShaderReloader.shader(lineShaderPathFrag, pg);


        pg.pushStyle();
        pg.strokeWeight(5);
        float w = pg.width;
        float h = pg.height;
        int step = floor(cell);
        pg.beginShape(POINTS);
        for (int x = 0; x < w; x+= step) {
            for (int y = 0; y < h; y+= step) {
                pg.stroke(State.normalizedColorProvider.color(1));
                pg.vertex(x+0.5f,y+0.5f);
            }
        }
        pg.endShape();
        pg.popStyle();


        pg.resetShader();
    }

    public static void setMaxAlpha(float value) {
        alphaMax = value;
        alphaMax = constrain(alphaMax, 0, 1);
    }

    private static void updateAlpha(Window draggedWindow) {
        if(draggedWindow != null){
            alpha = lerp(alpha, alphaMax, alphaDelta);
        }else{
            alpha = lerp(alpha, 0, alphaDelta);
        }
        alpha = constrain(alpha, 0, alphaMax);
    }

    static PVector snapToGrid(float inputX, float inputY){
        if(!snapToGridEnabled) {
            return new PVector(inputX, inputY);
        }
        int x = floor(inputX);
        int y = floor(inputY);
        if(x % cell > cell / 2 ){
            x += cell;
        }
        if(y % cell > cell / 2 ){
            y += cell;
        }
        x = constrain(x, 0, State.app.width);
        y = constrain(y, 0, State.app.width);
        while(x % cell != 0){
            x -= 1;
        }
        while(y % cell != 0){
            y -= 1;
        }
        return new PVector(x, y);
    }
}
