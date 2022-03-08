package toolbox.windows.nodes;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.jogamp.newt.event.KeyEvent;
import processing.core.PGraphics;
import toolbox.global.State;
import toolbox.global.Utils;
import toolbox.global.KeyCodes;

public class FilePathNode extends AbstractNode {

    @Expose
    String filePath;

    String defaultFilePath;

    public FilePathNode(String path, FolderNode parentFolder, String defaultFilePath) {
        super(NodeType.VALUE_ROW, path, parentFolder);
        this.defaultFilePath = defaultFilePath;
        this.filePath = this.defaultFilePath;
        displayInlineName = false;
        State.overwriteWithLoadedStateIfAny(this);
    }

    protected void updateDrawInlineNode(PGraphics pg) {
        fillForegroundBasedOnMouseOver(pg);
        drawLeftText(pg, filePath);
    }

    public void drawLeftText(PGraphics pg, String text) {
        String displayText = text.equals("") ? "<paste path here>" : "<path>";
        super.drawLeftText(pg, displayText);
    }

    public void keyPressedOverNode(KeyEvent e, float x, float y) {
        super.keyPressedOverNode(e, x, y);
        if(e.getKeyCode() == KeyCodes.KEY_CODE_CTRL_C) {
            Utils.setClipboardString(filePath);
        }
        if(e.getKeyCode() == KeyCodes.KEY_CODE_CTRL_V) {
            filePath = Utils.getClipboardString();
        }
        if(e.getKeyChar() == 'r'){
            filePath = defaultFilePath;
        }
    }

    public void overwriteState(JsonElement loadedNode) {
        JsonElement textElement = loadedNode.getAsJsonObject().get("filePath");
        if(textElement != null){
            this.filePath = textElement.getAsString();
        }
    }
}
