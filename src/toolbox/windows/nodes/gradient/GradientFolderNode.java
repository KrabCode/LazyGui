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
    String gradientShader = "gradient.glsl";
    private int colorCount;

    public GradientFolderNode(String path, FolderNode parent) {
        super(path, parent);
        directionTypeSlider = new SliderIntNode(path + "/direction", this, 0, 0, 2, 0.1f, true);
        blendTypeSlider = new SliderIntNode(path + "/blend type", this, 0, 0, 3, 0.1f, true);
        children.add(new GradientPreviewNode(path + "/preview", this));
        children.add(directionTypeSlider);
        children.add(blendTypeSlider);
        colorCount = 5;
        for (int i = 0; i < colorCount; i++) {
            float iNorm = norm(i, 0, colorCount-1);
            children.add(createGradientColorPicker(path + "/" + getColorNameByIndex(i), 0, 0, iNorm, 1, iNorm));
        }

        // TODO maybe State.overwriteWithLoadedStateIfAny(this); instead of the loadMostRecentSave on frame 2 inside Gui.draw()
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
            if(o1 == null || o2 == null){
                int i = 1;
            }
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
        return result;
    }

    GradientColorPickerFolderNode createGradientColorPicker(String path, float hueNorm, float saturationNorm, float brightnessNorm, float alphaNorm, float pos){
        int hex = State.colorProvider.color(hueNorm, saturationNorm, brightnessNorm, alphaNorm);
        return new GradientColorPickerFolderNode(path, this, hex, pos);
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
            // I skip drawing the "preview" left text by not calling super.drawLeftText() here
        }
    }

    public static class GradientColorPickerFolderNode extends ColorPickerFolderNode {
        @Expose
        private float gradientPosDefault;

        public GradientColorPickerFolderNode(String path, FolderNode parentFolder, int hex, float gradientPos) {
            super(path, parentFolder, hex);
            this.children.add(new SliderNode(path + "/pos", parentFolder, gradientPos, 0,1,0.01f, true));
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
