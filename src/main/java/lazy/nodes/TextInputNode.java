package lazy.nodes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import lazy.utils.KeyCodes;
import lazy.input.LazyKeyEvent;
import lazy.stores.FontStore;
import lazy.utils.EasyClipboard;
import lazy.utils.JsonSaves;
import processing.core.PConstants;
import processing.core.PGraphics;

public class TextInputNode extends AbstractNode {

    @Expose
    String content;

    public TextInputNode(String path, FolderNode folder, String content) {
        super(NodeType.VALUE, path, folder);
        this.content = content;
        JsonSaves.overwriteWithLoadedStateIfAny(this);
    }

    @Override
    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
        fillForegroundBasedOnMouseOver(pg);
        drawRightText(pg, getDisplayValue(pg));
    }

    @Override
    public void keyPressedOverNode(LazyKeyEvent e, float x, float y) {
        // based on tip #13 in here:
        // https://amnonp5.wordpress.com/2012/01/28/25-life-saving-tips-for-processing/
        if (isMouseOverNode) {
//            println(e.toString());
            if (e.getKeyCode() == PConstants.BACKSPACE) {
                if (content.length() > 0) {
                    content = content.substring(0, content.length() - 1);
                }
            } else if (e.getKeyCode() == KeyCodes.DELETE || e.getKeyChar() == PConstants.DELETE) {
                content = "";
            } else if (e.getKeyCode() == KeyCodes.CTRL_C && e.getKeyChar() != 'c') {
                EasyClipboard.setClipboardString(this.content);
            } else if (e.getKeyCode() == KeyCodes.CTRL_V && e.getKeyChar() != 'v') {
                content = EasyClipboard.getClipboardString();
            } else if (e.getKeyCode() != PConstants.SHIFT && e.getKeyCode() != PConstants.CONTROL && e.getKeyCode() != PConstants.ALT) {
                content = content + e.getKeyChar();
            }
        }
//        println(content.replaceAll("\\n", "(newline)"));
    }

    @Override
    public void overwriteState(JsonElement loadedNode) {
        JsonObject json = loadedNode.getAsJsonObject();
        if (json.has("content")) {
            content = json.get("content").getAsString();
        }
    }

    public String getStringValue() {
        return content;
    }

    private String getDisplayValue(PGraphics pg) {
        if(content.endsWith("\n")){
            return "";
        }
        float availableWidth = parent.window.windowSizeX - pg.textWidth(name + " ") - FontStore.textMarginX * 2;
        if(!content.contains("\n")){
            return FontStore.getSubstringFromEndToFit(pg, content, availableWidth);
        }
        String[] lines = content.split("[\\r\\n]+");
        String lastLine = lines[lines.length-1];
        return FontStore.getSubstringFromEndToFit(pg, lastLine, availableWidth);
    }

    @Override
    public String getConsolePrintableValue() {
        return content;
    }
}
