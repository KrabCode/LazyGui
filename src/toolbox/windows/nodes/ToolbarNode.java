package toolbox.windows.nodes;

import com.jogamp.newt.event.KeyEvent;
import processing.core.PGraphics;
import toolbox.global.PaletteStore;
import toolbox.global.State;
import toolbox.global.Utils;
import java.util.ArrayList;

import static processing.core.PApplet.*;
import static processing.core.PConstants.TAU;
import static toolbox.global.palettes.PaletteColorType.*;

public class ToolbarNode extends AbstractNode {
    int buttonCount = 8;
    ArrayList<Float> buttonRotations = new ArrayList<>();


    public ToolbarNode(String path, FolderNode parentFolder) {
        super(NodeType.VALUE_ROW, path, parentFolder);
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
            float x = i * cell;
            pg.translate(x, 0);
            pg.pushMatrix();
            updateDrawButton(pg, i);
            pg.popMatrix();
            if (i > 0) {
                pg.stroke(PaletteStore.get(WINDOW_BORDER));
                pg.strokeWeight(1);
                pg.line(0, 0, 0, cell - 1);
            }
            pg.popStyle();
            pg.popMatrix();
        }
        pg.stroke(PaletteStore.get(WINDOW_BORDER));
        pg.line(0, cell - 1, size.x, cell - 1);
    }


    private void updateDrawButton(PGraphics pg, int buttonIndex) {
        float n = cell * 0.28f;
        float rotation = buttonRotations.get(buttonIndex);
        pg.strokeWeight(1.2f);
        pg.noFill();
        pg.translate(cell * 0.5f, cell * 0.5f);
        pg.rotate(rotation);
        if (isMouseOverButton(buttonIndex)) {
            pg.stroke(PaletteStore.get(FOCUS_FOREGROUND));
            pg.fill(PaletteStore.get(FOCUS_BACKGROUND));
            buttonRotations.set(buttonIndex, rotation + radians(2));
        } else {
            pg.stroke(PaletteStore.get(NORMAL_FOREGROUND));
        }
        pg.beginShape();
        if (buttonIndex == 0) {
            pg.circle(0, 0, n);
            return;
        }
        buttonIndex += 2;
        for (int i = 0; i < buttonIndex; i++) {
            float theta = map(i, 0, buttonIndex - 1, 0, TAU);
            pg.vertex(n * cos(theta), n * sin(theta));
        }
        pg.endShape();
    }

    @Override
    protected void highlightNodeNodeOnMouseOver(PGraphics pg) {
        // skip the full node highlight and instead highlight each button separately
    }

    @Override
    public void mouseReleasedAnywhere(float x, float y) {
        if (isParentWindowHidden()) {
            return;
        }
        super.mouseReleasedAnywhere(x, y);
        for (int i = 0; i < buttonCount; i++) {
            if (isMouseOverButton(i)) {
                if (i == 0) {
                    State.createTreeSaveFile();
                } else if (i == 1) {
                    State.loadMostRecentTreeSave();
                }
                return;
            }
        }
    }

    private boolean isMouseOverButton(int buttonIndex) {
        return Utils.isPointInRect(State.app.mouseX, State.app.mouseY,
                pos.x + buttonIndex * cell, pos.y,
                cell, cell);
    }

    public static final int KEY_CODE_S = 83;
    public static final int KEY_CODE_L = 76;

    @Override
    public void keyPressedOutOfNode(KeyEvent e, float x, float y) {
        super.keyPressedOutOfNode(e, x, y);
        int code = e.getKeyCode();
//        println(code);
        if(e.isControlDown() && code == KEY_CODE_S){
            State.createTreeSaveFile();
        }else if(e.isControlDown() && code == KEY_CODE_L){
            State.loadMostRecentTreeSave();
        }
    }
}