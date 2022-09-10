package lazy.windows.nodes.gradient;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import processing.core.PGraphics;
import lazy.global.NodeTree;
import lazy.windows.nodes.NodeFolder;
import lazy.windows.nodes.ToggleNode;
import lazy.windows.nodes.colorPicker.ColorPickerFolder;
import lazy.windows.nodes.sliders.SliderNode;

import static processing.core.PConstants.ROUND;

public class GradientColorPickerFolder extends ColorPickerFolder {
    @Expose
    private float gradientPosDefault;
    @Expose
    private boolean activeDefault;

    public GradientColorPickerFolder(String path, NodeFolder parentFolder, int hex, float gradientPos, boolean active) {
        super(path, parentFolder, hex);
        this.children.add(new SliderNode(path + "/pos", parentFolder, gradientPos, 0,1,0.01f, true));
        this.children.add(new ToggleNode(path + "/active", parentFolder, active));
        gradientPosDefault = gradientPos;
        activeDefault = active;
    }

    protected void updateDrawInlineNode(PGraphics pg) {
        super.updateDrawInlineNode(pg);
        if(isSkipped()){
            pg.strokeCap(ROUND);
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
        SliderNode pos = (SliderNode) findChildByName("pos");
        if(pos != null){
            pos.overwriteState(loadedNode.getAsJsonObject().get("gradientPosDefault"));
        }
        // TODO in theory none of this is needed, the underlying sliders and toggles can know and save and load the data themselves
        ToggleNode active = (ToggleNode) findChildByName("active");
        if(active != null){
            active.overwriteState(loadedNode.getAsJsonObject().get("active"));
        }
    }
}