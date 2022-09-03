package lazy.global;

import lazy.windows.nodes.AbstractNode;
import lazy.windows.nodes.NodeFolder;
import lazy.windows.nodes.NodeType;
import processing.core.*;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static processing.core.PApplet.*;

public class Utils {

    public static void setClipboardString(String data) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(data);
        clipboard.setContents(selection, selection);
    }

    public static String getClipboardString() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            return (String) clipboard.getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public static boolean isPointInRect(float px, float py, float rx, float ry, float rw, float rh) {
        return px > rx && px < rx + rw && py >= ry && py <= ry + rh;
    }

    public static String getPathWithoutName(String pathWithName) {
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

    public static void resetSketchMatrixInAnyRenderer() {
        if (State.app.sketchRenderer().equals(P3D)) {
            State.app.camera();
        } else {
            State.app.resetMatrix();
        }
    }

    public static String getTrimmedTextToFitOneLine(PGraphics pg, String text, float space) {
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < text.length(); i++){
            char character = text.charAt(i);
            float textWidthAfterNewChar = pg.textWidth(result.toString() + character);
            if(textWidthAfterNewChar >= space){
                break;
            }
            result.append(character);
        }
        return result.toString();
    }

    public static String generateRandomShortId() {
        return UUID.randomUUID().toString().replace("-","").substring(0,8);
    }

    public static void openSaveFolder() {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(State.saveDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ArrayListBuilder<T>{
        private final ArrayList<T> list = new ArrayList<>();

        public ArrayListBuilder<T> add(T o){
            list.add(o);
            return this;
        }

        public ArrayList<T> build(){
            return list;
        }
    }


    public static String prettyPrintTree(){
        StringBuilder sb = new StringBuilder();
        printNTree(NodeTree.getRoot(), new boolean[1000], 0, false, sb);
        return sb.toString();
    }

    // This method uses and adapts code from here: https://www.geeksforgeeks.org/print-n-ary-tree-graphically/
    private static void printNTree(AbstractNode x,
                                   boolean[] flag,
                                   int depth, boolean isLast, StringBuilder sb)
    {
        // Condition when node is None
        if (x == null)
            return;

        // Loop to print the depths of the
        // current node
        for (int i = 1; i < depth; ++i) {
            sb.append("| ");
        }
        String valueToPrint = x.getPrintableValue();
        String textToPrint = x.name + (valueToPrint.length() == 0 ? "" : " : " + valueToPrint);

        if (depth != 0) {
            String symbol = x.type == NodeType.FOLDER ? "+ " : "- ";
            if (isLast) {
                sb.append(symbol)
                        .append(textToPrint)
                        .append('\n');
                flag[depth] = false;
            }
            else {
                sb.append(symbol)
                        .append(textToPrint)
                        .append('\n');
            }
        }

        int it = 0;
        if(x.type == NodeType.FOLDER){
            NodeFolder f = (NodeFolder) x;
            for (AbstractNode i : f.children) {
                ++it;
                // Recursive call for the
                // children nodes
                printNTree(i, flag, depth + 1,
                        it == (f.children.size()) - 1, sb);
            }
        }
        flag[depth] = true;
    }
}
