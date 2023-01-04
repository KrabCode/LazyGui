package lazy.nodes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import lazy.utils.KeyCodes;
import lazy.input.LazyKeyEvent;
import lazy.stores.FontStore;
import lazy.utils.ClipboardUtils;
import lazy.utils.JsonSaves;
import processing.core.PConstants;
import processing.core.PGraphics;

import static lazy.stores.FontStore.getSubstringFromEndToFit;
import static lazy.stores.FontStore.getSubstringFromStartToFit;
import static lazy.stores.LayoutStore.cell;
import static processing.core.PConstants.*;

public class TextNode extends AbstractNode {

    @Expose
    String content;
    private final boolean isEditable;

    String regexNewLineSplit = "(?<=\\n)";

    public TextNode(String path, FolderNode folder, String content, boolean isEditable) {
        super(NodeType.VALUE, path, folder);
        this.content = content;
        this.isEditable = isEditable;
        JsonSaves.overwriteWithLoadedStateIfAny(this);
    }

    @Override
    protected void updateDrawInlineNodeAbstract(PGraphics pg) {
        fillForegroundBasedOnMouseOver(pg);
        int lineCount = content.split(regexNewLineSplit).length + (content.endsWith("\n") ? 1 : 0);
        idealInlineNodeHeightInCells = lineCount + 1; // + 1 for the node name left text (kind of serving as a header here)
        drawRightText(pg, content);
    }

    protected void drawRightText(PGraphics pg, String text) {
        fillForegroundBasedOnMouseOver(pg);
        pg.textAlign(LEFT, CENTER);
        String[] lines = content.split(regexNewLineSplit);
        pg.pushMatrix();
        float marginLeftInCells = 0.5f;
        float marginLeft = marginLeftInCells * cell;
        pg.translate(marginLeft, cell);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String lineWithoutNewline = line.replace("\n", "");
            float availableWidth = size.x - marginLeft - FontStore.textMarginX;
            String lineThatFits;
            if (i == lines.length - 1 && isEditable) {
                lineThatFits = getSubstringFromEndToFit(pg, lineWithoutNewline, availableWidth);
            } else {
                lineThatFits = getSubstringFromStartToFit(pg, lineWithoutNewline, availableWidth);
            }
            pg.translate(0, cell);
            pg.text(lineThatFits, FontStore.textMarginX, -FontStore.textMarginY);
        }
        pg.popMatrix();
    }

    @Override
    public void keyPressedOverNode(LazyKeyEvent e, float x, float y) {
        if (!isEditable) {
            return;
        }
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
                ClipboardUtils.setClipboardString(this.content);
            } else if (e.getKeyCode() == KeyCodes.CTRL_V && e.getKeyChar() != 'v') {
                content = ClipboardUtils.getClipboardString();
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

    public void setStringValue(String newValue) {
        content = newValue;
    }

    @Override
    public String getConsolePrintableValue() {
        return content;
    }
}
