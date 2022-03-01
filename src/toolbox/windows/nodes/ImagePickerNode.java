package toolbox.windows.nodes;

import processing.core.PGraphics;
import processing.core.PImage;
import toolbox.global.State;

import static processing.core.PConstants.P2D;

public class ImagePickerNode extends FolderNode {

    PImage img;
    String knownImagePath = "";
    PGraphics errorGraphics;
    PGraphics imageGraphics;

    public ImagePickerNode(String path, FolderNode parent, String defaultFilePath) {
        super(path, parent);
        this.children.add(new FilePathNode(NodeType.VALUE_ROW, this.path + "/imagePath", this, defaultFilePath));

        imageGraphics = State.app.createGraphics(State.app.width,State.app.height, P2D);
        imageGraphics.beginDraw();
        imageGraphics.clear();
        imageGraphics.endDraw();

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
        String currentImagePath = ((FilePathNode) children.get(0)).filePath;
        if(!knownImagePath.equals(currentImagePath)){
            System.out.println("Loading image from: " + currentImagePath);
            img = State.app.loadImage(currentImagePath);
        }
        knownImagePath = currentImagePath;
        if(img == null){
            return errorGraphics;
        }
        imageGraphics.beginDraw();
        imageGraphics.clear();
        imageGraphics.translate(State.gui.slider(this.path + "/x"), State.gui.slider(this.path + "/y"));
        imageGraphics.scale(State.gui.slider(this.path + "/scale", 1));
        imageGraphics.image(img, 0, 0);
        imageGraphics.endDraw();
        return imageGraphics;
    }
}
