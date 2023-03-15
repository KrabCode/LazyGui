package lazy.nodes;

import lazy.stores.FontStore;
import lazy.themes.ThemeColorType;
import lazy.themes.ThemeStore;
import processing.core.PGraphics;

import static lazy.stores.LayoutStore.cell;
import static processing.core.PApplet.map;
import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.RIGHT;

class GradientPreviewNode extends AbstractNode {
    final GradientFolderNode parent;

    GradientPreviewNode(String path, GradientFolderNode parent) {
        super(NodeType.VALUE, path, parent);
        this.parent = parent;
        masterInlineNodeHeightInCells = 6;
    }

    @Override
    protected void drawNodeBackground(PGraphics pg) {
        pg.image(parent.getOutputGraphics(), 1, 1, size.x - 1, size.y - 1);
    }

    @Override
    protected void drawNodeForeground(PGraphics pg, String name) {
        drawColorStops(pg);
    }

    private void drawColorStops(PGraphics pg) {
        pg.textAlign(RIGHT, CENTER);
        pg.textFont(FontStore.getSideFont());
        for (int i = 0; i < parent.colorCount; i++) {
            GradientColorStopNode colorStop = parent.findColorStopByIndex(i);
            if(colorStop.isPosSliderBeingUsed()){
                float pointerLineY = map(colorStop.getGradientPos(), 0, 1, 1, size.y-2);
                float pointerLineWidth = cell;
                pg.strokeWeight(1);
                pg.stroke(ThemeStore.getColor(ThemeColorType.FOCUS_FOREGROUND));
                pg.pushMatrix();
                pg.translate(size.x, pointerLineY);
                pg.line(-pointerLineWidth, 0, 0, 0);
                pg.stroke(ThemeStore.getColor(ThemeColorType.NORMAL_BACKGROUND));
                pg.line(-pointerLineWidth, 1, 0, 1);
                pg.popMatrix();
            }
        }
    }

}
