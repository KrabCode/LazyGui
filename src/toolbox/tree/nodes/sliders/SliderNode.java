package toolbox.tree.nodes.sliders;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import processing.core.PVector;
import processing.opengl.PShader;
import toolbox.GlobalState;
import toolbox.Palette;
import toolbox.ShaderStore;
import toolbox.tree.nodes.FolderNode;
import toolbox.tree.nodes.ValueNode;

import java.util.ArrayList;
import java.util.HashMap;

import static processing.core.PApplet.*;

public class SliderNode extends ValueNode {
    public SliderNode(String path, FolderNode parentFolder) {
        super(path, parentFolder);
    }

    public float valueFloat;
    public float valueFloatMin;
    public float valueFloatMax;
    public float valueFloatDefault;
    public boolean valueFloatConstrained;
    public float valueFloatPrecision = 1;
    public float valueFloatPrecisionDefault = 1;
    float backgroundScrollX = 0;

    PVector mouseDelta = new PVector();
    ArrayList<Float> precisionRange = new ArrayList<>();
    HashMap<Float, Integer> precisionRangeDigitsAfterDot = new HashMap<>();
    int currentPrecisionIndex;
    String shaderPath = "sliderBackground.glsl";

    public void initSlider() {
        initPrecision();
        loadPrecisionFromNode();
        ShaderStore.getShader(shaderPath);
    }

    private void initPrecision() {
        precisionRange.add(0.00001f);
        precisionRange.add(0.0001f);
        precisionRange.add(0.001f);
        precisionRange.add(0.01f);
        precisionRange.add(0.1f);
        precisionRange.add(1.0f);
        precisionRange.add(10.0f);
        precisionRange.add(100.0f);
        currentPrecisionIndex = 4;
        precisionRangeDigitsAfterDot.put(0.00001f, 5);
        precisionRangeDigitsAfterDot.put(0.0001f, 4);
        precisionRangeDigitsAfterDot.put(0.001f, 3);
        precisionRangeDigitsAfterDot.put(0.01f, 2);
        precisionRangeDigitsAfterDot.put(0.1f, 1);
        precisionRangeDigitsAfterDot.put(1f, 0);
        precisionRangeDigitsAfterDot.put(10f, 0);
        precisionRangeDigitsAfterDot.put(100f, 0);
    }

