package toolbox.tree.rows.color;

import com.jogamp.newt.event.KeyEvent;

import static toolbox.global.KeyCodes.KEY_CODE_CTRL_V;

public class SaturationRow extends ColorSliderRow {


    public SaturationRow(String path, ColorPickerFolderRow parentFolder, float defaultValue) {
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
    public void keyPressedOverRow(KeyEvent e, float x, float y) {
        super.keyPressedOverRow(e,x,y);
        if(e.getKeyCode() == KEY_CODE_CTRL_V) {
            parentColorPickerFolder.loadValuesFromHSBA();
        }
    }
}
