package lazy.nodes;

import com.google.gson.JsonElement;
import processing.core.PGraphics;

class GradientColorNode extends ColorPickerFolderNode {

    GradientColorNode(String path, FolderNode parentFolder, int hex, float gradientPos, boolean active) {
        super(path, parentFolder, hex);
        this.children.add(new SliderNode(path + "/pos", parentFolder, gradientPos, 0,1,true));
        this.children.add(new ToggleNode(path + "/active", parentFolder, active));
    }

    @Override
    protected void drawNodeForeground(PGraphics pg, String name) {
        drawLeftText(pg, name);
        if(!isSkipped()){
            drawPreviewRect(pg);
        }
    }

    float getGradientPos() {
        return ((SliderNode) findChildByName("pos")).valueFloat;
    }

    boolean isSkipped(){
        return !((ToggleNode) findChildByName("active")).valueBoolean;
    }

    @Override
    public void overwriteState(JsonElement loadedNode) {
        super.overwriteState(loadedNode);
    }
}