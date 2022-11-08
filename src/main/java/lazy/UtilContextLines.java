package lazy;

import processing.core.PConstants;
import processing.core.PGraphics;

import java.util.List;

public class UtilContextLines {


    public static final List<String> contextLinesOptions = new Utils.ArrayListBuilder<String>()
            .add("never", "on hover", "always").build();
    public static final int SHOW_CONTEXT_LINES_MODE_NEVER = 0;
    public static final int SHOW_CONTEXT_LINES_MODE_ON_HOVER = 1;
    public static final int SHOW_CONTEXT_LINES_ALWAYS = 2;
    public static int showContextLinesMode = SHOW_CONTEXT_LINES_MODE_ON_HOVER;

    public static void update(String path, PGraphics pg) {
        showContextLinesMode = contextLinesOptions.indexOf(State.gui.stringPicker(path + "visibility", contextLinesOptions));
        if (showContextLinesMode == SHOW_CONTEXT_LINES_MODE_NEVER) {
            return;
        }
        pg.pushStyle();
        pg.stroke(State.gui.colorPicker(path + "color", State.normalizedColorProvider.color(0.5f)).hex);
        String strokeCapName = State.gui.stringPicker(path + "cap", new String[]{"round", "square", "project"});
        int strokeCap = strokeCapName.equals("square") ? PConstants.SQUARE :
                        strokeCapName.equals("round") ? PConstants.ROUND :
                                PConstants.PROJECT;
        pg.strokeCap(strokeCap);
        pg.strokeWeight(State.gui.slider(path + "weight", 0));
        List<AbstractNode> allNodes = NodeTree.getAllNodesAsList();
        for (AbstractNode node : allNodes) {
            if (node.type != NodeType.FOLDER) {
                continue;
            }
            FolderNode folderNode = (FolderNode) node;
            if (folderNode.window == null) {
                continue;
            }
            boolean shouldShowLineFromTitleTowardsInlineNode =
                    (!folderNode.window.closed && showContextLinesMode == SHOW_CONTEXT_LINES_ALWAYS) ||
                    (folderNode.window.isTitleHighligted() && showContextLinesMode == SHOW_CONTEXT_LINES_MODE_ON_HOVER);
            if (shouldShowLineFromTitleTowardsInlineNode) {
                folderNode.window.drawConnectingLineFromTitleBarToInlineNode(pg);
            }
        }
        pg.popStyle();
    }
}
