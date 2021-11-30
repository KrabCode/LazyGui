package toolbox.windows.nodes.colorPicker;

import com.jogamp.newt.event.KeyEvent;

import static toolbox.global.KeyCodes.KEY_CODE_CTRL_V;

public class SaturationNode extends ColorSliderNode {


    public SaturationNode(String path, ColorPickerFolderNode parentFolder, float defaultValue) {
        super(path, parentFolder, defaultValue);
        shaderColorMode = 1;
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
    @Override
    public void keyPressedOverNode(KeyEvent e, float x, float y) {
        super.keyPressedOverNode(e,x,y);
        if(e.getKeyCode() == KEY_CODE_CTRL_V) {
            parentColorPickerFolder.loadValuesFromHSBA();
        }
    }
}
