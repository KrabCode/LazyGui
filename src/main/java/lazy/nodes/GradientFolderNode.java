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
    final ToggleNode directionToggle;
    final RadioFolderNode blendTypePicker;
    final SliderIntNode colorCountSlider;
    final ArrayList<String> blendTypeOptions = new ArrayListBuilder<String>().add("mix").add("rgb").add("hsv").build();
    final String gradientShaderPath = "gradient.glsl";
    int colorCount;
    int maxColorCount = 8;
    private int frameLastUpdatedOutputGraphics = -1;
    int[] LUT;

    public GradientFolderNode(String path, FolderNode parent, float alpha) {
        super(path, parent);
        colorCount = 4;
        maxColorCount = max(colorCount, maxColorCount);
        directionToggle = new ToggleNode(path + "/vertical", this, true);
        blendTypePicker = new RadioFolderNode(path + "/blend", this, blendTypeOptions.toArray(new String[0]), blendTypeOptions.get(0));
        colorCountSlider = new SliderIntNode(path + "/stops", this, colorCount, 2, maxColorCount, true);
        children.add(new GradientPreviewNode(path + "/preview", this));
        children.add(directionToggle);
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
        lazyUpdateOutGraphics();
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

    private void lazyUpdateOutGraphics() {
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

//         PShader shader = ShaderStore.getShader(gradientShaderPath);
        PShader shader = ShaderReloader.getShader("shaders/" + gradientShaderPath);

        shader.set("colorCount", colorCount);
        ArrayList<GradientColorStopNode> colorStopsInPositionOrder = getAllColorStopsInPositionOrder();
        float[] colorValues = getColorValuesAsFloatArray(colorStopsInPositionOrder);
        float[] colorPositions = getColorPositionsAsFloatArray(colorStopsInPositionOrder);
        shader.set("colorValues", colorValues, 4);
        shader.set("colorPositions", colorPositions, 1);
        shader.set("directionType", getDirectionIndex());
        shader.set("blendType", getBlendTypeIndex());
        out.beginDraw();
        out.clear();
//        out.filter(shader);
        ShaderReloader.filter("shaders/" + gradientShaderPath, out);
        out.endDraw();
        updateLookUpTable(out);
    }

    private void updateLookUpTable(PGraphics out) {
        out.loadPixels();
        boolean isVertical = isGradientDirectionVertical();
        if(isVertical){
            LUT = new int[out.height];
            for (int i = 0; i < out.height; i++) {
                LUT[i] = out.pixels[out.width/2 + i * out.width];
            }
        }else{
            LUT = new int[out.width];
            System.arraycopy(out.pixels, out.width * out.height / 2, LUT, 0, out.width);
        }
    }

    public PickerColor getGradientColorAt(float position) {
        lazyUpdateOutGraphics();
        int lookupIndex = constrain(floor(map(position, 0, 1, 0, LUT.length-1)), 0, LUT.length-1);
        return new PickerColor(LUT[lookupIndex]);
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

    boolean isGradientDirectionVertical(){
        return directionToggle.valueBoolean;
    }

    private int getDirectionIndex() {
        return directionToggle.valueBoolean ? 1 : 0;
    }

    private int getBlendTypeIndex(){
        return blendTypeOptions.indexOf(blendTypePicker.valueString);
    }

    private float[] getColorValuesAsFloatArray(ArrayList<GradientColorStopNode> colorStops) {
        float[] result = new float[colorStops.size() * 4];
        int i = 0;
        for (GradientColorStopNode colorPicker : colorStops) {
            PickerColor color = colorPicker.getColor();
            result[i] = color.hue;
            result[i + 1] = color.saturation;
            result[i + 2] = color.brightness;
            result[i + 3] = color.alpha;
            i += 4;
        }
        return result;
    }

    private float[] getColorPositionsAsFloatArray(ArrayList<GradientColorStopNode> colorStops) {
        float[] result = new float[colorStops.size()];
        int i = 0;
        for (GradientColorStopNode colorPicker : colorStops) {
            result[i++] = colorPicker.getGradientPos();
        }
        return result;
    }

    public PGraphics getOutputGraphics() {
        lazyUpdateOutGraphics();
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

}
