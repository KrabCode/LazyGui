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
        if(valueBoolean){
            pg.noStroke();
            pg.fill(ThemeStore.getColor(ThemeColorType.FOCUS_BACKGROUND));
            pg.rectMode(CORNER);
            pg.rect(1,0,size.x-1,size.y);
            pg.fill(ThemeStore.getColor(ThemeColorType.FOCUS_FOREGROUND));
            float rectSize = cell * 0.25f;
            pg.rectMode(CENTER);
            pg.rect(size.x - cell*0.5f, size.y - cell*0.5f, rectSize, rectSize);
        }
    }

    @Override
    void mouseReleasedOverNode(float x, float y){
        if(armed && !valueBoolean){ // can only toggle manually to true, toggle to false happens automatically
            valueBoolean = true;
        }
        armed = false;
    }
}
