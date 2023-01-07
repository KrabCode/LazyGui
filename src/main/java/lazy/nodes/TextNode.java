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
    float marginLeftInCells = 0.2f;

    String regexLookBehindForNewLine = "(?<=\\n)";
    private final boolean shouldDisplayHeaderRow;

    public TextNode(String path, FolderNode folder, String content) {
        super(NodeType.VALUE, path, folder);
        this.content = content;
        shouldDisplayHeaderRow = !name.trim().isEmpty();
        JsonSaves.overwriteWithLoadedStateIfAny(this);
    }

    @Override
    protected void drawNodeBackground(PGraphics pg) {
        drawLeftIndentBox(pg);
    }

    @Override
    protected void drawNodeForeground(PGraphics pg, String name) {
        fillForegroundBasedOnMouseOver(pg);
        int lineCount = content.split(regexLookBehindForNewLine).length + (content.endsWith("\n") ? 1 : 0);
        if(shouldDisplayHeaderRow){
            drawLeftText(pg, name);
            lineCount += 1;
        }
        masterInlineNodeHeightInCells = lineCount;
        String contentToDraw = content.length() == 0 ? "..." : content;
        fillForegroundBasedOnMouseOver(pg);
        drawContent(pg, contentToDraw);
    }

    private void drawLeftIndentBox(PGraphics pg) {
        if(masterInlineNodeHeightInCells == 0){
            return;
        }
        pg.fill(ThemeStore.getColor(ThemeColorType.WINDOW_BORDER));
        pg.noStroke();
        float boxX = 0;
        float boxY = 0;
        float boxHeight = masterInlineNodeHeightInCells * cell;
        float boxWidth = marginLeftInCells * cell;
        if(shouldDisplayHeaderRow){
            boxY = cell;
            boxHeight -= cell;
        }
        pg.rectMode(CORNER);
        pg.rect(boxX, boxY, boxWidth, boxHeight);
    }

    protected void drawContent(PGraphics pg, String contentToDraw) {
        fillForegroundBasedOnMouseOver(pg);
        pg.textAlign(LEFT, CENTER);
        String[] lines = contentToDraw.split(regexLookBehindForNewLine);
        pg.pushMatrix();
        float marginLeft = marginLeftInCells * cell;
        if(shouldDisplayHeaderRow){
            pg.translate(0, cell);
        }
        pg.translate(marginLeft, 0);
        pg.textFont(FontStore.getSideFont());
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String lineTrimmed = line.replace("\n", "");
            float availableWidth = size.x - marginLeft * 2 - FontStore.textMarginX;
            String lineThatFits;
            if (i == lines.length - 1) {
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
