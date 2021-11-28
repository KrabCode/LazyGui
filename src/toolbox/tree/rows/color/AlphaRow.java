package toolbox.tree.rows.color;

import toolbox.global.PaletteStore;
import toolbox.global.palettes.PaletteColorType;

public class AlphaRow extends ColorSliderRow {


    public AlphaRow(String path, ColorPickerFolderRow parentFolder, float defaultValue) {
        super(path, parentFolder, defaultValue);
        shaderColorMode = 3;
    }

    @Override
    void updateColorInParentFolder() {
        parentColorPickerFolder.loadValuesFromHSBA();
    }
    @Override
    protected void onValueResetToDefault() {
        super.onValueResetToDefault();
        parentColorPickerFolder.loadValuesFromHSBA();
    }


    protected int foregroundMouseOverBrightnessAware(){
        if(isMouseOverRow){
            if(parentColorPickerFolder.brightness() > 0.7f && valueFloat > 0.3f){
                return 0;
            }else{
                return 1;
            }
        }else{
            return PaletteStore.get(PaletteColorType.NORMAL_FOREGROUND);
        }
    }
}
