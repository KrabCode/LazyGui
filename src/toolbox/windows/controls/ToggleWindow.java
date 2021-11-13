package toolbox.windows.controls;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.GlobalState;
import toolbox.Palette;
import toolbox.tree.Node;

import static processing.core.PConstants.CENTER;

public class ToggleWindow extends ControlWindow {

    public ToggleWindow(Node node, PVector pos) {
        super(node, pos, new PVector(GlobalState.cell * 4, GlobalState.cell * 3));
    }

    @Override
    protected void drawContent(PGraphics pg) {
        boolean value = (boolean) node.getValue();
        String text = "off";
        if (value) {
            text = "on";
        }
        if(isThisFocused()){
            pg.fill(Palette.selectedTextFill);
        }else{
            pg.fill(Palette.standardTextFill);
        }
        pg.textAlign(CENTER, CENTER);
        pg.text(text, pg.width * 0.5f - GlobalState.font.getSize() * 0.25f, pg.height * 0.5f + GlobalState.font.getSize() * 0.5f);
    }

    @Override
    protected void reactToMouseReleasedInsideWithoutDrawing(float x, float y) {
        super.reactToMouseReleasedInsideWithoutDrawing(x, y);
        flipValue();
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        super.keyReleased(keyEvent);
        if(hidden){
            return;
        }
        if (isThisFocused()) {
            if (keyEvent.getKeyChar() == ' ') {
                flipValue();
            }
        }
        keyEvent.setConsumed(true);
    }

    private void flipValue() {
        node.setValue(!(boolean) node.getValue());
    }


}
