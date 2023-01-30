package lazy.nodes;



import lazy.utils.KeyCodes;
import lazy.input.LazyKeyEvent;
import lazy.input.LazyMouseEvent;
import lazy.stores.NormColorStore;
import lazy.stores.ShaderStore;
import lazy.themes.ThemeColorType;
import lazy.themes.ThemeStore;
import processing.core.PGraphics;
import processing.opengl.PShader;

import static processing.core.PApplet.norm;

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
    public void mouseDragNodeContinue(LazyMouseEvent e) {
        super.mouseDragNodeContinue(e);
        updateColorInParentFolder();
        e.setConsumed(true);
    }

    @Override
    public void mouseReleasedOverNode(float x, float y) {
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
    protected void drawNodeBackground(PGraphics pg) {
        super.drawNodeBackground(pg);
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
    protected void drawNodeForeground(PGraphics pg, String name) {
        pg.fill(foregroundMouseOverBrightnessAwareColor());
        drawLeftText(pg, name);
        drawRightText(pg, getValueToDisplay(), false);
    }

    protected int foregroundMouseOverBrightnessAwareColor(){
        if(isMouseOverNode){
            if(parentColorPickerFolder.brightness() > 0.7f){
                return NormColorStore.color(0);
            }else{
                return NormColorStore.color(1);
            }
        }else{
            return ThemeStore.getColor(ThemeColorType.NORMAL_FOREGROUND);
        }
    }


    @Override
    public void keyPressedOverNode(LazyKeyEvent e, float x, float y) {
        super.keyPressedOverNode(e, x, y); // handle the value change inside SliderNode
        if (e.getKeyCode() == KeyCodes.V) {
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
