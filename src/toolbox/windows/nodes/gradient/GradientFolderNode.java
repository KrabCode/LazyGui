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
    SliderIntNode colorPickerCountSlider;
    SliderIntNode directionTypeSlider;
    SliderIntNode blendTypeSlider;
    String gradientShader = "gradient.glsl";

    @Expose
    private int colorCount = 4;

    public GradientFolderNode(String path, FolderNode parent) {
        super(path, parent);
        children.add(new GradientPreviewNode(path + "/preview", this));
        colorPickerCountSlider = new SliderIntNode(path + "/count", this, colorCount, 2, 26, 0.1f,true);
        directionTypeSlider = new SliderIntNode(path + "/direction", this, 0, 0, 2, 0.1f, true);
        blendTypeSlider = new SliderIntNode(path + "/blend type", this, 0, 0, 3, 0.1f, true);
        children.add(colorPickerCountSlider);
        children.add(directionTypeSlider);
        children.add(blendTypeSlider);
        updateOutGraphics();
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        super.updateDrawInlineNode(pg);
        int previousColorCount = colorCount;
        colorCount = colorPickerCountSlider.getIntValue();
        while(colorCount > getAllGradientColorPickerChildrenInPositionOrder().size()){
            int i = getAllGradientColorPickerChildrenInPositionOrder().size();
            float iNorm = norm(i, 0, colorCount-1);
            gradientColorPicker(path + "/" + getColorNameByIndex(i), 0,0,iNorm, 1, iNorm);
        }
        if(previousColorCount != colorCount){
            updateOutGraphics();
        }
    }

    String names = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String getColorNameByIndex(int index){
        return String.valueOf(names.charAt(index));
    }

    private void updateOutGraphics(){
        PApplet app = State.app;
        if (out == null || out.width != app.width || out.height != app.height) {
            out = app.createGraphics(app.width, app.height, P2D);
        }

        children.sort((o1, o2) -> {
            if(o1.className.equals("GradientColorPickerFolderNode") && o2.className.equals("GradientColorPickerFolderNode")){
                return Float.compare(((GradientColorPickerFolderNode) o1).getGradientPos(), ((GradientColorPickerFolderNode) o2).getGradientPos());
            }
            return 0;
        });
        PShader shader = ShaderStore.lazyInitGetShader(gradientShader);
        shader.set("colorCount", colorCount);
        shader.set("colorValues", getColorValues(), 4);
        shader.set("colorPositions", getColorPositions(), 1);
        shader.set("directionType", directionTypeSlider.getIntValue());
        shader.set("blendType", blendTypeSlider.getIntValue());
        out.beginDraw();
        out.clear();
        ShaderStore.hotFilter(gradientShader, out);
        out.endDraw();
    }

    private float[] getColorValues() {
        ArrayList<GradientColorPickerFolderNode> colorPickers = getAllGradientColorPickerChildrenInPositionOrder();
        float[] result = new float[colorPickers.size() * 4];
        int i = 0;
        for(GradientColorPickerFolderNode colorPicker : colorPickers){
            Color color = colorPicker.getColor();
            result[i] = color.hue;
            result[i+1] = color.saturation;
            result[i+2] = color.brightness;
            result[i+3] = color.alpha;
            i += 4;
        }
        return result;
    }

    private float[] getColorPositions() {
        ArrayList<GradientColorPickerFolderNode> colorPickers = getAllGradientColorPickerChildrenInPositionOrder();
        float[] result = new float[colorPickers.size()];
        int i = 0;
        for(GradientColorPickerFolderNode colorPicker : colorPickers){
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
        result.sort((o1, o2) -> Float.compare(o1.getGradientPos(), o2.getGradientPos()));
        return result;
    }

    GradientColorPickerFolderNode gradientColorPicker(String path, float hueNorm, float saturationNorm, float brightnessNorm, float alphaNorm, float pos){
        GradientColorPickerFolderNode node = (GradientColorPickerFolderNode) NodeTree.findNodeByPathInTree(path);
        if (node == null) {
            int hex = State.colorProvider.color(hueNorm, saturationNorm, brightnessNorm, alphaNorm);
            node = new GradientColorPickerFolderNode(path, this, hex, pos);
            NodeTree.insertNodeAtItsPath(node);
        }
        return node;
    }

    private static class GradientPreviewNode extends AbstractNode {

        GradientFolderNode parent;

        public GradientPreviewNode(String path, GradientFolderNode parent) {
            super(NodeType.VALUE_ROW, path, parent);
            this.parent = parent;
            heightMultiplier = 2;
        }

        @Override
        protected void updateDrawInlineNode(PGraphics pg) {
            pg.image(parent.getOutputGraphics(), 0, 0, size.x, size.y);
        }

        @Override
        public void drawLeftText(PGraphics pg, String text) {
            // I skip drawing the "preview" left text by not calling super.drawLeftText() here
        }
    }

    public static class GradientColorPickerFolderNode extends ColorPickerFolderNode {
        @Expose
        private float gradientPosDefault;

        public GradientColorPickerFolderNode(String path, FolderNode parentFolder, int hex, float gradientPos) {
            super(path, parentFolder, hex);
            gradientPosDefault = gradientPos;
        }

        public float getGradientPos() {
            return State.gui.slider(path + "/pos", gradientPosDefault, 0.01f,0,1, true);
        }

        @Override
        public void overwriteState(JsonElement loadedNode) {
            super.overwriteState(loadedNode);
            JsonElement gradientPosLoaded = loadedNode.getAsJsonObject().get("gradientPosDefault");
            if(gradientPosLoaded != null){
                this.gradientPosDefault = gradientPosLoaded.getAsFloat();
                ((SliderNode)NodeTree.findNodeByPathInTree(path + "/pos")).valueFloat = gradientPosDefault;
                ((SliderNode)NodeTree.findNodeByPathInTree(path + "/pos")).valueFloatDefault = gradientPosDefault;
            }
        }
    }
}
