package lazy;

import processing.core.PGraphics;

import static lazy.State.cell;
import static processing.core.PConstants.*;

class StringPickerItem extends ToggleNode {

    String valueString;

    StringPickerItem(String path, FolderNode folder, boolean valueBoolean, String valueString) {
        super(path, folder, valueBoolean);
        this.type = NodeType.TRANSIENT;
        this.valueString = valueString;
    }

    @Override
    protected void updateDrawInlineNodeAbstract(PGraphics pg){
        // do not draw the right toggle handle
    }

    @Override
    void mouseReleasedOverNode(float x, float y){
        if(armed && !valueBoolean){ // can only toggle manually to true, toggle to false happens automatically
            valueBoolean = true;
        }
        armed = false;
    }

    @Override
    void drawLeftText(PGraphics pg, String text) {
        if(valueBoolean){
            pg.noStroke();
            pg.fill(ThemeStore.getColor(ThemeColorType.FOCUS_BACKGROUND));
            pg.rectMode(CORNER);
            pg.rect(0,0,size.x,size.y);
            pg.fill(ThemeStore.getColor(ThemeColorType.FOCUS_FOREGROUND));
            float rectSize = cell * 0.25f;
            pg.rectMode(CENTER);
            pg.rect(size.x - cell*0.5f, size.y - cell*0.5f, rectSize, rectSize);
        }else{
            fillForegroundBasedOnMouseOver(pg);
        }
        pg.textAlign(LEFT, CENTER);
        pg.text(text, State.textMarginX, size.y - State.textMarginY);
    }
}
