package toolbox.global;

import com.google.gson.*;
import toolbox.windows.nodes.FolderNode;
import toolbox.windows.nodes.AbstractNode;
import toolbox.windows.nodes.NodeType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

import static processing.core.PApplet.main;
import static processing.core.PApplet.println;

public class NodeTree {
    private static final FolderNode mainRoot = new FolderNode("", null);
    private static final HashMap<String, AbstractNode> nodesByPath = new HashMap<>();
    private static String jsonState;
    private static Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

    private NodeTree() {

    }

    public static FolderNode getMainRoot() {
        return mainRoot;
    }

    public static AbstractNode getLazyInitParentFolderByPath(String nodePath) {
        String folderPath = getPathWithoutName(nodePath);
        lazyCreateFolderPath(folderPath);
        return findNodeByPathInTree(folderPath);
    }

    public static AbstractNode findNodeByPathInTree(String path) {
        return findNodeByPathInTree(path, NodeTree.mainRoot);
    }

    public static AbstractNode findNodeByPathInTree(String path, FolderNode root) {
        if (nodesByPath.containsKey(path)) {
            return nodesByPath.get(path);
        }
        Queue<AbstractNode> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            AbstractNode node = queue.poll();
            if (node.path.equals(path)) {
                nodesByPath.put(path, node);
                return node;
            }
            if (node.type == NodeType.FOLDER_ROW) {
                FolderNode folder = (FolderNode) node;
                for (AbstractNode child : folder.children) {
                    queue.offer(child);
                }
            }
        }
        return null;
    }

    public static void lazyCreateFolderPath(String path) {
        String[] split = path.split("/");
        String runningPath = split[0];
        FolderNode parentFolder = null;
        for (int i = 0; i < split.length; i++) {
            AbstractNode n = findNodeByPathInTree(runningPath);
            if (n == null) {
                if (parentFolder == null) {
                    parentFolder = mainRoot;
                }
                n = new FolderNode(runningPath, parentFolder);
                parentFolder.children.add(n);
                parentFolder = (FolderNode) n;
            } else if (n.type == NodeType.FOLDER_ROW) {
                parentFolder = (FolderNode) n;
            } else {
                println("expected folder based on path but got value node");
            }
            if (i < split.length - 1) {
                runningPath += "/" + split[i + 1];
            }
        }
    }

    public static void insertNodeAtItsPath(AbstractNode node) {
        if (findNodeByPathInTree(node.path) != null) {
            return;
        }
        String folderPath = getPathWithoutName(node.path);
        lazyCreateFolderPath(folderPath);
        FolderNode folder = (FolderNode) findNodeByPathInTree(folderPath);
        assert folder != null;
        folder.children.add(node);
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

    public static void saveToJson() {
        String json = gson.toJson(mainRoot);
        println(json);
        jsonState = json;
    }

    public static void loadFromJson() {
        println(jsonState);
        // don't delete or do anything to the existing nodes, just overwrite their values
        JsonElement loadedRoot = gson.fromJson(jsonState, JsonElement.class);

        Queue<JsonElement> queue = new LinkedList<>();
        queue.offer(loadedRoot);
        while (!queue.isEmpty()) {
            JsonElement loadedNode = queue.poll();
            String loadedPath = loadedNode.getAsJsonObject().get("path").getAsString();
            AbstractNode mainNode = findNodeByPathInTree(loadedPath);
            mainNode.overwriteState(loadedNode);
            String loadedType = loadedNode.getAsJsonObject().get("type").getAsString();
            if (Objects.equals(loadedType, NodeType.FOLDER_ROW.toString())) {
                JsonArray loadedChildren = loadedNode.getAsJsonObject().get("children").getAsJsonArray();
                for (JsonElement child : loadedChildren) {
                    queue.offer(child);
                }
            }
        }
    }

}

