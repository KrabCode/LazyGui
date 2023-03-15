package lazy.nodes;

import com.google.gson.JsonElement;
import processing.core.PGraphics;

class GradientColorNode extends ColorPickerFolderNode {

    GradientColorNode(String path, FolderNode parentFolder, int hex, float gradientPos) {
        super(path, parentFolder, hex);
        this.children.add(new SliderNode(path + "/pos", parentFolder, gradientPos, 0,1,true));
    }

    @Override
    protected void drawNodeForeground(PGraphics pg, String name) {
        drawLeftText(pg, name);
        drawPreviewRect(pg);
    }

    float getGradientPos() {
        return ((SliderNode) findChildByName("pos")).valueFloat;
    }

    @Override
    public void overwriteState(JsonElement loadedNode) {
        super.overwriteState(loadedNode);
    }
}