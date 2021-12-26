package toolbox.windows.nodes.gradient;

import com.google.gson.annotations.Expose;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;
import toolbox.global.ShaderStore;
import toolbox.global.State;
import toolbox.windows.nodes.AbstractNode;
import toolbox.windows.nodes.FolderNode;
import toolbox.windows.nodes.NodeType;
import toolbox.windows.nodes.colorPicker.Color;

import java.util.ArrayList;
import java.util.Comparator;

import static processing.core.PApplet.norm;
import static processing.core.PConstants.HSB;
import static processing.core.PConstants.P2D;

public class GradientFolderNode extends FolderNode {
    PGraphics out;
    String gradientShader = "gradient.glsl";
    @Expose
    private int colorCount;

    public GradientFolderNode(String path, FolderNode parent) {
        super(path, parent);
        updateOutGraphics();
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        super.updateDrawInlineNode(pg);
        colorCount = State.gui.sliderInt(path + "/count", 4, 2, 32);
    }


    // TODO why no work :sob:
    private void updateOutGraphics(){
        PApplet app = State.app;
        if (out == null || out.width != app.width || out.height != app.height) {
            out = app.createGraphics(app.width, app.height, P2D);
        }

        PShader shader = ShaderStore.lazyInitGetShader(gradientShader);
        shader.set("colorCount", colorCount);
        shader.set("colorValues", getColorValues(), 4);
        shader.set("colorPositions", getColorPositions(), 1);
        out.beginDraw();
        ShaderStore.hotFilter(gradientShader, out);
        out.endDraw();
    }

    private float[] getColorValues() {
        float[] result = new float[colorCount * 4];
        int i = 0;
        while ( i < colorCount * 4) {
            float iNorm = norm(i/4f, 0, colorCount-1);
            Color color = State.gui.colorPicker(path+"/"+(i/4), iNorm);
            result[i] = color.hue;
            result[i+1] = color.saturation;
            result[i+2] = color.brightness;
            result[i+3] = color.alpha;
            i += 4;
        }
        return result;
    }

    private float[] getColorPositions() {
        float[] result = new float[colorCount];
        for (int i = 0; i < colorCount; i++) {
            float iNorm = norm(i, 0, colorCount-1);
            result[i] = State.gui.slider(path+"/"+i+"/pos", iNorm, 0.01f, 0, 1, true);
        }
        return result;
    }

    public PGraphics getOutputGraphics() {
        updateOutGraphics();
        return out;
    }

}
