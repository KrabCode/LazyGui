package toolbox.windows;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.window.Window;

public class TestWindow extends Window {

    public TestWindow(PApplet app, String path, String title, PVector pos, PVector size) {
        super(app, path, title, pos, size);
    }

    @Override
    protected void drawContent(PGraphics pg) {

    }
}
