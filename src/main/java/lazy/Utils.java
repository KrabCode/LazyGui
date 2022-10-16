package lazy;

import processing.core.*;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static processing.core.PApplet.*;

class Utils {

    static void setClipboardString(String data) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(data);
        clipboard.setContents(selection, selection);
    }

    static String getClipboardString() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            return (String) clipboard.getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    static boolean isPointInRect(float px, float py, float rx, float ry, float rw, float rh) {
        return px > rx && px < rx + rw && py >= ry && py <= ry + rh;
    }

    static String getPathWithoutName(String pathWithName) {
        String[] split = pathWithName.split("/");
        StringBuilder sum = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) {
            sum.append(split[i]);
            if (i < split.length - 2) {
                sum.append("/");
            }
        }
        return sum.toString();
    }

    @SuppressWarnings("unused")
    private static void printAvailableFonts() {
        String[] fontList = PFont.list();
        for (String s :
                fontList) {
            println(s);
        }
    }

    static void resetSketchMatrixInAnyRenderer() {
        if (State.app.sketchRenderer().equals(P3D)) {
            State.app.camera();
        } else {
            State.app.resetMatrix();
        }
    }

    static String getTrimmedTextToFitOneLine(PGraphics pg, String text, float space) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            float textWidthAfterNewChar = pg.textWidth(result.toString() + character);
            if (textWidthAfterNewChar >= space) {
                break;
            }
            result.append(character);
        }
        return result.toString();
    }

    static String[] splitFullPathWithoutEndAndRoot(String fullPath){
        String[] pathWithEnd = fullPath.split("/");
        return Arrays.copyOf(pathWithEnd, pathWithEnd.length-1);
    }

    static String generateRandomShortId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    static void openSaveFolder() {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(State.saveDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ArrayListBuilder<T> {
        private final ArrayList<T> list = new ArrayList<>();

        ArrayListBuilder<T> add(T o) {
            list.add(o);
            return this;
        }

        ArrayList<T> build() {
            return list;
        }
    }


    static String prettyPrintTree() {
        StringBuilder sb = new StringBuilder();
        buildPrettyPrintedTreeString(NodeTree.getRoot(), 1, sb);
        return sb.toString();
    }

    private static void buildPrettyPrintedTreeString(AbstractNode node, int depth, StringBuilder outputBuilder) {
        StringBuilder prefix = new StringBuilder();
        boolean hasNonTransientChildren = false;

        boolean isFolder = node.type == NodeType.FOLDER;
        if (isFolder) {
            NodeFolder folder = (NodeFolder) node;
            boolean hasChildren = folder.children.size() > 0;
            if(hasChildren){
                for(AbstractNode child : folder.children){
                    if(child.type != NodeType.TRANSIENT){
                        hasNonTransientChildren = true;
                        break;
                    }
                }
            }
        }
        boolean shouldDisplay = node.type != NodeType.TRANSIENT && (!isFolder || hasNonTransientChildren);
        if (shouldDisplay) {
            for (int i = 0; i < depth; i++) {
                boolean atMaxDepth = i == depth - 1;
                if (atMaxDepth) {
                    prefix.append(hasNonTransientChildren ? "+ " : "- ");
                } else {
                    prefix.append("|  ");
                }
            }
            String nodeValue = node.getPrintableValue();
            boolean hasValue = nodeValue != null && nodeValue.length() > 0;
            outputBuilder.append(prefix)
                    .append(node.name)
                    .append(hasValue ? ": " : "")
                    .append(nodeValue)
                    .append("\n");
        }
        if (isFolder) {
            NodeFolder folder = (NodeFolder) node;
            AbstractNode skippedOptions = null;
            for (AbstractNode child : folder.children) {
                if("options".equals(child.path)){
                    skippedOptions = child;
                    continue;
                }
                buildPrettyPrintedTreeString(child, depth + 1, outputBuilder);
            }
            if(skippedOptions != null){
                buildPrettyPrintedTreeString(skippedOptions, depth + 1, outputBuilder);
            }
        }
    }
}
