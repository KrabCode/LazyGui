package lazy.nodes;

import com.google.gson.JsonElement;
import lazy.PickerColor;
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
    int colorCount;
    private PGraphics out;
    private final ToggleNode wrapAtEdgesToggle;
    private final ToggleNode directionToggle;
    private final RadioFolderNode blendTypePicker;
    private final SliderIntNode colorCountSlider;
    private final ArrayList<String> blendTypeOptions = new ArrayListBuilder<String>()
            .add("mix").add("rgb").add("hsv").build();
    private int maxColorCount = 8;
    private int frameLastUpdatedOutputGraphics = -1;
    private int[] LUT;

    public GradientFolderNode(String path, FolderNode parent, int[] defaultColors) {
        super(path, parent);
        colorCount = 4;
        int minColorCount = 2;
        if(defaultColors != null){
            colorCount = max(minColorCount, defaultColors.length);
        }
        maxColorCount = max(colorCount, maxColorCount);
        directionToggle = new ToggleNode(path + "/vertical", this, true);
        wrapAtEdgesToggle = new ToggleNode(path + "/edge wrap", this, false);
        blendTypePicker = new RadioFolderNode(path + "/blend", this, blendTypeOptions.toArray(new String[0]), blendTypeOptions.get(0));
        colorCountSlider = new SliderIntNode(path + "/stops", this, colorCount, minColorCount, maxColorCount, true);
        children.add(new GradientPreviewNode(path + "/preview", this));
        children.add(directionToggle);
        children.add(wrapAtEdgesToggle);
        children.add(blendTypePicker);
        children.add(colorCountSlider);
        for (int i = 0; i < maxColorCount; i++) {
            float br = 1 - map(i%colorCount,0, colorCount, 0.2f, 0.9f);
            float colorPosition = norm(i%colorCount, 0, colorCount - 1);
            boolean shouldUseDefaultColor = defaultColors != null && i < defaultColors.length;
            int colorHex;
            if (shouldUseDefaultColor) {
                colorHex = defaultColors[i];
            } else {
                colorHex = color(0, 0, br, 1);
            }
            children.add(createGradientColorPicker(path + "/" + getColorNameByIndex(i), colorHex, colorPosition));
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

        String gradientShaderPath = "gradient.glsl";
//        String shaderPathLong = "shaders/gradient.glsl";
//        PShader shader = ShaderReloader.getShader(shaderPathLong);
        PShader shader = ShaderStore.getShader(gradientShaderPath);
        shader.set("colorCount", colorCount);
        ArrayList<GradientColorStopNode> colorStopsInPositionOrder = getAllColorStopsInPositionOrder();
        float[] colorValues = getColorValuesAsFloatArray(colorStopsInPositionOrder);
        float[] colorPositions = getColorPositionsAsFloatArray(colorStopsInPositionOrder);
        shader.set("colorValues", colorValues, 4);
        shader.set("colorPositions", colorPositions, 1);
        shader.set("directionType", getDirectionIndex());
        shader.set("blendType", getBlendTypeIndex());
        shader.set("wrapAtEdges", wrapAtEdgesToggle.valueBoolean);
        out.beginDraw();
        out.clear();
        out.filter(shader);
//        ShaderReloader.filter(shaderPathLong, out);
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
                NodeTree.showAtFullPath(path + "/" + getColorNameByIndex(i));
            }else{
                NodeTree.hideAtFullPath(path + "/" + getColorNameByIndex(i));
            }
        }
    }

    String getColorNameByIndex(int index) {
        String a = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return String.valueOf(a.charAt(index));
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

    GradientColorStopNode createGradientColorPicker(String path, int hex, float pos) {
        return new GradientColorStopNode(path, this, hex, pos);
    }

}
