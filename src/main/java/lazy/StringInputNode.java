package lazy;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import processing.core.PConstants;
import processing.core.PGraphics;

public class StringInputNode extends AbstractNode {

    @Expose
    String content;

    StringInputNode(String path, FolderNode folder, String content) {
        super(NodeType.VALUE, path, folder);
        this.content = content;
        State.overwriteWithLoadedStateIfAny(this);
    }

    @Override
    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
        fillForegroundBasedOnMouseOver(pg);
        drawRightText(pg, content);
    }

    @Override
    void keyPressedOverNode(LazyKeyEvent e, float x, float y) {
        if (isMouseOverNode) {
//            PApplet.println(e.toString());
            if (e.getKeyCode() == PConstants.BACKSPACE) {
                if (content.length() > 0) {
                    content = content.substring(0, content.length() - 1);
                }
            } else if (e.getKeyCode() == KeyCodes.DELETE || e.getKeyChar() == PConstants.DELETE) {
                content = "";
            } else if (e.getKeyCode() == KeyCodes.CTRL_C && e.getKeyChar() != 'c') {
                Utils.setClipboardString(this.content);
            } else if (e.getKeyCode() == KeyCodes.CTRL_V && e.getKeyChar() != 'v') {
                content = Utils.getClipboardString();
            } else if (e.getKeyCode() != PConstants.SHIFT && e.getKeyCode() != PConstants.CONTROL && e.getKeyCode() != PConstants.ALT) {
                content = content + e.getKeyChar();
            }
        }
    }

    @Override
    void overwriteState(JsonElement loadedNode) {
        JsonObject json = loadedNode.getAsJsonObject();
        if (json.has("content")) {
            content = json.get("content").getAsString();
        }
    }


    public String getStringValue() {
        return content;
    }

    @Override
    String getConsolePrintableValue() {
        return content;
    }
}
