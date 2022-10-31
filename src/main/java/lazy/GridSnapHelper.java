package lazy;

import processing.core.PGraphics;
import processing.core.PVector;

import static processing.core.PApplet.*;

public class GridSnapHelper {
    public static int snapGridCellSize = 20;
    public static boolean snapToGridEnabled;
    public static boolean showGuideWhenDragging = true;
    private static final float alphaChangePerFrame = 0.025f;
    static float maxAlpha = 0.5f;
    private static float alpha = 0;

    static void displayGuide(PGraphics pg, boolean shouldLightUp){
        if(!showGuideWhenDragging){
            return;
        }
        updateAlpha(shouldLightUp);
        pg.strokeWeight(1.99f);
        pg.stroke(ThemeStore.getColor(ThemeColorType.WINDOW_BORDER), alpha);
        float w = pg.width;
        float h = pg.height;
        float m = max(w, h);
        for (int x = 0; x < m; x+= snapGridCellSize) {
            pg.line(x, 0, x, h);
        }
        for (int y = 0; y < m; y+= snapGridCellSize) {
            pg.line(0, y, w, y);
        }
    }

    private static void updateAlpha(boolean shouldLightUp) {
        if(shouldLightUp){
            alpha += alphaChangePerFrame;
        }else{
            alpha -= alphaChangePerFrame;
        }
        alpha = constrain(alpha, 0, maxAlpha);
    }

    static PVector snapToGrid(float inputX, float inputY){
        if(!snapToGridEnabled) {
            return new PVector(inputX, inputY);
        }
        int cell = snapGridCellSize;
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
