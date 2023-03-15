package lazy.nodes;

import com.google.gson.JsonElement;
import lazy.PickerColor;
import lazy.ShaderReloader;
import lazy.stores.*;
import lazy.utils.ArrayListBuilder;
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

    final String gradientShaderPath = "gradient.glsl";
    int colorCount;
    int maxColorCount = 8;
    private int frameLastUpdatedOutputGraphics = -1;

    // TODO constructor that allows n input colors...
    public GradientFolderNode(String path, FolderNode parent, float alpha) {
        super(path, parent);
        colorCount = 4;
        maxColorCount = max(colorCount, maxColorCount);
        directionTypePicker = new RadioFolderNode(path + "/direction", this, directionOptions.toArray(new String[0]), directionOptions.get(1));
        blendTypePicker = new RadioFolderNode(path + "/blend", this, blendTypeOptions.toArray(new String[0]), blendTypeOptions.get(0));
        colorCountSlider = new SliderIntNode(path + "/stops", this, colorCount, 2, maxColorCount, true);
        children.add(new GradientPreviewNode(path + "/preview", this));
        children.add(directionTypePicker);
        children.add(blendTypePicker);
        children.add(colorCountSlider);
        for (int i = 0; i < maxColorCount; i++) {
            float br = 1 - map(i%colorCount, 0, colorCount, 0.2f, 0.9f);
            float iNorm = norm(i, 0, maxColorCount - 1);
            children.add(createGradientColorPicker(path + "/" + getColorNameByIndex(i),  br, alpha, iNorm));
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
        if(frameLastUpdatedOutputGraphics == app.frameCount){
            return; // weird bugs when updated more than once per frame
        }
        frameLastUpdatedOutputGraphics = app.frameCount;
        if (out == null || out.width != app.width || out.height != app.height) {
            out = app.createGraphics(app.width, app.height, P2D);
        }
        colorCount = colorCountSlider.getIntValue();
        maxColorCount = max(colorCount, maxColorCount);
        updateColorStopVisibility();

        // ShaderStore.getShader(gradientShaderPath);
        PShader shader = ShaderReloader.getShader("shaders/" + gradientShaderPath);

        shader.set("colorCount", colorCount);
        float[] colorValues = getColorValuesInPositionOrder();
        float[] colorPositions = getColorPositionsInPositionOrder();
        shader.set("colorValues", colorValues, 4);
        shader.set("colorPositions", colorPositions, 1);
        shader.set("directionType", getDirectionIndex());
        shader.set("blendType", getBlendTypeIndex());
        out.beginDraw();
        out.clear();
//        out.filter(shader);
        ShaderReloader.filter("shaders/" + gradientShaderPath, out);
        out.endDraw();
    }

    private void updateColorStopVisibility() {
        for (int i = 0; i <= maxColorCount; i++) {
            if(i < colorCount){
                NodeTree.show(path + "/" + getColorNameByIndex(i));
            }else{
                NodeTree.hide(path + "/" + getColorNameByIndex(i));
            }
        }
    }

    private int getDirectionIndex() {
        return directionOptions.indexOf(directionTypePicker.valueString);
    }

    private int getBlendTypeIndex(){
        return blendTypeOptions.indexOf(blendTypePicker.valueString);
    }

    private float[] getColorValuesInPositionOrder() {
        ArrayList<GradientColorStopNode> colorPickers = getAllColorStopsInPositionOrder();
        float[] result = new float[colorPickers.size() * 4];
        int i = 0;
        for (GradientColorStopNode colorPicker : colorPickers) {
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
        ArrayList<GradientColorStopNode> colorPickers = getAllColorStopsInPositionOrder();
        float[] result = new float[colorPickers.size()];
        int i = 0;
        for (GradientColorStopNode colorPicker : colorPickers) {
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

    ArrayList<GradientColorStopNode> getAllColorStopsInPositionOrder() {
        ArrayList<GradientColorStopNode> colorStops = new ArrayList<>();
        for (int i = 0; i < colorCount; i++) {
            colorStops.add(findColorStopByIndex(i));
        }
        // sort them by position
        colorStops.sort((o1, o2) -> Float.compare(o1.getGradientPos(), o2.getGradientPos()));
        return colorStops;
    }

    GradientColorStopNode findColorStopByIndex(int i){
        return (GradientColorStopNode) findChildByName(getColorNameByIndex(i));
    }

    public void overwriteState(JsonElement loadedNode) {
        super.overwriteState(loadedNode);
    }

    GradientColorStopNode createGradientColorPicker(String path, float brightnessNorm, float alpha, float pos) {
        int hex = color(0, 0, brightnessNorm, alpha);
        return new GradientColorStopNode(path, this, hex, pos);
    }

    public PickerColor getGradientColorAt(float position) {
        // TODO implement
        position = constrain(position, 0, 1);
        int selectedDirection = getDirectionIndex();
        int resultHexColor = NormColorStore.color(0);
        if(selectedDirection == directionOptions.indexOf("x")){
            int x = constrain(round(position * out.width), 1, out.width-1);
            resultHexColor = out.get(x, out.height/2);
        }
        if(selectedDirection == directionOptions.indexOf("y")){
            int y = constrain(round(position * out.height), 1, out.height-1);
            resultHexColor = out.get(out.width/2, y);
        }
        if(selectedDirection == directionOptions.indexOf("center")){
            resultHexColor = out.get(out.width/2, out.height / 2 + round(0.5f * position * out.height));
        }
        return new PickerColor(resultHexColor);
    }
}
