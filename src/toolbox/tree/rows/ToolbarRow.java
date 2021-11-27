package toolbox.tree.rows;

import processing.core.PGraphics;
import toolbox.global.GuiPaletteStore;
import toolbox.global.State;
import toolbox.global.Utils;
import java.util.ArrayList;

import static processing.core.PApplet.*;
import static processing.core.PConstants.TAU;
import static toolbox.global.palettes.GuiPaletteColorType.*;

public class ToolbarRow extends Row {
    int buttonCount = 8;
    ArrayList<Float> buttonRotations = new ArrayList<>();


    public ToolbarRow(String path, FolderRow parentFolder) {
        super(RowType.DISPLAY, path, parentFolder);
        this.name = "";
        for (int i = 0; i < buttonCount; i++) {
            buttonRotations.add(0f);
        }
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        for (int i = 0; i < buttonCount; i++) {
            pg.pushMatrix();
            pg.pushStyle();
            float x =  i * cell;
            pg.translate(x, 0);
            pg.pushMatrix();
            updateDrawButton(pg, i);
            pg.popMatrix();
            if(i > 0){
                pg.stroke(GuiPaletteStore.get(WINDOW_BORDER));
                pg.strokeWeight(1);
                pg.line(0,0,0,cell-1);
            }
            pg.popStyle();
            pg.popMatrix();
        }
        pg.stroke(GuiPaletteStore.get(WINDOW_BORDER));
        pg.line(0,cell-1,size.x, cell-1);
    }



    private void updateDrawButton(PGraphics pg, int buttonIndex) {
        float n = cell * 0.28f;
        float rotation = buttonRotations.get(buttonIndex);
        pg.strokeWeight(1.2f);
        pg.noFill();
        pg.translate(cell * 0.5f, cell * 0.5f);
        pg.rotate(rotation);
        if (isMouseOverButton(buttonIndex)) {
            pg.stroke(GuiPaletteStore.get(FOCUS_FOREGROUND));
            pg.fill(GuiPaletteStore.get(FOCUS_BACKGROUND));
            buttonRotations.set(buttonIndex, rotation + radians(2));
        } else {
            pg.stroke(GuiPaletteStore.get(NORMAL_FOREGROUND));
        }
        pg.beginShape();
        if(buttonIndex == 0){
            pg.circle(0,0,n);
            return;
        }
        buttonIndex += 2;
        for (int i = 0; i < buttonIndex; i++) {
            float theta = map(i, 0, buttonIndex-1, 0, TAU);
            pg.vertex(n*cos(theta), n*sin(theta));
        }
        pg.endShape();
    }

    @Override
    protected void highlightNodeRowOnMouseOver(PGraphics pg) {
        // skip the full row highlight and instead highlight each button separately
    }

    @Override
    public void mouseReleasedAnywhere(float x, float y) {
        if(isParentWindowHidden()){
            return;
        }
        super.mouseReleasedAnywhere(x, y);
        for(int i = 0; i < buttonCount; i++){
            if(isMouseOverButton(i)){
                // TODO
                return;
            }
        }
    }

    private boolean isMouseOverButton(int buttonIndex){
        return Utils.isPointInRect(State.app.mouseX, State.app.mouseY,
                pos.x + buttonIndex * cell, pos.y,
                cell, cell);
    }
}
