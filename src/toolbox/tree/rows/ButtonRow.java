package toolbox.tree.rows;

import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import toolbox.global.Palette;
import toolbox.global.State;

import static processing.core.PConstants.CENTER;

public class ButtonRow extends Row {
    public ButtonRow(String path, FolderRow folder) {
        super(RowType.CONTROL, path, folder);
    }

    public boolean valueBoolean = false;
    private boolean mousePressedLastFrame = false;

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        pg.noFill();
        pg.translate(size.x * 0.9f, size.y * 0.5f);
        if(mouseOver){
            if (isDragged){
                pg.fill(Palette.focusForeground);
            }else{
                pg.fill(Palette.normalForeground);
            }

        }
        pg.stroke(Palette.normalForeground);
        pg.rectMode(CENTER);
        pg.rect(0,0, cell * 0.75f, cell * 0.5f);
        boolean mousePressed = State.app.mousePressed;
        valueBoolean = mouseOver && mousePressedLastFrame && !mousePressed;
        mousePressedLastFrame = mousePressed;
    }

    @Override
    public void mouseDragRowContinue(MouseEvent e, float x, float y, float px, float py) {

    }
}
