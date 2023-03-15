package lazy.nodes;

import com.google.gson.JsonElement;
import lazy.PickerColor;
import lazy.stores.NodeTree;
import lazy.stores.ShaderStore;
import lazy.utils.ArrayListBuilder;
import lazy.stores.JsonSaveStore;
import processing.core.PGraphics;
import processing.opengl.PShader;

import java.util.ArrayList;

import static lazy.stores.NormColorStore.color;
import static lazy.stores.GlobalReferences.app;
import static lazy.stores.LayoutStore.cell;
import static processing.core.PApplet.*;

public class GradientFolderNode extends FolderNode {
    PGraphics out;
    final RadioFolderNode directionTypePicker;
    final RadioFolderNode blendTypePicker;
    final SliderIntNode colorCountSlider;
    final ArrayList<String> blendTypeOptions = new ArrayListBuilder<String>().add("mix").add("rgb").add("hsv").build();
    final ArrayList<String> directionOptions = new ArrayListBuilder<String>().add("x").add("y").add("center").build();

    final String gradientShader = "gradient.glsl";
    private final int maxColorCount = 10;
    private int colorCount;
    private int lastUpdatedFrame = -1;

    // TODO constructor that allows n input colors...
    public GradientFolderNode(String path, FolderNode parent, float alpha) {
        super(path, parent);
        colorCount = 3;
        directionTypePicker = new RadioFolderNode(path + "/direction", this, directionOptions.toArray(new String[0]), directionOptions.get(1));
        blendTypePicker = new RadioFolderNode(path + "/blend type", this, blendTypeOptions.toArray(new String[0]), blendTypeOptions.get(0));
        colorCountSlider = new SliderIntNode(path + "/color count", this, colorCount, 2, maxColorCount, true);
        children.add(new GradientPreviewNode(path + "/preview", this));
        children.add(directionTypePicker);
        children.add(blendTypePicker);
        children.add(colorCountSlider);
        idealWindowWidthInCells = 9;
        for (int i = 0; i < maxColorCount; i++) {
            float iNorm = norm(i, 0, maxColorCount - 1);
            children.add(createGradientColorPicker(path + "/" + getColorNameByIndex(i),  1-iNorm, alpha, iNorm));
        }
        JsonSaveStore.overwriteWithLoadedStateIfAny(this);

    }

    @Override
    public void updateValuesRegardlessOfParentWindowOpenness() {
        updateOutGraphics();
    }

    @Override
    protected void drawNodeForeground(PGraphics pg, String name) {
        drawLeftText(pg, name);
        drawGradientPreviewIcon(pg);
    }

    void drawGradientPreviewIcon(PGraphics pg){
        pg.translate(size.x - cell * 0.5f, cell * 0.5f);
        pg.imageMode(CENTER);
        float previewRectSize = cell * 0.6f;
        pg.image(out, 0, 0, previewRectSize, previewRectSize);
        strokeForegroundBasedOnMouseOver(pg);
        pg.rectMode(CENTER);
        pg.noFill();
        pg.rect(0, 0, previewRectSize, previewRectSize);
    }

    String getColorNameByIndex(int index) {
        String a = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return String.valueOf(a.charAt(index));
    }

    private void updateOutGraphics() {
        if(lastUpdatedFrame == app.frameCount){
            return; // weird bugs when updated more than once per frame
        }
        lastUpdatedFrame = app.frameCount;
        if (out == null || out.width != app.width || out.height != app.height) {
            out = app.createGraphics(app.width, app.height, P2D);
        }

        colorCount = colorCountSlider.getIntValue();
        updateColorStopChildren();

        PShader shader = ShaderStore.getShader(gradientShader);
        shader.set("colorCount", colorCount);
        // add fake copied neighbour colors on each(?) end of the gradient to not have to require it from the user
        float[] colorValues = surroundWithColorPadding(getColorValuesInPositionOrder());
        float[] colorPositions = surroundWithPositionPadding(getColorPositionsInPositionOrder());
        shader.set("colorValues", colorValues, 4);
        shader.set("colorPositions", colorPositions, 1);
        shader.set("directionType", directionOptions.indexOf(directionTypePicker.valueString));
        shader.set("blendType", blendTypeOptions.indexOf(blendTypePicker.valueString));
        out.beginDraw();
        out.clear();
        out.filter(shader);
        out.endDraw();
    }

    private float[] surroundWithPositionPadding(float[] positions) {
        // TODO implement using array copying
        return positions;
    }

    private float[] surroundWithColorPadding(float[] colors) {
        // TODO implement using array copying
        return colors;
    }

    private void updateColorStopChildren() {
        for (int i = 0; i <= maxColorCount; i++) {
            if(i < colorCount){
                NodeTree.show(path + "/" + getColorNameByIndex(i));
            }else{
                NodeTree.hide(path + "/" + getColorNameByIndex(i));
            }
        }
    }

    private float[] getColorValuesInPositionOrder() {
        ArrayList<GradientColorNode> colorPickers = getAllColorStopsInPositionOrder();
        float[] result = new float[colorPickers.size() * 4];
        int i = 0;
        for (GradientColorNode colorPicker : colorPickers) {
            PickerColor color = colorPicker.getColor();
            result[i] = color.hue;
            result[i + 1] = color.saturation;
            result[i + 2] = color.brightness;
            result[i + 3] = color.alpha;
            i += 4;
        }
        return result;
    }

    private float[] getColorPositionsInPositionOrder() {
        ArrayList<GradientColorNode> colorPickers = getAllColorStopsInPositionOrder();
        float[] result = new float[colorPickers.size()];
        int i = 0;
        for (GradientColorNode colorPicker : colorPickers) {
            result[i++] = colorPicker.getGradientPos();
        }
        // enforce that positions reach both ends
        result[0] = 0;
        result[result.length-1] = 1;
        return result;
    }

    public PGraphics getOutputGraphics() {
        updateOutGraphics();
        return out;
    }

    ArrayList<GradientColorNode> getAllColorStopsInPositionOrder() {
        ArrayList<GradientColorNode> colorStops = new ArrayList<>();
        for (int i = 0; i < colorCount; i++) {
            colorStops.add((GradientColorNode) findChildByName(getColorNameByIndex(i)));
        }
        // sort them by position
        colorStops.sort((o1, o2) -> Float.compare(o1.getGradientPos(), o2.getGradientPos()));
        return colorStops;
    }

    public void overwriteState(JsonElement loadedNode) {
        super.overwriteState(loadedNode);
    }

    GradientColorNode createGradientColorPicker(String path, float brightnessNorm, float alphaNorm, float pos) {
        int hex = color(0, 0, brightnessNorm, alphaNorm);
        return new GradientColorNode(path, this, hex, pos);
    }

    public PickerColor getGradientColorAt(float position) {
        // TODO implement
        position = constrain(position, 0, 1);
        return new PickerColor(0,0,0,0,1);
    }

    private static class GradientPreviewNode extends AbstractNode {

        final GradientFolderNode parent;

        GradientPreviewNode(String path, GradientFolderNode parent) {
            super(NodeType.VALUE, path, parent);
            this.parent = parent;
            masterInlineNodeHeightInCells = 6;
        }

        @Override
        protected void drawNodeBackground(PGraphics pg) {
            pg.image(parent.getOutputGraphics(), 1, 1, size.x-1, size.y-1);
        }

        @Override
        protected void drawNodeForeground(PGraphics pg, String name) {

        }

    }

}
