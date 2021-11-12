package toolbox.windows;

import com.jogamp.newt.event.MouseEvent;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.window.Window;

public class ListWindow extends Window {

    public ListWindow(PApplet app, String path, String title, PVector pos, PVector size) {
        super(app, path, title, pos, size);
    }

    @Override
    protected void drawContent(PGraphics pg) {
        drawGrid(pg);
    }

    @Override
    public void mousePressed(MouseEvent e, float x, float y) {
        super.mousePressed(e, x, y);
        if (isPointInsideContent(x, y)) {
            e.setConsumed(true);
        }
    }
}

