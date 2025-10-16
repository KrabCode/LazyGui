package com.krab.lazy.nodes;

import com.google.gson.JsonElement;
import com.krab.lazy.input.LazyKeyEvent;
import com.krab.lazy.stores.GlobalReferences;
import com.krab.lazy.stores.JsonSaveStore;
import com.krab.lazy.stores.LayoutStore;
import com.krab.lazy.themes.ThemeColorType;
import com.krab.lazy.themes.ThemeStore;
import processing.core.PGraphics;
import processing.core.PImage;

/**
 * Folder node that holds a transient PImage (not serialized) and exposes setter/getter.
 * Contains a single inline ImagePreviewNode child to render the image inside the folder's window.
 */
public class ImageFolderNode extends FolderNode {

    private transient PImage image;
    boolean mainCanvasWorkaroundApplied = false;

    public ImageFolderNode(String path, FolderNode parent) {
        super(path, parent);
        // add the preview child
        children.add(new ImagePreviewNode(path + "/preview", this));
        // don't serialize image - ensure other folder state (like window) can be loaded normally
        JsonSaveStore.overwriteWithLoadedStateIfAny(this);
    }

    public void setImage(PImage img) {
        if(!mainCanvasWorkaroundApplied && img == GlobalReferences.app.g) {
            // there's a bug when using the main canvas and calling background() on it and trying to display it before draw() ends
            // it says "OpenOpenGL error 1282 at bot endDraw(): invalid operation" and doesn't display it
            // workaround: call endDraw() and beginDraw() on it once to force some internal state update
            ((PGraphics) img).endDraw();
            ((PGraphics) img).beginDraw();
            mainCanvasWorkaroundApplied = true;
        }
        this.image = img;
    }

    public PImage getImage() {
        return image;
    }

    @Override
    public void overwriteState(JsonElement loadedNode) {
        // use default FolderNode behavior to restore window positions, etc.
        super.overwriteState(loadedNode);
    }

    @Override
    public void keyPressedOverNode(LazyKeyEvent e, float x, float y) {
        super.keyPressedOverNode(e, x, y);
    }

    @Override
    protected void drawNodeForeground(PGraphics pg, String name) {
        // draw the standard left text
        drawLeftText(pg, name);

        // draw a small eye icon at the same place/size as other preview icons
        float previewRectSize = LayoutStore.cell * 0.6f;
        float iconX = size.x - LayoutStore.cell * 0.5f;
        float iconY = size.y * 0.5f;

        pg.pushMatrix();
        pg.translate(iconX, iconY);

        // eye outline
        if (isMouseOverNode) {
            pg.stroke(ThemeStore.getColor(ThemeColorType.FOCUS_FOREGROUND));
        } else {
            pg.stroke(ThemeStore.getColor(ThemeColorType.NORMAL_FOREGROUND));
        }
        pg.strokeWeight(1.2f);
        pg.noFill();
        float w = previewRectSize;
        float h = previewRectSize * 0.6f;
        pg.ellipse(0, 0, w, h);

        // pupil
        pg.noStroke();
        if (isMouseOverNode) {
            pg.fill(ThemeStore.getColor(ThemeColorType.FOCUS_FOREGROUND));
        } else {
            pg.fill(ThemeStore.getColor(ThemeColorType.NORMAL_FOREGROUND));
        }
        float pupilSize = h * 0.45f;
        pg.ellipse(0, 0, pupilSize, pupilSize);

        pg.popMatrix();
    }
}
