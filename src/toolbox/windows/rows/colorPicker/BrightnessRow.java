package toolbox.windows.rows.colorPicker;

import com.jogamp.newt.event.KeyEvent;

import static toolbox.global.KeyCodes.KEY_CODE_CTRL_V;

public class BrightnessRow extends ColorSliderRow {

    public BrightnessRow(String path, ColorPickerFolderRow parentFolder, float defaultValue) {
        super(path, parentFolder, defaultValue);
        shaderColorMode = 2;
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
        super.keyPressedOverRow(e, x, y);

        if (e.getKeyCode() == KEY_CODE_CTRL_V) {
            parentColorPickerFolder.loadValuesFromHSBA();
        }
    }
}
