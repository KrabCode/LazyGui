package toolbox.windows.nodes;

import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.jogamp.newt.event.KeyEvent;
import processing.core.PGraphics;
import toolbox.global.State;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import static toolbox.global.KeyCodes.KEY_CODE_CTRL_C;
import static toolbox.global.KeyCodes.KEY_CODE_CTRL_V;

public class TextNode extends AbstractNode {

    @Expose
    String text = "";

    public TextNode(NodeType type, String path, FolderNode parentFolder) {
        super(type, path, parentFolder);
        displayInlineName = false;
        State.overwriteWithLoadedStateIfAny(this);
    }

    protected void updateDrawInlineNode(PGraphics pg) {
        fillForegroundBasedOnMouseOver(pg);
        drawLeftText(pg, text);
    }

    public void drawLeftText(PGraphics pg, String text) {
        String displayText = text.equals("") ? "<no path>" : "<some path>";
        super.drawLeftText(pg, displayText);
    }

    public void keyPressedOverNode(KeyEvent e, float x, float y) {
        super.keyPressedOverNode(e, x, y);
        if(e.getKeyCode() == KEY_CODE_CTRL_C) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(text);
            clipboard.setContents(selection, selection);
        }
        if(e.getKeyCode() == KEY_CODE_CTRL_V) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            try {
                text = (String) clipboard.getData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void overwriteState(JsonElement loadedNode) {
        JsonElement textElement = loadedNode.getAsJsonObject().get("text");
        if(textElement != null){
            this.text = textElement.getAsString();
        }
    }
}
