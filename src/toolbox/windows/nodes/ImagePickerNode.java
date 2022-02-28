package toolbox.windows.nodes;

import processing.core.PGraphics;
import processing.core.PImage;
import toolbox.Gui;
import toolbox.global.State;

import static processing.core.PConstants.P2D;

public class ImagePickerNode extends FolderNode {

    PImage img;
    String knownImagePath = "";
    PGraphics errorGraphics;
    PGraphics imageGraphics;

    public ImagePickerNode(String path, FolderNode parent) {
        super(path, parent);
        this.children.add(new TextNode(NodeType.VALUE_ROW, this.path + "/imagePath", this));
        imageGraphics = State.app.createGraphics(State.app.width,State.app.height, P2D);
        imageGraphics.beginDraw();
        imageGraphics.clear();
        imageGraphics.endDraw();
        errorGraphics = State.app.createGraphics(State.app.width,State.app.height, P2D);
        errorGraphics.beginDraw();
        errorGraphics.background(0,0,0, 30);
        errorGraphics.stroke(255);
        errorGraphics.line(0,0,errorGraphics.width, errorGraphics.height);
        errorGraphics.line(errorGraphics.width,0,0, errorGraphics.height);
        errorGraphics.endDraw();
    }

    protected void updateDrawInlineNode(PGraphics pg) {
        super.updateDrawInlineNode(pg);
        String currentImagePath = ((TextNode) children.get(0)).text;
        if(!knownImagePath.equals(currentImagePath)){
            System.out.println("Loading image from: " + currentImagePath);
            img = State.app.loadImage(currentImagePath);
        }
        knownImagePath = currentImagePath;
    }

    public PImage getOutputImage() {
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
