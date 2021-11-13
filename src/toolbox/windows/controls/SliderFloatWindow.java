package toolbox.windows.controls;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.GlobalState;
import toolbox.tree.Node;
import toolbox.windows.Window;

import static processing.core.PApplet.constrain;
import static processing.core.PApplet.println;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.LEFT;

@SuppressWarnings("DuplicatedCode")
public class SliderFloatWindow extends Window {

    public SliderFloatWindow(Node node, PVector pos) {
        super(node, pos, new PVector(GlobalState.cell * 10, GlobalState.cell * 2));
        node.precision = 10;
    }

    public SliderFloatWindow(Node node, PVector pos, PVector size) {
        super(node, pos, size);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    protected void drawContent(PGraphics pg) {
        String text = getValueToDisplay();
        fillTextColorBasedOnFocus(pg);
        pg.textAlign(LEFT, CENTER);
        pg.text(text,
                pg.width  * 0.5f - GlobalState.font.getSize() * 0.25f,
                pg.height * 0.5f + GlobalState.font.getSize() * 0.5f
        );
    }

    protected String getValueToDisplay(){
        return String.valueOf(node.valueFloat);
    }

    @Override
    protected void reactToMouseDraggedInsideWithoutDrawing(float x, float y, float px, float py) {
        super.reactToMouseDraggedInsideWithoutDrawing(x, y, px, py);
        node.valueFloat += screenDistanceToValueDistance(x - px, node.precision);
        validateValue();
    }


    void validateValue(){
        if(node.valueFloatConstrained){
            node.valueFloat = constrain(node.valueFloat, node.valueFloatMin, node.valueFloatMax);
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        super.keyPressed(keyEvent);
        println(keyEvent.toString());
        int keyCode = keyEvent.getKeyCode();
        if(keyCode == 0x96 || keyCode == 0x10){
            node.precision *= 10;
        }
        if(keyCode == 0x98 || keyCode == 0xb){
            node.precision *= 0.1;
        }
        keyEvent.setConsumed(true);
        println(node.precision);
        println(keyEvent);
    }

    @Override
    public void mouseWheelMoved(MouseEvent e, int dir) {
        super.mouseWheelMoved(e, dir);
        if(dir > 0){
            node.precision *= 10;
        }else if(dir < 0){
            node.precision *= 0.1;
        }
        e.setConsumed(true);
    }
}
