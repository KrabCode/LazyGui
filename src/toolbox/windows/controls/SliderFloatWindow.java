package toolbox.windows.controls;

import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.GlobalState;
import toolbox.Palette;
import toolbox.tree.Node;

import static processing.core.PApplet.nf;
import static processing.core.PConstants.CENTER;

public class SliderFloatWindow extends ControlWindow {

    private boolean isDraggedInside = false;

    public SliderFloatWindow(Node node, PVector pos) {
        super(node, pos, new PVector(GlobalState.cell * 8, GlobalState.cell * 4));
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    protected void drawContent(PGraphics pg) {
        String text = nf((float) node.getValue(), 2, 2);
        if(isThisFocused()){
            pg.fill(Palette.selectedTextFill);
        }else{
            pg.fill(Palette.standardTextFill);
        }
        pg.textAlign(CENTER, CENTER);
        pg.text(text, pg.width * 0.5f - GlobalState.font.getSize() * 0.25f, pg.height * 0.5f + GlobalState.font.getSize() * 0.5f);
    }

    @Override
    protected void reactToMouseDraggedInsideWithoutDrawing(float x, float y, float px, float py) {
        super.reactToMouseDraggedInsideWithoutDrawing(x, y, px, py);
        float deltaX = (x - px) * 0.01f;
        node.setValue((float) node.getValue() + deltaX);
    }
}
