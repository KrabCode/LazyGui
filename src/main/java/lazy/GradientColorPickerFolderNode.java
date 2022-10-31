package lazy;

import com.google.gson.JsonElement;
import processing.core.PGraphics;

import static processing.core.PConstants.ROUND;

class GradientColorPickerFolderNode extends ColorPickerFolderNode {

    GradientColorPickerFolderNode(String path, FolderNode parentFolder, int hex, float gradientPos, boolean active) {
        super(path, parentFolder, hex);
        this.children.add(new SliderNode(path + "/pos", parentFolder, gradientPos, 0,1,0.01f, true));
        this.children.add(new ToggleNode(path + "/active", parentFolder, active));
    }

    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
        super.updateDrawInlineNodeAbstract(pg);
        if(isSkipped()){
            pg.strokeCap(ROUND);
            pg.strokeWeight(1.99f);
            float n = previewRectSize * 0.25f;
            pg.line(-n,-n,n,n);
            pg.line(n,-n,-n,n);
        }
    }

    float getGradientPos() {
        return ((SliderNode) findChildByName("pos")).valueFloat;
    }

    boolean isSkipped(){
        return !((ToggleNode) findChildByName("active")).valueBoolean;
    }

    @Override
    void overwriteState(JsonElement loadedNode) {
        super.overwriteState(loadedNode);
    }
}