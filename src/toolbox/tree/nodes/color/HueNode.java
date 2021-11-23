package toolbox.tree.nodes.color;

import processing.core.PGraphics;
import processing.opengl.PShader;
import toolbox.ShaderStore;

import static processing.core.PApplet.norm;

public class HueNode extends ColorSliderNode {

    public HueNode(String path, ColorPickerFolderNode parentFolder, float defaultValue) {
        super(path, parentFolder, defaultValue);
        shaderColorMode = 0;
    }

    @Override
    void updateColorInParentFolder() {
        parentColorPickerFolder.loadValuesFromHSBA();
    }

}
