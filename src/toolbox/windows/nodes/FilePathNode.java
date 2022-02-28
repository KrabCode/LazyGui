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

public class FilePathNode extends AbstractNode {

    @Expose
    String filePath, defaultFilePath;

    public FilePathNode(NodeType type, String path, FolderNode parentFolder, String defaultFilePath) {
        super(type, path, parentFolder);
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
        if(e.getKeyCode() == KEY_CODE_CTRL_C) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection selection = new StringSelection(filePath);
            clipboard.setContents(selection, selection);
        }
        if(e.getKeyCode() == KEY_CODE_CTRL_V) {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            try {
                filePath = (String) clipboard.getData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IOException ex) {
                ex.printStackTrace();
            }
        }
        if(e.getKeyChar() == 'r'){
            filePath = defaultFilePath;
        }
    }

    public void overwriteState(JsonElement loadedNode) {
        JsonElement textElement = loadedNode.getAsJsonObject().get("text");
        if(textElement != null){
            this.filePath = textElement.getAsString();
        }
    }
}
