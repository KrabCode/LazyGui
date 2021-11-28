package toolbox.tree.rows.color;


public class HueRow extends ColorSliderRow {

    public HueRow(String path, ColorPickerFolderRow parentFolder, float defaultValue) {
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

    @Override
    protected void onValueResetToDefault() {
        super.onValueResetToDefault();
        parentColorPickerFolder.loadValuesFromHSBA();
    }
}