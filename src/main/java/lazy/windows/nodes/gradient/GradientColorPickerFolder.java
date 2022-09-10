package lazy.windows.nodes.gradient;

import com.google.gson.JsonElement;
import processing.core.PGraphics;
import lazy.windows.nodes.NodeFolder;
import lazy.windows.nodes.ToggleNode;
import lazy.windows.nodes.colorPicker.ColorPickerFolder;
import lazy.windows.nodes.sliders.SliderNode;

import static processing.core.PConstants.ROUND;

public class GradientColorPickerFolder extends ColorPickerFolder {

    public GradientColorPickerFolder(String path, NodeFolder parentFolder, int hex, float gradientPos, boolean active) {
        super(path, parentFolder, hex);
        this.children.add(new SliderNode(path + "/pos", parentFolder, gradientPos, 0,1,0.01f, true));
        this.children.add(new ToggleNode(path + "/active", parentFolder, active));
    }

    protected void updateDrawInlineNodeInner(PGraphics pg) {
        super.updateDrawInlineNodeInner(pg);
        if(isSkipped()){
            pg.strokeCap(ROUND);
            pg.strokeWeight(1.99f);
            float n = previewRectSize * 0.25f;
            pg.line(-n,-n,n,n);
            pg.line(n,-n,-n,n);
        }
    }

    public float getGradientPos() {
        return ((SliderNode) findChildByName("pos")).valueFloat;
    }

    public boolean isSkipped(){
        return !((ToggleNode) findChildByName("active")).valueBoolean;
    }

    @Override
    public void overwriteState(JsonElement loadedNode) {
        super.overwriteState(loadedNode);
    }
}