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

    static PVector snapToGrid(float x, float y){
        if(!snapToGridEnabled) {
            return new PVector(x, y);
        }
        int cell = snapGridCellSize;
        int ix = floor(x);
        int iy = floor(y);
        if(ix % cell > cell / 2 ){
            ix += cell;
        }
        if(iy % cell > cell / 2 ){
            iy += cell;
        }
        ix = constrain(ix, 0, State.app.width);
        iy = constrain(iy, 0, State.app.width);
        while(ix % cell != 0){
            ix -= 1;
        }
        while(iy % cell != 0){
            iy -= 1;
        }
        return new PVector(ix, iy);
    }
}
