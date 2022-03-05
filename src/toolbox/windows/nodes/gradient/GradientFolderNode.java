package toolbox.windows.nodes.gradient;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;
import toolbox.global.NodeTree;
import toolbox.global.ShaderStore;
import toolbox.global.State;
import toolbox.windows.nodes.*;
import toolbox.windows.nodes.colorPicker.Color;
import toolbox.windows.nodes.colorPicker.ColorPickerFolderNode;

import java.util.ArrayList;

import static processing.core.PApplet.*;
import static processing.core.PConstants.P2D;

public class GradientFolderNode extends FolderNode {
    PGraphics out;
    SliderIntNode directionTypeSlider;
    SliderIntNode blendTypeSlider;
    String gradientShader = "gui/gradient.glsl";
    private final int colorCount; // TODO make this default and set value with buttons at runtime

    public GradientFolderNode(String path, FolderNode parent, float alpha) {
        super(path, parent);
        directionTypeSlider = new SliderIntNode(path + "/direction", this, 0, 0, 2, 0.1f, true);
        blendTypeSlider = new SliderIntNode(path + "/blend type", this, 0, 0, 3, 0.1f, true);
        children.add(new GradientPreviewNode(path + "/preview", this));
        children.add(directionTypeSlider);
        children.add(blendTypeSlider);
        colorCount = 5;
        for (int i = 0; i < colorCount; i++) {
            float iNorm = norm(i, 0, colorCount-1);
            // default A alpha is 1 for some reason even though I set 0 here
            children.add(createGradientColorPicker(path + "/" + getColorNameByIndex(i), 0, 0, iNorm, alpha, iNorm, i % 2 == 0));
        }
        State.overwriteWithLoadedStateIfAny(this);
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        pg.translate(size.x - cell * 0.5f, cell * 0.5f);
        pg.imageMode(CENTER);
        pg.image(out,0,0,previewRectSize,previewRectSize);
        updateOutGraphics();
    }

    String getColorNameByIndex(int index){
        return String.valueOf("ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(index));
    }

    private void updateOutGraphics(){
        PApplet app = State.app;
        if (out == null || out.width != app.width || out.height != app.height) {
            out = app.createGraphics(app.width, app.height, P2D);
        }

        children.sort((o1, o2) -> {
            assert o1 != null;
            assert o2 != null;
            if (o1.className.equals("GradientColorPickerFolderNode") && o2.className.equals("GradientColorPickerFolderNode")) {
                return Float.compare(((GradientColorPickerFolderNode) o1).getGradientPos(), ((GradientColorPickerFolderNode) o2).getGradientPos());
            }
            return 0;
        });
        PShader shader = ShaderStore.lazyInitGetShader(gradientShader);
        int activeColorCount = getColorCount();
        shader.set("colorCount", activeColorCount);
        shader.set("colorValues", getColorValues(activeColorCount), 4);
        shader.set("colorPositions", getColorPositions(activeColorCount), 1);
        shader.set("directionType", directionTypeSlider.getIntValue());
        shader.set("blendType", blendTypeSlider.getIntValue());
        out.beginDraw();
        out.clear();
        ShaderStore.hotFilter(gradientShader, out);
        out.endDraw();
    }

    private int getColorCount(){
        int activeColorCount = colorCount;
        ArrayList<GradientColorPickerFolderNode> colorPickers = getAllGradientColorPickerChildrenInPositionOrder();
        for (GradientColorPickerFolderNode colorPicker : colorPickers) {
            if (colorPicker.isSkipped()) {
                activeColorCount--;
            }
        }
        return activeColorCount;
    }

    private float[] getColorValues(int activeCount) {
        ArrayList<GradientColorPickerFolderNode> colorPickers = getAllGradientColorPickerChildrenInPositionOrder();
        float[] result = new float[activeCount * 4];
        int i = 0;
        for(GradientColorPickerFolderNode colorPicker : colorPickers){
            if(colorPicker.isSkipped()){
                continue;
            }
            Color color = colorPicker.getColor();
            result[i] = color.hue;
            result[i+1] = color.saturation;
            result[i+2] = color.brightness;
            result[i+3] = color.alpha;
            i += 4;
        }
        return result;
    }

    private float[] getColorPositions(int activeCount) {
        ArrayList<GradientColorPickerFolderNode> colorPickers = getAllGradientColorPickerChildrenInPositionOrder();
        float[] result = new float[activeCount];
        int i = 0;
        for(GradientColorPickerFolderNode colorPicker : colorPickers){
            if(colorPicker.isSkipped()){
                continue;
            }
            result[i++] = colorPicker.getGradientPos();
        }
        return result;
    }

    public PGraphics getOutputGraphics() {
        updateOutGraphics();
        return out;
    }

    public ArrayList<GradientColorPickerFolderNode> getAllGradientColorPickerChildrenInPositionOrder(){
        ArrayList<GradientColorPickerFolderNode> result = new ArrayList<>();
        for(AbstractNode node : children){
            if(node.className.contains("GradientColorPickerFolderNode")){
                result.add((GradientColorPickerFolderNode) node);
            }
        }
        return result;
    }

    public void overwriteState(JsonElement loadedNode) {
        super.overwriteState(loadedNode);
    }

    GradientColorPickerFolderNode createGradientColorPicker(String path, float hueNorm, float saturationNorm, float brightnessNorm, float alphaNorm,
                                                            float pos, boolean active){
        int hex = State.normalizedColorProvider.color(hueNorm, saturationNorm, brightnessNorm, alphaNorm);
        return new GradientColorPickerFolderNode(path, this, hex, pos, active);
    }

    private static class GradientPreviewNode extends AbstractNode {

        GradientFolderNode parent;

        public GradientPreviewNode(String path, GradientFolderNode parent) {
            super(NodeType.VALUE_ROW, path, parent);
            this.parent = parent;
            heightMultiplier = 4;
        }

        @Override
        protected void updateDrawInlineNode(PGraphics pg) {
            pg.image(parent.getOutputGraphics(), 0, 0, size.x, size.y);
        }

        @Override
        public void drawLeftText(PGraphics pg, String text) {
            // skip drawing the "preview" left text by not calling super.drawLeftText() here
        }
    }

    public static class GradientColorPickerFolderNode extends ColorPickerFolderNode {
        @Expose
        private float gradientPosDefault;
        @Expose
        private boolean activeDefault;

        public GradientColorPickerFolderNode(String path, FolderNode parentFolder, int hex, float gradientPos, boolean active) {
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
            // TODO in theory none of this is needed, sliders and toggles can know and save and load the data themselves
            JsonElement gradientPosLoaded = loadedNode.getAsJsonObject().get("gradientPosDefault");
            JsonElement gradientActiveLoaded = loadedNode.getAsJsonObject().get("active");
            if(gradientPosLoaded != null){
                gradientPosDefault = gradientPosLoaded.getAsFloat();
                SliderNode pos = ((SliderNode)NodeTree.findNodeByPathInTree(path + "/pos"));
                if(pos != null){
                    pos.valueFloat = gradientPosDefault;
                    pos.valueFloatDefault = gradientPosDefault;
                }
            }
            if(gradientActiveLoaded != null){
                activeDefault = gradientActiveLoaded.getAsBoolean();
                ToggleNode active = ((ToggleNode)NodeTree.findNodeByPathInTree(path + "/active"));
                if(active != null){
                    active.valueBoolean = activeDefault;
                    active.valueBooleanDefault = activeDefault;
                }
            }
        }
    }
}
