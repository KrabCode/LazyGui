package toolbox.tree.nodes.color;

import static processing.core.PApplet.nf;

public class AlphaNode extends ColorSliderNode {


    public AlphaNode(String path, ColorPickerFolderNode parentFolder, float defaultValue) {
        super(path, parentFolder, defaultValue);
    }

    @Override
    void updateColorInParentFolder() {
        parentColorPickerFolder.loadValuesFromHSBA();
    }
}
