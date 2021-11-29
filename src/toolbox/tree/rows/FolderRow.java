package toolbox.tree.rows;

import com.jogamp.newt.event.MouseEvent;
import processing.core.PGraphics;
import processing.core.PVector;
import toolbox.tree.windows.FolderWindow;
import toolbox.tree.windows.WindowManager;

import java.util.ArrayList;

import static processing.core.PConstants.CENTER;

public class FolderRow extends Row {
    public ArrayList<Row> children = new ArrayList<>();
    public FolderWindow window;

    public FolderRow(String path, FolderRow parent) {
        super(RowType.FOLDER, path, parent);
    }


    @Override
    protected void updateDrawInlineNode(PGraphics pg) {
        strokeForegroundBasedOnMouseOver(pg);

        pg.fill(0);
        pg.translate(size.x - cell * 0.5f, size.y * 0.5f);
        float rectSize = cell * 0.5f;
        pg.translate(1,1);
        pg.rectMode(CENTER);
        pg.rect(0,0,rectSize, rectSize);
        pg.translate(-2,-2);
        pg.noFill();
        pg.rect(0,0,rectSize, rectSize);
    }

    @Override
    public void rowPressed(float x, float y) {

        super.rowPressed(x, y);
        WindowManager.uncoverOrCreateWindow(this, new PVector(x - cell * 0.5f, y-cell * 0.5f));
        this.isDragged = false;

    }

    @Override
    public void mouseDragRowContinue(MouseEvent e, float x, float y, float px, float py) {

    }
}
