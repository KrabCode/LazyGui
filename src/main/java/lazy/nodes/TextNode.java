package lazy.nodes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import lazy.stores.*;
import lazy.themes.ThemeColorType;
import lazy.themes.ThemeStore;
import lazy.utils.KeyCodes;
import lazy.input.LazyKeyEvent;
import lazy.utils.ClipboardUtils;
import processing.core.PConstants;
import processing.core.PGraphics;


import static lazy.stores.FontStore.*;
import static lazy.stores.GlobalReferences.app;
import static lazy.stores.LayoutStore.cell;
import static processing.core.PConstants.*;

public class TextNode extends AbstractNode {

    @Expose
    String stringValue;
    String buffer;

    private final int millisInputDelay;
    private int millisInputStarted;

    private final float marginLeftInCells = 0.3f;
    private final String regexLookBehindForNewLine = "(?<=\\n)";
    private final boolean shouldDisplayHeaderRow;

    public TextNode(String path, FolderNode folder, String content) {
        super(NodeType.VALUE, path, folder);
        this.stringValue = content;
        this.buffer = content;
        shouldDisplayHeaderRow = !name.trim().isEmpty();
        millisInputDelay = DelayStore.getKeyboardBufferDelayMillis();
        millisInputStarted = -millisInputDelay * 2;
        JsonSaveStore.overwriteWithLoadedStateIfAny(this);
    }

    @Override
    protected void drawNodeBackground(PGraphics pg) {
        drawLeftIndentLine(pg);
    }

    @Override
    protected void drawNodeForeground(PGraphics pg, String name) {
        fillForegroundBasedOnMouseOver(pg);
        String toDisplay = buffer;
        int lineCount = toDisplay.split(regexLookBehindForNewLine).length + (toDisplay.endsWith("\n") ? 1 : 0);
        if(shouldDisplayHeaderRow){
            drawLeftText(pg, name);
            lineCount += 1;
        }
        masterInlineNodeHeightInCells = lineCount;
        String contentToDraw = toDisplay.length() == 0 ? "..." : toDisplay;
        fillForegroundBasedOnMouseOver(pg);
        drawContent(pg, contentToDraw);
    }

    private void drawLeftIndentLine(PGraphics pg) {
        if(masterInlineNodeHeightInCells == 0){
            return;
        }
        pg.stroke(ThemeStore.getColor(ThemeColorType.WINDOW_BORDER));
        float n = marginLeftInCells * cell;
        float verticalMargin = n;
        float boxX = n;
        float boxY = 0;
        float boxHeight = masterInlineNodeHeightInCells * cell;
        if(shouldDisplayHeaderRow){
            boxY = cell;
            boxHeight -= cell;
        }
        pg.rectMode(CORNER);
        pg.line(boxX, boxY + verticalMargin, boxX, boxY + boxHeight - verticalMargin);
    }

    protected void drawContent(PGraphics pg, String contentToDraw) {
        fillForegroundBasedOnMouseOver(pg);
        pg.textAlign(LEFT, CENTER);
        String[] lines = contentToDraw.split(regexLookBehindForNewLine);
        pg.pushMatrix();
        float contentMarginLeft = marginLeftInCells * cell;
        if(shouldDisplayHeaderRow){
            pg.translate(0, cell);
        }
        pg.translate(0, 0);
        pg.textFont(FontStore.getSideFont());
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].replace("\n", "");
            float textFieldWidth = size.x - contentMarginLeft - FontStore.textMarginX;
            float indicatorWidth = cell * 0.5f;
            float textFieldWidthMinusIndicator = textFieldWidth - indicatorWidth;
            String lineThatFits = getSubstringFromStartToFit(pg, line, textFieldWidth);
            boolean wasLineTrimmed = lineThatFits.length() < line.length();
            if(wasLineTrimmed){
                // trim it further to give indicator some space
                lineThatFits = getSubstringFromStartToFit(pg, line, textFieldWidthMinusIndicator);
            }
            boolean isLastLine = i == lines.length - 1;
            if (isLastLine) {
                // last line is displayed "fromEnd" because you want to see what you're typing,
                // and you never want to draw the right indicator there
                lineThatFits = getSubstringFromEndToFit(pg, line, textFieldWidth);
            }else if(wasLineTrimmed){
                drawRightTrimIndicator(pg, indicatorWidth);
            }
            pg.translate(0, cell);

            pg.text(lineThatFits, contentMarginLeft + FontStore.textMarginX, -FontStore.textMarginY);
        }
        pg.popMatrix();
    }

    private void drawRightTrimIndicator(PGraphics pg, float trimIndicatorWidth) {
        pg.pushMatrix();
        pg.pushStyle();
        pg.translate(size.x-trimIndicatorWidth, 0);
        fillBackgroundBasedOnMouseOver(pg);
        pg.beginShape();
        pg.vertex(0,0);
        pg.fill(ThemeStore.getColor(ThemeColorType.WINDOW_BORDER));
        pg.vertex(trimIndicatorWidth, 0);
        pg.vertex(trimIndicatorWidth, cell);
        fillBackgroundBasedOnMouseOver(pg);
        pg.vertex(0, cell);
        pg.endShape();
        pg.popStyle();
        pg.popMatrix();
    }

    @Override
    public void updateValuesRegardlessOfParentWindowOpenness() {
        trySetContentToBufferAfterDelay();
    }

    private void trySetContentToBufferAfterDelay() {
        if(!stringValue.equals(buffer) && app.millis() > millisInputStarted + millisInputDelay){
            setStringValueUndoably(buffer);
        }
    }

    @Override
    public void keyPressedOverNode(LazyKeyEvent e, float x, float y) {
        // based on tip #13 in here:
        // https://amnonp5.wordpress.com/2012/01/28/25-life-saving-tips-for-processing/
        if (isMouseOverNode) {
//            PApplet.println("key code" + e.getKeyCode());
            if(KeyCodes.shouldIgnoreForTextInput(e.getKeyCode())){
                return;
            }
            millisInputStarted = app.millis();
            if (e.getKeyCode() == PConstants.BACKSPACE) {
                if (buffer.length() > 0) {
                    buffer = buffer.substring(0, buffer.length() - 1);
                }
            } else if (e.getKeyCode() == KeyCodes.DELETE || e.getKey() == PConstants.DELETE) {
                buffer = "";
            } else if (e.isControlDown() && e.getKeyCode() == KeyCodes.C) {
                ClipboardUtils.setClipboardString(this.buffer);
            } else if (e.isControlDown() && e.getKeyCode() == KeyCodes.V) {
                setStringValueUndoably(ClipboardUtils.getClipboardString());
            } else if(!e.isControlDown() && !e.isAltDown()){
                buffer = buffer + e.getKey();
            }
        }
    }

    @Override
    public void overwriteState(JsonElement loadedNode) {
        JsonObject json = loadedNode.getAsJsonObject();
        if (json.has("stringValue")) {
            stringValue = json.get("stringValue").getAsString();
            buffer = stringValue;
        }
    }

    public String getStringValue() {
        return stringValue;
    }

    private void setStringValueUndoably(String newValue) {
        setStringValue(newValue);
        UndoRedoStore.onUndoableActionEnded();
    }

    public void setStringValue(String newValue) {
        stringValue = newValue;
        buffer = newValue;
    }

    @Override
    public String getConsolePrintableValue() {
        return stringValue;
    }
}