    private void loadPrecisionFromNode() {
        float p = valueFloatPrecision;
        for (int i = 0; i < precisionRange.size() - 1; i++) {
            float thisValue = precisionRange.get(i);
            float nextValue = precisionRange.get(i + 1);
            if (thisValue == p) {
                currentPrecisionIndex = i;
                return;
            } else if (
                    thisValue < p && nextValue > p) {
                currentPrecisionIndex = i + 1;
                precisionRange.add(i + 1, p);
                precisionRangeDigitsAfterDot.put(p, String.valueOf(p).length() - 2);
            }
        }
    }

    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        updateDrawSliderNode(pg);
    }

    void updateDrawSliderNode(PGraphics pg) {
        String valueText = getValueToDisplay().replaceAll(",", ".");
        if (isDragged || mouseOver) {
            updateValue();
            boolean constrainedThisFrame = tryConstrainValue();
            drawBackgroundScroller(pg, constrainedThisFrame);
            mouseDelta.x = 0;
            mouseDelta.y = 0;
        }
        fillTextColorBasedOnFocus(pg);
        pg.textAlign(RIGHT, CENTER);
        float textMarginX = 5;
        pg.text(valueText,
                size.x - textMarginX,
                size.y * 0.5f
        );
/*
        if(mouseOver){
            pg.textAlign(RIGHT, CENTER);
            pg.text(getPrecisionToDisplay(),
                    size.x - textMarginX,
                    size.y * 0.5f
            );
        }*/
    }

    private void drawBackgroundScroller(PGraphics pg, boolean constrainedThisFrame) {
        if(!constrainedThisFrame){
            backgroundScrollX += mouseDelta.x;
        }
        PShader shader = ShaderStore.getShader(shaderPath);
        shader.set("scrollX", backgroundScrollX);
        shader.set("quadPos", pos.x, pos.y);
        shader.set("quadPos", pos.x, pos.y);
        shader.set("quadSize", size.x, size.y);
//        shader.set("windowSize", (float) GlobalState.app.width, (float) GlobalState.app.height);
        shader.set("precisionNormalized", norm(currentPrecisionIndex, 0, precisionRange.size()));
        ShaderStore.hotShader(shaderPath, pg);
        pg.fill(Palette.contentBackgroundFill);
        pg.noStroke();
        pg.rect(0,0, size.x, size.y);
        pg.resetShader();
    }

    public String getValueToDisplay() {
        int fractionPadding = precisionRangeDigitsAfterDot.get(valueFloatPrecision);
        if (fractionPadding == 0) {
            return String.valueOf(floor(valueFloat));
        }
        return nf(valueFloat, 0, fractionPadding);
    }

    public String getPrecisionToDisplay() {
        float p = valueFloatPrecision;
        if (p >= 1) {
            p = floor(p);
        }
        if (abs(p - 0.0001f) < 0.00001f) {
            return "0.0001";
        }
        if (abs(p - 0.00001f) < 0.000001f) {
            return "0.00001";
        }
        if (abs(p - 0.000001f) < 0.0000001f) {
            return "0.000001";
        }
        return nf(p);
    }

    @Override
    public void mouseWheelMovedInsideNode(float x, float y, int dir) {
        super.mouseWheelMovedInsideNode(x,y, dir);
        if(dir > 0){
            decreasePrecision();
        }else if(dir < 0){
            increasePrecision();
        }
    }

    private void increasePrecision() {
        currentPrecisionIndex = min(currentPrecisionIndex + 1, precisionRange.size() - 1);
        setPrecisionToNode();
    }

    private void decreasePrecision() {
        currentPrecisionIndex = max(currentPrecisionIndex - 1, 0);
        setPrecisionToNode();
    }

    protected void setPrecisionToNode() {
        valueFloatPrecision = precisionRange.get(currentPrecisionIndex);
        validatePrecision();
    }

    private void updateValue() {
        float delta = mouseDelta.x * precisionRange.get(currentPrecisionIndex);
        valueFloat += delta;
    }

    protected boolean tryConstrainValue() {
        boolean constrained = false;
        if (valueFloatConstrained) {
            if(valueFloat > valueFloatMax || valueFloat < valueFloatMin){
                constrained = true;
            }
            valueFloat = constrain(valueFloat, valueFloatMin, valueFloatMax);
        }
        return constrained;
    }

    @Override
    public void keyPressedInsideNode(KeyEvent e, float x, float y) {
        super.keyPressedInsideNode(e, x, y);
        if(e.getKeyChar() == 'r'){
            valueFloat = valueFloatDefault;
            valueFloatPrecision = valueFloatPrecisionDefault;
            currentPrecisionIndex = precisionRange.indexOf(valueFloatPrecision);
        }
    }

    int mouseTeleportCooldown = 30;
    int lastMouseTeleportFrame = -mouseTeleportCooldown;

    @Override
    public void mouseDragged(MouseEvent e, float x, float y, float px, float py) {
        super.mouseDragged(e, x, y, px, py);
        if(isDragged){
            mouseDelta.x = getTeleportSafeDelta(x - px);
            mouseDelta.y = y - py;
        }
        tryMouseTeleport(e.getX(), e.getY());
    }

    private float getTeleportSafeDelta(float delta){
        if(abs(delta) > GlobalState.app.width * 0.75f){
            float sign = delta > 0 ? 1 : -1;
            float teleportSafeDelta = abs(delta) - GlobalState.app.width;
            delta = sign * teleportSafeDelta;
        }
        return delta;
    }

    private void tryMouseTeleport(int x, int y) {
        if(GlobalState.app.frameCount < lastMouseTeleportFrame + mouseTeleportCooldown){
            return;
        }
        boolean teleported = false;
        if(x <= 3){
            GlobalState.robot.mouseMove(
                    GlobalState.window.getX() + GlobalState.app.width,
                    GlobalState.window.getY() + y
            );
            teleported = true;
        }
        if(x >= GlobalState.app.width - 3){
            GlobalState.robot.mouseMove(
                    GlobalState.window.getX() + 1,
                    GlobalState.window.getY() + y
            );
            teleported = true;
        }
        if(teleported){
            lastMouseTeleportFrame = GlobalState.app.frameCount;
        }
    }
}
