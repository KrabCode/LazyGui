package toolbox.tree.rows;

import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import toolbox.global.GuiPaletteStore;

import static processing.core.PApplet.lerp;
import static processing.core.PApplet.map;
import static processing.core.PConstants.CORNER;
import static toolbox.global.palettes.GuiPaletteColorType.*;

public class ToggleRow extends Row {

    public boolean valueBooleanDefault;
    public boolean valueBoolean;
    boolean armed = false;
    public float handlePosNorm;

    public ToggleRow(String path, FolderRow folder, boolean defaultValue) {
        super(RowType.CONTROL, path, folder);
        valueBooleanDefault = defaultValue;
        valueBoolean = defaultValue;
        handlePosNorm = valueBoolean ? 1 : 0;
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        drawToggleHandle(pg);
    }

    private void drawToggleHandle(PGraphics pg) {
        float handleWidth = cell * 0.75f;
        float handleXCenter = size.x - cell * 0.8f;
        float handleXLeft = handleXCenter - handleWidth * 0.5f;
        float handleXRight = handleXCenter + handleWidth * 0.5f;
        float lerpAmt = 0.3f;
        if(valueBoolean){
            handlePosNorm = lerp(handlePosNorm,1, lerpAmt);
        }else{
            handlePosNorm = lerp(handlePosNorm,0, lerpAmt);
        }
        float handleX = map(handlePosNorm, 0, 1, handleXLeft, handleXRight);
        float handleY = size.y * 0.5f;
        float handleDiameter = cell * 0.4f;
        float handleHeight = 5;
        pg.fill(valueBoolean ? GuiPaletteStore.get(WINDOW_BORDER) : GuiPaletteStore.get(FOCUS_BACKGROUND));
        if(mouseOver){
            pg.fill(GuiPaletteStore.get(NORMAL_FOREGROUND));
        }
        pg.rectMode(CORNER);
        pg.rect(handleXLeft, handleY-handleHeight / 2f, handleWidth, handleHeight, 8);
        pg.noStroke();
        pg.fill(valueBoolean ? GuiPaletteStore.get(NORMAL_FOREGROUND) : GuiPaletteStore.get(WINDOW_BORDER));
        if(mouseOver && valueBoolean){
            pg.fill(GuiPaletteStore.get(FOCUS_FOREGROUND));
        }
        pg.ellipse(handleX, handleY, handleDiameter, handleDiameter);
    }


    @Override
    public void rowPressed(float x, float y) {
        super.rowPressed(x, y);
        armed = true;
    }

    public void mouseReleasedOverRow(float x, float y){
        super.mouseReleasedOverRow(x,y);
        if(armed){
            valueBoolean = !valueBoolean;
        }
        armed = false;
    }

    @Override
    public void mouseDragRowContinue(MouseEvent e, float x, float y, float px, float py) {

    }
}
