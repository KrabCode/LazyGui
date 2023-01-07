package lazy.nodes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import lazy.themes.ThemeColorType;
import lazy.themes.ThemeStore;
import lazy.utils.KeyCodes;
import lazy.input.LazyKeyEvent;
import lazy.stores.FontStore;
import lazy.utils.ClipboardUtils;
import lazy.utils.JsonSaves;
import processing.core.PConstants;
import processing.core.PGraphics;


import static lazy.stores.FontStore.*;
import static lazy.stores.LayoutStore.cell;
import static processing.core.PConstants.*;

public class TextNode extends AbstractNode {

    @Expose
    String content;
    private final boolean isEditable;
    float marginLeftInCells = 0.2f;

    String regexLookBehindForNewLine = "(?<=\\n)";

    public TextNode(String path, FolderNode folder, String content, boolean isEditable) {
        super(NodeType.VALUE, path, folder);
        this.content = content;
        this.isEditable = isEditable;
        JsonSaves.overwriteWithLoadedStateIfAny(this);
    }

    @Override
    protected void drawNodeBackground(PGraphics pg) {
        if(idealInlineNodeHeightInCells - 1 < 0){
            return;
        }
        pg.fill(ThemeStore.getColor(ThemeColorType.WINDOW_BORDER));
        pg.noStroke();
        float boxMarginLeft = marginLeftInCells * cell;
        float boxX = 0;
        float boxY = cell;
        float boxHeight = (idealInlineNodeHeightInCells - 1) * cell;
        float boxWidth = boxMarginLeft;
        pg.rectMode(CORNER);
        pg.rect(boxX, boxY, boxWidth, boxHeight);
    }

    @Override
    protected void drawNodeForeground(PGraphics pg, String name) {
        drawLeftText(pg, name);
        strokeForegroundBasedOnMouseOver(pg);
        pg.noFill();
        if(!isEditable){
            drawRightEyeIcon(pg);
        }else{
            drawRightKeyboardIcon(pg);
        }
        int lineCount = content.split(regexLookBehindForNewLine).length + (content.endsWith("\n") ? 1 : 0);
        idealInlineNodeHeightInCells = lineCount + 1; // + 1 for the node name left text (kind of serving as a header here)
        String contentToDraw = content.length() == 0 ? "..." : content;
        fillForegroundBasedOnMouseOver(pg);
        drawContent(pg, contentToDraw);
    }

    private void drawRightEyeIcon(PGraphics pg) {

    }

    private void drawRightKeyboardIcon(PGraphics pg) {

    }

    protected void drawContent(PGraphics pg, String content) {
        fillForegroundBasedOnMouseOver(pg);
        pg.textAlign(LEFT, CENTER);
        String[] lines = content.split(regexLookBehindForNewLine);
        pg.pushMatrix();
        float marginLeft = marginLeftInCells * cell;
        pg.translate(marginLeft, cell);
        pg.textFont(FontStore.getSideFont());
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String lineTrimmed = line.replace("\n", "");
            float availableWidth = size.x - marginLeft * 2 - FontStore.textMarginX;
            String lineThatFits;
            if (i == lines.length - 1 && isEditable) {
                // FromEnd because you want to see what you're typing
                lineThatFits = getSubstringFromEndToFit(pg, lineTrimmed, availableWidth);
            } else {
                // FromStart because you want to read everything else from the start
                lineThatFits = getSubstringFromStartToFit(pg, lineTrimmed, availableWidth);
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
//            println("key code" + e.getKeyCode());
            if(KeyCodes.isKeyCodeIgnored(e.getKeyCode())){
                return;
            }
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
