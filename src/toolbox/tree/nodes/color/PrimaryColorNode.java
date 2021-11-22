package toolbox.tree.nodes.color;


public class PrimaryColorNode extends ColorSliderNode {

    public PrimaryColorNode(String path, ColorPickerFolderNode parentFolder, float defaultValue) {
        super(path, parentFolder, defaultValue);
    }

    @Override
    void updateColorInParentFolder() {
        parentColorPickerFolder.loadValuesFromRGBA();
    }
}
