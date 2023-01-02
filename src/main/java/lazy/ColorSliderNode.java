package lazy;



import processing.core.PGraphics;
import processing.opengl.PShader;

import static lazy.ColorStore.normColor;
import static processing.core.PApplet.norm;
import static processing.core.PConstants.*;

abstract class ColorSliderNode extends SliderNode {

    final ColorPickerFolderNode parentColorPickerFolder;
    final float maximumFloatPrecision = 1;
    private final String colorShaderPath = "sliderBackgroundColor.glsl";
    protected int shaderColorMode = -1;

    ColorSliderNode(String path, ColorPickerFolderNode parentFolder) {
        super(path, parentFolder, 0, 0, 1, true);
        this.parentColorPickerFolder = parentFolder;
        showPercentIndicatorWhenConstrained = false;
        setPrecisionIndexAndValue(precisionRange.indexOf(0.01f));
        initSliderBackgroundShader();
        ShaderStore.getShader(colorShaderPath);
    }

    @Override
    protected void validatePrecision() {
        if (valueFloatPrecision >= maximumFloatPrecision) {
            valueFloatPrecision = maximumFloatPrecision;
            currentPrecisionIndex = precisionRange.indexOf(maximumFloatPrecision);
        }
    }

    @Override
    void mouseDragNodeContinue(LazyMouseEvent e) {
        super.mouseDragNodeContinue(e);
        updateColorInParentFolder();
        e.setConsumed(true);
    }

    @Override
    void mouseReleasedOverNode(float x, float y) {
        super.mouseReleasedOverNode(x, y);
        updateColorInParentFolder();
    }

    @Override
    protected void onValueFloatChanged() {
        super.onValueFloatChanged();
        updateColorInParentFolder();
    }

    protected void updateColorInParentFolder() {
        if(parentColorPickerFolder == null) {
            return;
        }
        parentColorPickerFolder.loadValuesFromHSBA();
    }

    @Override
    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
        super.updateDrawInlineNodeAbstract(pg);
        if(isDragged){
            pg.stroke(foregroundMouseOverBrightnessAwareColor());
            pg.strokeWeight(1);
            pg.line(size.x / 2f, 0f, size.x / 2f, size.y-1f);
        }
    }

    @Override
    protected void updateBackgroundShader(PGraphics pg) {
        PShader bgShader = ShaderStore.getShader(colorShaderPath);
        bgShader.set("quadPos", pos.x, pos.y);
        bgShader.set("quadSize", size.x, size.y);
        bgShader.set("hueValue", parentColorPickerFolder.hue());
        bgShader.set("brightnessValue", parentColorPickerFolder.brightness());
        bgShader.set("saturationValue", parentColorPickerFolder.saturation());
        bgShader.set("alphaValue", parentColorPickerFolder.alpha());
        bgShader.set("mode", shaderColorMode);
        bgShader.set("precisionNormalized", norm(currentPrecisionIndex, 0, precisionRange.size()));
        pg.shader(bgShader);
    }

    @Override
    void drawLeftText(PGraphics pg, String text) {
        pg.fill(foregroundMouseOverBrightnessAwareColor());
        super.drawLeftText(pg, text);
    }

    @Override
    void drawRightText(PGraphics pg, String text) {
        pg.fill(foregroundMouseOverBrightnessAwareColor());
        pg.textAlign(RIGHT, CENTER);
        pg.text(text,size.x - FontStore.textMarginX, size.y - FontStore.textMarginY);
    }

    protected int foregroundMouseOverBrightnessAwareColor(){
        if(isMouseOverNode){
            if(parentColorPickerFolder.brightness() > 0.7f){
                return normColor(0);
            }else{
                return normColor(1);
            }
        }else{
            return ThemeStore.getColor(ThemeColorType.NORMAL_FOREGROUND);
        }
    }


    @Override
    void keyPressedOverNode(LazyKeyEvent e, float x, float y) {
        super.keyPressedOverNode(e, x, y); // handle the value change inside SliderNode
        if (e.getKeyCode() == KeyCodes.CTRL_V) {
            // reflect the value change in the resulting color
            updateColorInParentFolder();
        }
    }

    static class HueNode extends ColorSliderNode {

        HueNode(String path, ColorPickerFolderNode parentFolder) {
            super(path, parentFolder);
            shaderColorMode = 0;
        }

        @Override
        protected boolean tryConstrainValue() {
            while (valueFloat < 0) {
                valueFloat += 1;
            }
            valueFloat %= 1;
            return false;
        }
    }

    static class SaturationNode extends ColorSliderNode {
        SaturationNode(String path, ColorPickerFolderNode parentFolder) {
            super(path, parentFolder);
            shaderColorMode = 1;
        }
    }

    static class BrightnessNode extends ColorSliderNode {
        BrightnessNode(String path, ColorPickerFolderNode parentFolder) {
            super(path, parentFolder);
            shaderColorMode = 2;
        }
    }

    static class AlphaNode extends ColorSliderNode {
        AlphaNode(String path, ColorPickerFolderNode parentFolder) {
            super(path, parentFolder);
            shaderColorMode = 3;
        }
    }
}
