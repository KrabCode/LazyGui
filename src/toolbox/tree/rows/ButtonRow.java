package toolbox.tree.rows;

import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import toolbox.global.PaletteStore;
import toolbox.global.State;

import static processing.core.PConstants.CENTER;
import static toolbox.global.palettes.PaletteColorType.FOCUS_FOREGROUND;
import static toolbox.global.palettes.PaletteColorType.NORMAL_FOREGROUND;

public class ButtonRow extends Row {
    public ButtonRow(String path, FolderRow folder) {
        super(RowType.CONTROL, path, folder);
    }

    public boolean valueBoolean = false;
    private boolean mousePressedLastFrame = false;

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        pg.noFill();
        pg.translate(size.x - cell *0.5f, cell * 0.5f);
        if(isMouseOverRow){
            if (isDragged){
                pg.fill(PaletteStore.get(FOCUS_FOREGROUND));
            }else{
                pg.fill(PaletteStore.get(NORMAL_FOREGROUND));
            }

        }
        pg.stroke(PaletteStore.get(NORMAL_FOREGROUND));
        pg.rectMode(CENTER);
        pg.rect(0,0, cell * 0.25f, cell*0.15f);
        boolean mousePressed = State.app.mousePressed;
        valueBoolean = isMouseOverRow && mousePressedLastFrame && !mousePressed;
        mousePressedLastFrame = mousePressed;
    }

    @Override
    public void mouseDragRowContinue(MouseEvent e, float x, float y, float px, float py) {

    }
}
