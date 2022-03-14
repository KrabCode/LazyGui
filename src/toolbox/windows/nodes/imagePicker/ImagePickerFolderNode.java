package toolbox.windows.nodes.imagePicker;

import processing.core.PGraphics;
import processing.core.PImage;
import toolbox.global.State;
import toolbox.windows.nodes.FolderNode;
import toolbox.windows.nodes.sliders.SliderNode;
import toolbox.windows.nodes.ToggleNode;

import static processing.core.PConstants.P2D;

public class ImagePickerFolderNode extends FolderNode {

    PImage img;
    String knownImagePath = "";
    PGraphics errorGraphics;
    PGraphics imageGraphics;
    PGraphics emptyGraphics;

    public ImagePickerFolderNode(String path, FolderNode parent, String defaultFilePath) {
        super(path, parent);
        children.add(new ImagePickerFilePathNode(this.path + "/imagePath", this, defaultFilePath));
        children.add(new ToggleNode(this.path + "/show", this, true));
        children.add(new SliderNode(this.path + "/x", this, 0));
        children.add(new SliderNode(this.path + "/y", this, 0));
        children.add(new SliderNode(this.path + "/scale", this, 1, 0, Float.MAX_VALUE, 0.1f, true));

        imageGraphics = State.app.createGraphics(State.app.width, State.app.height, P2D);
        imageGraphics.beginDraw();
        imageGraphics.clear();
        imageGraphics.endDraw();

        emptyGraphics = State.app.createGraphics(State.app.width, State.app.height, P2D);
        emptyGraphics.beginDraw();
        emptyGraphics.clear();
        emptyGraphics.endDraw();

        errorGraphics = State.app.createGraphics(State.app.width, State.app.height, P2D);
    }

    public PImage getOutputImage() {
        String currentImagePath = ((ImagePickerFilePathNode) findChildByName("imagePath")).filePath;
        float x = ((SliderNode) findChildByName("x")).valueFloat;
        float y = ((SliderNode) findChildByName("y")).valueFloat;
        float scale = ((SliderNode) findChildByName("scale")).valueFloat;
        if (!knownImagePath.equals(currentImagePath)) {
            System.out.println("Loading image from: " + currentImagePath);
            img = State.app.loadImage(currentImagePath);
        }
        knownImagePath = currentImagePath;
        if (!shouldDraw()) {
            return emptyGraphics;
        }
        if (!isImageReady()) {
            updateErrorGraphics(x, y, scale);
            return errorGraphics;
        }
        updateImageGraphics(x, y, scale);
        return imageGraphics;
    }

    private void updateErrorGraphics(float x, float y, float scale) {
        errorGraphics.beginDraw();
        errorGraphics.clear();
        errorGraphics.translate(x, y);
        errorGraphics.scale(scale);
        errorGraphics.fill(State.normalizedColorProvider.color(0.5f, 0.1f));
        errorGraphics.noStroke();
        errorGraphics.rect(0, 0, errorGraphics.width, errorGraphics.height);
        errorGraphics.endDraw();
    }

    private void updateImageGraphics(float x, float y, float scale) {
        imageGraphics.beginDraw();
        imageGraphics.clear();
        imageGraphics.translate(x, y);
        imageGraphics.scale(scale);
        imageGraphics.image(img, 0, 0);
        imageGraphics.endDraw();
    }

    boolean isImageReady() {
        return img != null && (img.width > 0 && img.height > 0);
    }

    private boolean shouldDraw() {
        return ((ToggleNode) findChildByName("show")).valueBoolean;
    }


}
