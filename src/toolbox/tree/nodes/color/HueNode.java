package toolbox.tree.nodes.color;


public class HueNode extends ColorSliderNode {

    public HueNode(String path, ColorPickerFolderNode parentFolder, float defaultValue) {
        super(path, parentFolder, defaultValue);
        shaderColorMode = 0;
    }

    @Override
    void updateColorInParentFolder() {
        parentColorPickerFolder.loadValuesFromHSBA();
    }

    @Override
    protected boolean tryConstrainValue() {
        while(valueFloat < 0){
            valueFloat += 1;
        }
        valueFloat %= 1;
        return false;
    }
}
