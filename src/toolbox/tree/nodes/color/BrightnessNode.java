package toolbox.tree.nodes.color;

public class BrightnessNode extends ColorSliderNode {

    public BrightnessNode(String path, ColorPickerFolderNode parentFolder, float defaultValue) {
        super(path, parentFolder, defaultValue);
    }

    @Override
    void updateColorInParentFolder() {
        parentColorPickerFolder.loadValuesFromHSBA();
    }
}
