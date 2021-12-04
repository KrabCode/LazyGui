package toolbox.windows.nodes.colorPicker;

import com.jogamp.newt.event.KeyEvent;
import toolbox.global.PaletteStore;
import toolbox.global.palettes.PaletteColorType;

import static toolbox.global.KeyCodes.KEY_CODE_CTRL_V;

public class AlphaNode extends ColorSliderNode {


    public AlphaNode(String path, ColorPickerFolderNode parentFolder) {
        super(path, parentFolder);
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
        if(isMouseOverNode){
            if(parentColorPickerFolder.brightness() > 0.7f && valueFloat > 0.3f){
                return 0;
            }else{
                return 1;
            }
        }else{
            return PaletteStore.get(PaletteColorType.NORMAL_FOREGROUND);
        }
    }
    @Override
    public void keyPressedOverNode(KeyEvent e, float x, float y) {
        super.keyPressedOverNode(e,x,y);
        if(e.getKeyCode() == KEY_CODE_CTRL_V) {
            parentColorPickerFolder.loadValuesFromHSBA();
        }
    }
}
