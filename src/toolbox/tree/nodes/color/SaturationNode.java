package toolbox.tree.nodes.color;

public class SaturationNode extends ColorSliderNode {


    public SaturationNode(String path, ColorPickerFolderNode parentFolder, float defaultValue) {
        super(path, parentFolder, defaultValue);
        shaderColorMode = 1;
    }

    @Override
    void updateColorInParentFolder() {
        parentColorPickerFolder.loadValuesFromHSBA();
    }

}
