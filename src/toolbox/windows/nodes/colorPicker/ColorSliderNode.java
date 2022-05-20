package toolbox.windows.nodes.colorPicker;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import processing.opengl.PShader;
import toolbox.global.themes.ThemeStore;
import toolbox.global.InternalShaderStore;
import toolbox.global.themes.ThemeColorType;
import toolbox.windows.nodes.sliders.SliderNode;
import toolbox.global.KeyCodes;

import static processing.core.PApplet.norm;
import static processing.core.PConstants.*;

public abstract class ColorSliderNode extends SliderNode {

    public final ColorPickerFolder parentColorPickerFolder;
    float maximumFloatPrecision = 0.1f;
    private final String colorShaderPath = "sliderBackgroundColor.glsl";
    protected int shaderColorMode = -1;

    public ColorSliderNode(String path, ColorPickerFolder parentFolder) {
        super(path, parentFolder, 0, 0, 1, 0.01f, true);
        this.parentColorPickerFolder = parentFolder;
        currentPrecisionIndex = precisionRange.indexOf(valueFloatPrecision);
        initSliderBackgroundShader();
        InternalShaderStore.getShader(colorShaderPath);
    }

    @Override
    public void validatePrecision() {
        if (valueFloatPrecision >= maximumFloatPrecision) {
            valueFloatPrecision = maximumFloatPrecision;
            currentPrecisionIndex = precisionRange.indexOf(maximumFloatPrecision);
        }
    }

    @Override
    public void mouseDragNodeContinue(MouseEvent e, float x, float y, float px, float py) {
        super.mouseDragNodeContinue(e, x, y, px, py);
        updateColorInParentFolder();
        e.setConsumed(true);
    }

    @Override
    public void mouseReleasedOverNode(float x, float y) {
        super.mouseReleasedOverNode(x, y);
        updateColorInParentFolder();
    }

    abstract void updateColorInParentFolder();

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        super.updateDrawInlineNode(pg);
        if(isDragged){
            pg.stroke(foregroundMouseOverBrightnessAwareColor());
            pg.strokeWeight(1);
            pg.line(size.x / 2f, 0f, size.x / 2f, size.y-1f);
        }
    }

    @Override
    protected void updateDrawBackgroundShader(PGraphics pg) {
        PShader shader = InternalShaderStore.getShader(colorShaderPath);
        shader.set("quadPos", pos.x, pos.y);
        shader.set("quadSize", size.x, size.y);
        shader.set("hueValue", parentColorPickerFolder.hue());
        shader.set("brightnessValue", parentColorPickerFolder.brightness());
        shader.set("saturationValue", parentColorPickerFolder.saturation());
        shader.set("alphaValue", parentColorPickerFolder.alpha());
        shader.set("mode", shaderColorMode);
        shader.set("precisionNormalized", norm(currentPrecisionIndex, 0, precisionRange.size()));
        InternalShaderStore.shader(colorShaderPath, pg);
    }

    @Override
    public void drawLeftText(PGraphics pg, String text) {
        pg.fill(foregroundMouseOverBrightnessAwareColor());
        super.drawLeftText(pg, text);
    }

    @Override
    public void drawRightText(PGraphics pg, String text) {
        pg.fill(foregroundMouseOverBrightnessAwareColor());
        float textMarginX = 5;
        pg.textAlign(RIGHT, CENTER);
        pg.text(text,size.x - textMarginX, size.y * 0.5f);
    }

    protected int foregroundMouseOverBrightnessAwareColor(){
        if(isMouseOverNode){
            if(parentColorPickerFolder.brightness() > 0.7f){
                return 0;
            }else{
                return 1;
            }
        }else{
            return ThemeStore.getColor(ThemeColorType.NORMAL_FOREGROUND);
        }
    }


    @Override
    public void keyPressedOverNode(KeyEvent e, float x, float y) {
        super.keyPressedOverNode(e, x, y); // handle the value change
        if (e.getKeyCode() == KeyCodes.KEY_CODE_CTRL_V) {
            // reflect the value change in the resulting color
            updateColorInParentFolder();
        }
    }

    public static class HueNode extends ColorSliderNode {

        public HueNode(String path, ColorPickerFolder parentFolder) {
            super(path, parentFolder);
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
    }

    public static class SaturationNode extends ColorSliderNode {
        public SaturationNode(String path, ColorPickerFolder parentFolder) {
            super(path, parentFolder);
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
    }

    public static class BrightnessNode extends ColorSliderNode {

        public BrightnessNode(String path, ColorPickerFolder parentFolder) {
            super(path, parentFolder);
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
    }

    public static class AlphaNode extends ColorSliderNode {

        public AlphaNode(String path, ColorPickerFolder parentFolder) {
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


        protected int foregroundMouseOverBrightnessAwareColor(){
            if(isMouseOverNode){
                if(parentColorPickerFolder.brightness() > 0.7f && valueFloat > 0.3f){
                    return 0;
                }else{
                    return 1;
                }
            }else{
                return ThemeStore.getColor(ThemeColorType.NORMAL_FOREGROUND);
            }
        }
    }
}
