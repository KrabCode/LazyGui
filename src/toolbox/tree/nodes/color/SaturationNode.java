package toolbox.tree.nodes.color;

import static processing.core.PApplet.nf;

public class SaturationNode extends ColorSliderNode {


    public SaturationNode(String path, ColorPickerFolderNode parentFolder, float defaultValue) {
        super(path, parentFolder, defaultValue);
    }

    @Override
    void updateColorInParentFolder() {
        parentColorPickerFolder.loadValuesFromHSBA();
    }
}
