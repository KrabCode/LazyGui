package lazy;

import processing.core.*;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static processing.core.PApplet.*;

// TODO remove Utils and State class, put things in their respective places
// https://github.com/KrabCode/LazyGui/issues/84
class Utils {

    /**
     * Hue values loop at the 1 - 0 border both in the positive and negative direction, just like two pi loops back to 0.
     * @param hue value to transfer to the [0-1] range without changing apparent color value
     * @return hue in the range between 0-1
     */
    static float hueModulo(float hue){
        if (hue < 0.f){
            return hue % 1f + 1f;
        } else {
            return hue % 1f;
        }
    }

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

    static String getSubstringFromStartToFit(PGraphics pg, String text, float availableWidth) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            float textWidthAfterNewChar = pg.textWidth(result.toString() + character);
            if (textWidthAfterNewChar >= availableWidth) {
                break;
            }
            result.append(character);
        }
        return result.toString();
    }

    static String getSubstringFromEndToFit(PGraphics pg, String text, float availableWidth){
        StringBuilder result = new StringBuilder();
        for (int i = text.length() - 1; i >= 0; i--) {
            char character = text.charAt(i);
            float textWidthAfterNewChar = pg.textWidth(result.toString() + character);
            if (textWidthAfterNewChar >= availableWidth) {
                break;
            }
            result.insert(0, character);
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
            desktop.open(State.getSaveDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ArrayListBuilder<T> {
        private final ArrayList<T> list = new ArrayList<>();

        ArrayList<T> build() {
            return list;
        }

        ArrayListBuilder<T> add(T o) {
            list.add(o);
            return this;
        }

        @SafeVarargs
        public final ArrayListBuilder<T> add(T... options) {
            for (T t :
                 options) {
                add(t);
            }
            return this;
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
            FolderNode folder = (FolderNode) node;
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
            String nodeValue = node.getConsolePrintableValue();
            boolean hasValue = nodeValue != null && nodeValue.length() > 0;
            outputBuilder.append(prefix)
                    .append(node.name)
                    .append(hasValue ? ": " : "")
                    .append(nodeValue)
                    .append("\n");
        }
        if (isFolder) {
            FolderNode folder = (FolderNode) node;
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
