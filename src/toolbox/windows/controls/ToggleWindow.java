package toolbox.windows.controls;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.GlobalState;
import toolbox.Palette;
import toolbox.tree.Node;
import toolbox.windows.Window;

import static processing.core.PConstants.CENTER;

public class ToggleWindow extends Window {

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
    public void keyReleased(KeyEvent keyEvent) {
        super.keyReleased(keyEvent);
        if (isThisFocused()) {
            if (keyEvent.getKeyChar() == ' ') {
                node.setValue(!(boolean) node.getValue());
            }
        }
        keyEvent.setConsumed(true);
    }

    @Override
    public void mouseReleased(MouseEvent e, float x, float y) {
        super.mouseReleased(e, x, y);

    }
}
