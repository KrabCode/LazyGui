package gui.windows.nodes;

import processing.core.PGraphics;
import processing.core.PImage;
import gui.global.State;

import static processing.core.PConstants.P2D;

public class ImagePickerNode extends FolderNode {

    PImage img;
    String knownImagePath = "";
    PGraphics errorGraphics;
    PGraphics imageGraphics;
    PGraphics emptyGraphics;

    public ImagePickerNode(String path, FolderNode parent, String defaultFilePath) {
        super(path, parent);
        children.add(new FilePathNode(this.path + "/imagePath", this, defaultFilePath));
        children.add(new ToggleNode(this.path + "/show", this, true));
        children.add(new SliderNode(this.path + "/x", this, 0));
        children.add(new SliderNode(this.path + "/y", this, 0));
        children.add(new SliderNode(this.path + "/scale", this, 1));

        imageGraphics = State.app.createGraphics(State.app.width,State.app.height, P2D);
        imageGraphics.beginDraw();
        imageGraphics.clear();
        imageGraphics.endDraw();

        emptyGraphics = State.app.createGraphics(State.app.width,State.app.height, P2D);
        emptyGraphics.beginDraw();
        emptyGraphics.clear();
        emptyGraphics.endDraw();

        errorGraphics = State.app.createGraphics(State.app.width,State.app.height, P2D);
        errorGraphics.beginDraw();
        errorGraphics.background(0,0,0, 30);
        errorGraphics.stroke(255);
        errorGraphics.strokeWeight(4);
        errorGraphics.line(0,0,errorGraphics.width, errorGraphics.height);
        errorGraphics.line(errorGraphics.width,0,0, errorGraphics.height);
        errorGraphics.endDraw();
    }

    public PImage getOutputImage() {
        String currentImagePath = ((FilePathNode) findChildByName("imagePath")).filePath;
        if(!knownImagePath.equals(currentImagePath)){
            System.out.println("Loading image from: " + currentImagePath);
            img = State.app.loadImage(currentImagePath);
        }
        knownImagePath = currentImagePath;
        if(img == null){
            return errorGraphics;
        }
        if (!shouldDraw()) {
            return emptyGraphics;
        }
        imageGraphics.beginDraw();
        imageGraphics.clear();
        float x = ((SliderNode) findChildByName("x")).valueFloat;
        float y = ((SliderNode) findChildByName("y")).valueFloat;
        float scale = ((SliderNode) findChildByName("scale")).valueFloat;
        imageGraphics.translate(x, y);
        imageGraphics.scale(scale);
        imageGraphics.image(img, 0, 0);
        imageGraphics.endDraw();
        return imageGraphics;
    }

    private boolean shouldDraw() {
        return ((ToggleNode) findChildByName("show")).valueBoolean;
    }

}
