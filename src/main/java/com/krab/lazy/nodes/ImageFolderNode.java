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

        // if there is no image, show the eye with a slash
        boolean showSlash = (image == null);
        drawInlineEye(pg, showSlash);
    }

    private void drawInlineEye(PGraphics pg, boolean showSlash) {
        // draw a small eye icon at the same place/size as other preview icons
        float previewRectSize = LayoutStore.cell * 0.6f;
        float iconX = size.x - LayoutStore.cell * 0.5f;
        float iconY = size.y * 0.5f;

        pg.pushMatrix();
        pg.translate(iconX, iconY);

        float w = previewRectSize;
        float h = previewRectSize * 0.6f;

        // outline color depends on hover
        int stroke = isMouseOverNode ? ThemeStore.getColor(ThemeColorType.FOCUS_FOREGROUND) : ThemeStore.getColor(ThemeColorType.NORMAL_FOREGROUND);

        // eye outline
        pg.pushStyle();
        pg.stroke(stroke);
        pg.strokeWeight(1.2f);
        pg.noFill();
        pg.ellipse(0, 0, w, h);

        // pupil
        pg.noStroke();
        pg.fill(stroke);
        float pupilSize = h * 0.45f;
        pg.ellipse(0, 0, pupilSize, pupilSize);

        if (showSlash) {
            // optional slash when showing null state
            pg.stroke(stroke);
            pg.strokeWeight(2f);
            float slashHalfW = w * 0.45f;
            float slashHalfH = h * 0.45f;
            pg.line(-slashHalfW, -slashHalfH, slashHalfW, slashHalfH);
        }
        pg.popStyle();
        pg.popMatrix();
    }
}
