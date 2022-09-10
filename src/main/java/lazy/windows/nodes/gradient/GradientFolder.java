package lazy.windows.nodes.gradient;

import com.google.gson.JsonElement;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;
import lazy.global.InternalShaderStore;
import lazy.global.State;
import lazy.global.Utils;
import lazy.windows.nodes.*;
import lazy.windows.nodes.colorPicker.PickerColor;
import lazy.windows.nodes.select.StringPickerFolder;

import java.util.ArrayList;

import static processing.core.PApplet.*;

public class GradientFolder extends NodeFolder {
    PGraphics out;
    StringPickerFolder directionTypePicker;
    StringPickerFolder blendTypePicker;
    ArrayList<String> blendTypeOptions = new Utils.ArrayListBuilder<String>().add("mix").add("rgb").add("hsv").build();
    ArrayList<String> directionOptions = new Utils.ArrayListBuilder<String>().add("x").add("y").add("center").build();

    String gradientShader = "gradient.glsl";
    private final int colorCount;

    public GradientFolder(String path, NodeFolder parent, float alpha) {
        super(path, parent);
        directionTypePicker = new StringPickerFolder(path + "/direction", this, directionOptions.toArray(new String[0]), directionOptions.get(1));
        blendTypePicker = new StringPickerFolder(path + "/blend type", this, blendTypeOptions.toArray(new String[0]), blendTypeOptions.get(0));
        children.add(new GradientPreviewNode(path + "/preview", this));
        children.add(directionTypePicker);
        children.add(blendTypePicker);
        colorCount = 5;
        idealWindowWidth = State.cell * 9;
        for (int i = 0; i < colorCount; i++) {
            float iNorm = norm(i, 0, colorCount - 1);
            // default A alpha is 1 for some reason even though I set 0 here
            children.add(createGradientColorPicker(path + "/" + getColorNameByIndex(i), 0, 0, iNorm, alpha, iNorm, i % 2 == 0));
        }
        State.overwriteWithLoadedStateIfAny(this);
    }

    @Override
    protected void updateDrawInlineNodeInner(PGraphics pg) {
        pg.translate(size.x - cell * 0.5f, cell * 0.5f);
        pg.imageMode(CENTER);
        pg.image(out, 0, 0, previewRectSize, previewRectSize);
        strokeForegroundBasedOnMouseOver(pg);
        pg.rectMode(CENTER);
        pg.noFill();
        pg.rect(0, 0, previewRectSize, previewRectSize);
        updateOutGraphics();
    }

    String getColorNameByIndex(int index) {
        String a = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        return String.valueOf(a.charAt(index));
    }

    private void updateOutGraphics() {
        PApplet app = State.app;
        if (out == null || out.width != app.width || out.height != app.height) {
            out = app.createGraphics(app.width, app.height, P2D);
        }

        children.sort((o1, o2) -> {
            assert o1 != null;
            assert o2 != null;
            if (o1.className.equals("GradientColorPickerFolder") && o2.className.equals("GradientColorPickerFolder")) {
                return Float.compare(((GradientColorPickerFolder) o1).getGradientPos(), ((GradientColorPickerFolder) o2).getGradientPos());
            }
            return 0;
        });
        PShader shader = InternalShaderStore.getShader(gradientShader);
        int activeColorCount = getColorCount();
        shader.set("colorCount", activeColorCount);
        shader.set("colorValues", getColorValues(activeColorCount), 4);
        shader.set("colorPositions", getColorPositions(activeColorCount), 1);
        shader.set("directionType", directionOptions.indexOf(directionTypePicker.valueString));
        shader.set("blendType", blendTypeOptions.indexOf(blendTypePicker.valueString));
        out.beginDraw();
        out.clear();
        InternalShaderStore.filter(gradientShader, out);
        out.endDraw();
    }

    private int getColorCount() {
        int activeColorCount = colorCount;
        ArrayList<GradientColorPickerFolder> colorPickers = getAllGradientColorPickerChildrenInPositionOrder();
        for (GradientColorPickerFolder colorPicker : colorPickers) {
            if (colorPicker.isSkipped()) {
                activeColorCount--;
            }
        }
        return activeColorCount;
    }

    private float[] getColorValues(int activeCount) {
        ArrayList<GradientColorPickerFolder> colorPickers = getAllGradientColorPickerChildrenInPositionOrder();
        float[] result = new float[activeCount * 4];
        int i = 0;
        for (GradientColorPickerFolder colorPicker : colorPickers) {
            if (colorPicker.isSkipped()) {
                continue;
            }
            PickerColor color = colorPicker.getColor();
            result[i] = color.hue;
            result[i + 1] = color.saturation;
            result[i + 2] = color.brightness;
            result[i + 3] = color.alpha;
            i += 4;
        }
        return result;
    }

    private float[] getColorPositions(int activeCount) {
        ArrayList<GradientColorPickerFolder> colorPickers = getAllGradientColorPickerChildrenInPositionOrder();
        float[] result = new float[activeCount];
        int i = 0;
        for (GradientColorPickerFolder colorPicker : colorPickers) {
            if (colorPicker.isSkipped()) {
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

    public ArrayList<GradientColorPickerFolder> getAllGradientColorPickerChildrenInPositionOrder() {
        ArrayList<GradientColorPickerFolder> result = new ArrayList<>();
        for (AbstractNode node : children) {
            if (node.className.contains("GradientColorPickerFolder")) {
                result.add((GradientColorPickerFolder) node);
            }
        }
        return result;
    }

    public void overwriteState(JsonElement loadedNode) {
        super.overwriteState(loadedNode);
    }

    GradientColorPickerFolder createGradientColorPicker(String path, float hueNorm, float saturationNorm, float brightnessNorm, float alphaNorm,
                                                        float pos, boolean active) {
        int hex = State.normalizedColorProvider.color(hueNorm, saturationNorm, brightnessNorm, alphaNorm);
        return new GradientColorPickerFolder(path, this, hex, pos, active);
    }

    private static class GradientPreviewNode extends AbstractNode {

        GradientFolder parent;

        public GradientPreviewNode(String path, GradientFolder parent) {
            super(NodeType.VALUE, path, parent);
            this.parent = parent;
            rowHeightInCells = 9;
        }

        @Override
        protected void updateDrawInlineNodeInner(PGraphics pg) {
            pg.image(parent.getOutputGraphics(), 0, 0, size.x, size.y);
        }

        @Override
        public void drawLeftText(PGraphics pg, String text) {
            // we skip drawing the "preview" left text by not calling super.drawLeftText() here
        }
    }

}
