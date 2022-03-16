package toolbox.windows.nodes.select;

import processing.core.PGraphics;
import toolbox.global.State;
import toolbox.global.palettes.PaletteColorType;
import toolbox.global.palettes.PaletteStore;
import toolbox.windows.nodes.NodeFolder;
import toolbox.windows.nodes.ToggleNode;

import static processing.core.PConstants.*;

public class SelectStringItem extends ToggleNode {

    String valueString;

    public SelectStringItem(String path, NodeFolder folder, boolean valueBoolean, String valueString) {
        super(path, folder, valueBoolean);
        this.valueString = valueString;
    }

    @Override
    public void updateDrawInlineNode(PGraphics pg){
        // do not draw the right toggle handle
    }

    @Override
    public void mouseReleasedOverNode(float x, float y){
        if(armed && !valueBoolean){ // can only toggle manually to true, toggle to false happens automatically
            valueBoolean = true;
        }
        armed = false;
    }

    @Override
    public void drawLeftText(PGraphics pg, String text) {
        if(valueBoolean){
            pg.noStroke();
            pg.fill(PaletteStore.get(PaletteColorType.FOCUS_BACKGROUND));
            pg.rectMode(CORNER);
            pg.rect(0,0,size.x,size.y);
            pg.fill(PaletteStore.get(PaletteColorType.FOCUS_FOREGROUND));
            float rectSize = cell * 0.25f;
            pg.rectMode(CENTER);
            pg.rect(size.x - cell*0.5f, size.y - cell*0.5f, rectSize, rectSize);
        }else{
            pg.fill(PaletteStore.get(PaletteColorType.NORMAL_FOREGROUND));
        }
        pg.textAlign(LEFT, CENTER);
        pg.text(text, State.textMarginX, size.y - State.font.getSize() * 0.6f);
    }
}
