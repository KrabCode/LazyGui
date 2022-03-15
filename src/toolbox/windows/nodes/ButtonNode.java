package toolbox.windows.nodes;

import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import toolbox.global.palettes.PaletteStore;
import toolbox.global.State;
import toolbox.global.palettes.PaletteColorType;

import static processing.core.PConstants.CENTER;

public class ButtonNode extends AbstractNode {
    public ButtonNode(String path, NodeFolder folder) {
        super(NodeType.VALUE_ROW, path, folder);
    }

    public boolean valueBoolean = false;
    private boolean mousePressedLastFrame = false;

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        drawButton(pg);
        boolean mousePressed = State.app.mousePressed;
        valueBoolean = isMouseOverNode && mousePressedLastFrame && !mousePressed;
        mousePressedLastFrame = mousePressed;
    }

    private void drawButton(PGraphics pg) {
        pg.noFill();
        pg.translate(size.x - cell *0.5f, cell * 0.5f);
        if(isMouseOverNode){
            if (isDragged){
                pg.fill(PaletteStore.get(PaletteColorType.FOCUS_FOREGROUND));
            }else{
                pg.fill(PaletteStore.get(PaletteColorType.NORMAL_FOREGROUND));
            }

        }
        pg.stroke(PaletteStore.get(PaletteColorType.NORMAL_FOREGROUND));
        pg.rectMode(CENTER);
        pg.rect(0,0, cell * 0.25f, cell*0.15f);
    }

    @Override
    public void mouseDragNodeContinue(MouseEvent e, float x, float y, float px, float py) {

    }
}
