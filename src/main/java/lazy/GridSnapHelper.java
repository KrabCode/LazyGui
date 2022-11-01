package lazy;

import processing.core.PGraphics;
import processing.core.PVector;

import static lazy.State.cell;
import static processing.core.PApplet.*;

public class GridSnapHelper {
    public static boolean snapToGridEnabled = true;
    public static boolean showGuideWhenDragging = false;

    static void displayGuide(PGraphics pg, boolean shouldLightUp){
        if(!showGuideWhenDragging || !shouldLightUp){
            return;
        }
        pg.pushStyle();
        pg.strokeWeight(1);
        float w = pg.width;
        float h = pg.height;
        float m = max(w, h);
        for (int x = 0; x < m; x+= cell) {
            pg.stroke(ThemeStore.getColor(ThemeColorType.WINDOW_BORDER));
            pg.line(x, 0, x, h);
            pg.stroke(ThemeStore.getColor(ThemeColorType.NORMAL_BACKGROUND));
            pg.line(x+1, 0, x+1, h);
        }
        for (int y = 0; y < m; y+= cell) {
            pg.stroke(ThemeStore.getColor(ThemeColorType.WINDOW_BORDER));
            pg.line(0, y, w, y);
            pg.stroke(ThemeStore.getColor(ThemeColorType.NORMAL_BACKGROUND));
            pg.line(0, y+1, w, y+1);
        }
        pg.popStyle();
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
