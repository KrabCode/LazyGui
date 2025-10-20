package com.krab.lazy.nodes;

import com.krab.lazy.stores.LayoutStore;
import com.krab.lazy.stores.FontStore;
import com.krab.lazy.themes.ThemeColorType;
import com.krab.lazy.themes.ThemeStore;
import processing.core.PGraphics;
import processing.core.PImage;

import static processing.core.PConstants.CORNER;

/**
 * A transient preview node that draws a PImage centered and uniformly scaled to fit the node bounds.
 * It does not own or serialize the image; the parent `ImageFolderNode` stores the transient image.
 */
class ImagePreviewNode extends AbstractNode {

    private final ImageFolderNode parentFolder;

    ImagePreviewNode(String path, ImageFolderNode parent) {
        super(NodeType.TRANSIENT, path, parent);
        this.parentFolder = parent;
        masterInlineNodeHeightInCells = 4;
        isInlineNodeDraggable = false;
    }

    @Override
    public void updateValuesRegardlessOfParentWindowOpenness() {
        // adjust height (in cells) based on the parent window width to preserve image aspect ratio
        PImage img = parentFolder.getImage();
        if (img == null || parent == null || parent.window == null) {
            return;
        }
        float availableW = parent.window.windowSizeX - 2; // match drawing padding
        if (availableW <= 0 || img.width <= 0) {
            return;
        }
        // desired draw width is the available width for the node
        // compute scale to make image match available width
        float scaleForWidth = parent.window.windowSizeX / (float) img.width;
        // clamp to 1.0 to avoid upscaling
        scaleForWidth = Math.min(scaleForWidth, 1.0f);
        // compute desired height in pixels
        float desiredDrawH = img.height * scaleForWidth;
        // convert to cells, apply immediately (no smoothing)
        masterInlineNodeHeightInCells = Math.max(1f, desiredDrawH / LayoutStore.cell);
    }

    @Override
    protected void drawNodeBackground(PGraphics pg) {
        PImage img = parentFolder.getImage();
        if (img == null) {
            // draw a neutral background and a short message
            pg.pushStyle();
            pg.noStroke();
            pg.fill(ThemeStore.getColor(ThemeColorType.NORMAL_BACKGROUND));
            pg.rect(0, 0, size.x, size.y);
            // set text style and draw message centered vertically, left padded
            fillForegroundBasedOnMouseOver(pg);
            pg.textFont(FontStore.getSideFont());
            pg.textAlign(processing.core.PConstants.LEFT, processing.core.PConstants.CENTER);
            String msg = "null image";
            String trimmed = FontStore.getSubstringFromStartToFit(pg, msg, size.x - FontStore.textMarginX * 2);
            float x = FontStore.textMarginX;
            float y = FontStore.textMarginY;
            pg.text(trimmed, x, y);
            pg.popStyle();
            return;
        }

        float availableW = size.x;
        float availableH = size.y;
        float imgW = img.width;
        float imgH = img.height;
        if (imgW <= 0 || imgH <= 0) {
            return;
        }
        float scale = Math.min(availableW / imgW, availableH / imgH);
        // clamp scale to 1.0 to avoid upscaling
        scale = Math.min(scale, 1.0f);
        // allow upscaling so image grows as window is resized while preserving aspect ratio
        float drawW = imgW * scale;
        float drawH = imgH * scale;
        // align image to top-left with a small padding instead of centering
        float offsetX = 1f;
        float offsetY = 1f;

        pg.pushStyle();
        pg.imageMode(CORNER);
        pg.noStroke();
        pg.fill(ThemeStore.getColor(ThemeColorType.NORMAL_BACKGROUND));
        pg.rect(0, 0, size.x, size.y);
        pg.tint(0xFFFFFFFF);
        if(scale > 0.99f){
            // draw pixel-perfect if not scaled
            pg.image(img, offsetX, offsetY);
        }else{
            pg.image(img, offsetX, offsetY, drawW, drawH);
        }
        pg.popStyle();
    }

    @Override
    protected void drawNodeForeground(PGraphics pg, String name) {
        // no foreground for preview
    }
}
