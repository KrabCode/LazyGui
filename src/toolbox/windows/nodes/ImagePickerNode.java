package toolbox.windows.nodes;

import processing.core.PGraphics;
import processing.core.PImage;
import toolbox.Gui;
import toolbox.global.State;

import static processing.core.PConstants.P2D;

public class ImagePickerNode extends FolderNode {

    PImage img;
    String knownImagePath = "";
    PGraphics ig; // image graphics

    public ImagePickerNode(String path, FolderNode parent) {
        super(path, parent);
        this.children.add(new TextNode(NodeType.VALUE_ROW, this.path + "/imagePath", this));
        ig = State.app.createGraphics(State.app.width,State.app.height, P2D);
        ig.beginDraw();
        ig.background(255,0,0, 30);
        ig.endDraw();
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
        ig.beginDraw();
        if(img == null){
            ig.background(255,0,0, 30);
            ig.endDraw();
            return ig;
        }
        ig.clear();
        ig.translate(State.gui.slider(this.path + "/x"), State.gui.slider(this.path + "/y"));
        ig.scale(State.gui.slider(this.path + "/scale", 1));
        ig.image(img, 0, 0);
        ig.endDraw();
        return ig;
    }
}
