package lazy.utils;

import lazy.PickerColor;
import lazy.windows.Window;
import lazy.windows.WindowManager;
import lazy.stores.LayoutStore;
import lazy.stores.NormColorStore;
import lazy.stores.ShaderStore;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.opengl.PShader;

import java.util.List;

import static lazy.stores.GlobalReferences.gui;
import static lazy.stores.LayoutStore.cell;
import static processing.core.PApplet.*;

public class SnapToGrid {
    public static boolean snapToGridEnabled = false;
    static final List<String> availableVisibilityModes = new ArrayListBuilder<String>().add("always", "on drag", "never").build();
    private static PShader pointShader;
    private static final String pointShaderPath = "gridPointFrag.glsl";
    private static final int VISIBILITY_ALWAYS = 0;
    private static final int VISIBILITY_ON_DRAG = 1;
    private static final int VISIBILITY_NEVER = 2;
    private static final int defaultVisibilityModeIndex = VISIBILITY_ON_DRAG;
    private static int selectedVisibilityModeIndex = defaultVisibilityModeIndex;
    private static float dragAlpha = 0;
    private static final float dragAlphaDelta = 0.05f;
    private static PickerColor pointGridColor = null;
    private static float pointWeight = 4;
    private static float sdfCropDistance = 0.25f;

    public static void displayGuideAndApplyFilter(PGraphics pg, Window draggedWindow){
        if(pointShader == null){
            pointShader = ShaderStore.getShader(pointShaderPath);
        }
        if(selectedVisibilityModeIndex == VISIBILITY_ON_DRAG){
            updateAlpha(draggedWindow);
        }
        if(selectedVisibilityModeIndex == VISIBILITY_NEVER){
            return;
        }
        pointShader.set("alpha", selectedVisibilityModeIndex == VISIBILITY_ALWAYS ? pointGridColor.alpha : dragAlpha);
        pointShader.set("sdfCropEnabled", selectedVisibilityModeIndex == VISIBILITY_ON_DRAG);
        pointShader.set("sdfCropDistance", sdfCropDistance);
        if(draggedWindow != null){
            pointShader.set("window", draggedWindow.posX, draggedWindow.posY, draggedWindow.windowSizeX, draggedWindow.windowSizeY);
        }
        pg.shader(pointShader);

        pg.pushStyle();
        pg.strokeWeight(pointWeight);
        float w = pg.width;
        float h = pg.height;
        int step = floor(cell);
        pg.beginShape(POINTS);
        pg.strokeCap(ROUND);
        int pointColor = pointGridColor != null ? pointGridColor.hex : NormColorStore.color(1);
        pg.stroke(pointColor);
        for (int x = 0; x <= w; x+= step) {
            for (int y = 0; y <= h; y+= step) {
                pg.vertex(x+0.5f,y+0.5f);
            }
        }
        pg.endShape();
        pg.popStyle();


        pg.resetShader();
    }

    private static void updateAlpha(Window draggedWindow) {
        float dragAlphaMax = pointGridColor.alpha;
        dragAlphaMax = constrain(dragAlphaMax, 0, 1);
        if(draggedWindow != null){
            dragAlpha = lerp(dragAlpha, dragAlphaMax, dragAlphaDelta);
        }else{
            dragAlpha = lerp(dragAlpha, 0, dragAlphaDelta);
        }
        dragAlpha = constrain(dragAlpha, 0, dragAlphaMax);
    }

    public static PVector trySnapToGrid(float inputX, float inputY){
        if(!snapToGridEnabled) {
            return new PVector(inputX, inputY);
        }
        float negativeModuloBuffer = cell * 60;
        inputX += negativeModuloBuffer;
        inputY += negativeModuloBuffer;
        int x = floor(inputX);
        int y = floor(inputY);
        if(x % cell > cell / 2 ){
            x += cell;
        }
        if(y % cell > cell / 2 ){
            y += cell;
        }
        while(x % cell != 0){
            x -= 1;
        }
        while(y % cell != 0){
            y -= 1;
        }
        return new PVector(x-negativeModuloBuffer, y-negativeModuloBuffer);
    }

    public static List<String> getOptions() {
        return availableVisibilityModes;
    }

    public static void setSelectedVisibilityMode(String mode) {
        if(!availableVisibilityModes.contains(mode)){
            return;
        }
        selectedVisibilityModeIndex = availableVisibilityModes.indexOf(mode);
    }

    public static String getDefaultVisibilityMode() {
        return getOptions().get(defaultVisibilityModeIndex);
    }

    public static void update() {
        gui.pushFolder("grid");
        boolean previousSnapToGridEnabled = snapToGridEnabled;
        snapToGridEnabled = gui.toggle("snap to grid", true);

        if(hasCellSizeJustChanged() || hasJustBeenEnabled(previousSnapToGridEnabled, snapToGridEnabled)){
            WindowManager.snapAllStaticWindowsToGrid();
        }
        setSelectedVisibilityMode(gui.radio("show grid", getOptions(), getDefaultVisibilityMode()));
        pointGridColor = gui.colorPicker("point color", NormColorStore.color(0.5f, 1));
        pointWeight = gui.slider("point weight", pointWeight);
        sdfCropDistance = gui.slider("point range", sdfCropDistance);
        gui.popFolder();
    }

    static float cellSizeLastFrame = -1;

    private static boolean hasCellSizeJustChanged() {
        boolean result = cellSizeLastFrame != LayoutStore.cell;
        cellSizeLastFrame = LayoutStore.cell;
        return result;
    }

    private static boolean hasJustBeenEnabled(boolean previousState, boolean currentState) {
        return !previousState && currentState;
    }
}
