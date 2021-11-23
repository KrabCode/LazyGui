package toolbox.tree.nodes.color;

import processing.core.PGraphics;

import static processing.core.PApplet.*;

public class AlphaNode extends ColorSliderNode {


    public AlphaNode(String path, ColorPickerFolderNode parentFolder, float defaultValue) {
        super(path, parentFolder, defaultValue);
        shaderColorMode = 3;
    }

    @Override
    void updateColorInParentFolder() {
        parentColorPickerFolder.loadValuesFromHSBA();
    }
}
