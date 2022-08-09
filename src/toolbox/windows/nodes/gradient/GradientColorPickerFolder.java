package toolbox.windows.nodes.gradient;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import processing.core.PGraphics;
import toolbox.global.NodeTree;
import toolbox.global.State;
import toolbox.windows.nodes.NodeFolder;
import toolbox.windows.nodes.ToggleNode;
import toolbox.windows.nodes.colorPicker.ColorPickerFolder;
import toolbox.windows.nodes.sliders.SliderNode;

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
        // TODO in theory none of this is needed, the underlying sliders and toggles can know and save and load the data themselves
        JsonElement gradientPosLoaded = loadedNode.getAsJsonObject().get("gradientPosDefault");
        JsonElement gradientActiveLoaded = loadedNode.getAsJsonObject().get("active");
        if(gradientPosLoaded != null){
            gradientPosDefault = gradientPosLoaded.getAsFloat();
            SliderNode pos = ((SliderNode) NodeTree.findNode(path + "/pos"));
            if(pos != null){
                pos.valueFloat = gradientPosDefault;
                pos.valueFloatDefault = gradientPosDefault;
            }
        }
        if(gradientActiveLoaded != null){
            activeDefault = gradientActiveLoaded.getAsBoolean();
            ToggleNode active = ((ToggleNode)NodeTree.findNode(path + "/active"));
            if(active != null){
                active.valueBoolean = activeDefault;
                active.valueBooleanDefault = activeDefault;
            }
        }
    }
}