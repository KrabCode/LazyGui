package lazy.utils;

import lazy.nodes.AbstractNode;
import lazy.nodes.FolderNode;
import lazy.nodes.NodeType;
import lazy.stores.NodeTree;
import lazy.stores.NormColorStore;
import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.List;

import static lazy.stores.GlobalReferences.gui;

public class ContextLines {
    public static final String NEVER = "never";
    public static final String ON_HOVER = "on hover";
    public static final String ALWAYS = "always";
    public static final int SHOW_CONTEXT_LINES_MODE_NEVER = 0;
    public static final int SHOW_CONTEXT_LINES_MODE_ON_HOVER = 1;
    public static final int SHOW_CONTEXT_LINES_ALWAYS = 2;
    public static final List<String> contextLinesOptions = new ArrayListBuilder<String>()
            .add(NEVER, ON_HOVER, ALWAYS).build();
    private static int showContextLinesMode;
    private static boolean shouldPickShortestLine;
    private static int lineStroke;
    private static float weight;
    private static float endpointRectSize;

    public static void updateSettings() {
        gui.pushFolder("context lines");
        showContextLinesMode = contextLinesOptions.indexOf(
                gui.radio("visibility", contextLinesOptions, ON_HOVER));
        shouldPickShortestLine = gui.toggle("shortest line");
        lineStroke = gui.colorPicker("color", NormColorStore.color(0.5f)).hex;
        weight = gui.slider("weight", 1.2f);
        endpointRectSize = gui.slider("end size", 3.5f);
        gui.popFolder();
    }

    public static void drawLines(PGraphics pg){
        pg.pushStyle();
        pg.stroke(lineStroke);
        pg.fill(lineStroke);
        pg.strokeCap(PConstants.SQUARE);
        pg.strokeWeight(weight);
        List<AbstractNode> allNodes = NodeTree.getAllNodesAsList();
        if (showContextLinesMode == SHOW_CONTEXT_LINES_MODE_NEVER) {
            pg.popStyle();
            return;
        }
        for (AbstractNode node : allNodes) {
            if (node.type != NodeType.FOLDER) {
                continue;
            }
            FolderNode folderNode = (FolderNode) node;
            if (folderNode.window == null || folderNode.window.closed || !folderNode.isInlineNodeVisible()) {
                continue;
            }
            boolean shouldShowLineFromTitleTowardsInlineNode = showContextLinesMode == SHOW_CONTEXT_LINES_ALWAYS ||
                    (folderNode.window.isTitleHighlighted() && showContextLinesMode == SHOW_CONTEXT_LINES_MODE_ON_HOVER);
            if (shouldShowLineFromTitleTowardsInlineNode) {
                folderNode.window.drawContextLineFromTitleBarToInlineNode(pg, endpointRectSize, shouldPickShortestLine);
            }
        }
        pg.popStyle();
    }
}
