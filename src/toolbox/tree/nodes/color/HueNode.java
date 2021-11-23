package toolbox.tree.nodes.color;

import processing.core.PGraphics;
import processing.opengl.PShader;
import toolbox.ShaderStore;

import static processing.core.PApplet.norm;

public class HueNode extends ColorSliderNode {

    String hueShader = "sliderBackgroundHue.glsl";

    public HueNode(String path, ColorPickerFolderNode parentFolder, float defaultValue) {
        super(path, parentFolder, defaultValue);
        ShaderStore.lazyInitGetShader(hueShader);
    }

    @Override
    void updateColorInParentFolder() {
        parentColorPickerFolder.loadValuesFromHSBA();
    }

    @Override
    protected void updateDrawBackgroundShader(PGraphics pg) {
        PShader shader = ShaderStore.lazyInitGetShader(hueShader);
        shader.set("quadPos", pos.x, pos.y);
        shader.set("quadSize", size.x, size.y);
        shader.set("hueValue", valueFloat);
        shader.set("brightnessValue", parentColorPickerFolder.brightness());
        shader.set("saturationValue", parentColorPickerFolder.saturation());
        shader.set("precisionNormalized", norm(currentPrecisionIndex, 0, precisionRange.size()));
        ShaderStore.hotShader(hueShader, pg);
    }
}
