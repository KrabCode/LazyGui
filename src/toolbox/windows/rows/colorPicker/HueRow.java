package toolbox.windows.rows.colorPicker;


import com.jogamp.newt.event.KeyEvent;

import static toolbox.global.KeyCodes.KEY_CODE_CTRL_V;

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
        while (valueFloat < 0) {
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


    @Override
    public void keyPressedOverRow(KeyEvent e, float x, float y) {
        super.keyPressedOverRow(e, x, y);
        if (e.getKeyCode() == KEY_CODE_CTRL_V) {
            parentColorPickerFolder.loadValuesFromHSBA();
        }
    }
}
