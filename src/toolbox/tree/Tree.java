package toolbox.tree;

import toolbox.tree.nodes.FolderNode;
import toolbox.tree.nodes.Node;
import toolbox.tree.nodes.NodeType;

import java.util.LinkedList;
import java.util.Queue;

import static processing.core.PApplet.println;

public class Tree {
    public String name;
    public FolderNode root = new FolderNode("", null);

    public Tree(String name) {
        this.name = name;
    }

    public Node getLazyInitParentFolderByPath(String nodePath){
        String folderPath = getPathWithoutName(nodePath);
        lazyCreateFolderPath(folderPath);
        return findNodeByPathInTree(folderPath);
    }

    public Node findNodeByPathInTree(String path) {
        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            if (node.path.equals(path)) {
                return node;
            }
            if (node.type == NodeType.FOLDER) {
                FolderNode folder = (FolderNode) node;
                for (Node child : folder.children) {
                    queue.offer(child);
                }
            }
        }
        return null;
    }

    public void lazyCreateFolderPath(String path) {
        String[] split = path.split("/");
        String runningPath = split[0];
        FolderNode parentFolder = null;
        for (int i = 0; i < split.length; i++) {
            Node n = findNodeByPathInTree(runningPath);
            if (n == null) {
                if (parentFolder == null) {
                    parentFolder = root;
                }
                n = new FolderNode(runningPath, parentFolder);
                parentFolder.children.add(n);
                parentFolder = (FolderNode) n;
            }else if (n.type == NodeType.FOLDER) {
                parentFolder = (FolderNode) n;
            }else{
                println("expected folder based on path but got value node");
            }
            if(i < split.length - 1){
                runningPath += "/" + split[i + 1];
            }
        }
    }

    public void insertNodeAtItsPath(Node node) {
        if(findNodeByPathInTree(node.path) != null){
//            return;
        }
        String folderPath = getPathWithoutName(node.path);
        lazyCreateFolderPath(folderPath);
        FolderNode folder = (FolderNode) findNodeByPathInTree(folderPath);
        folder.children.add(node);
    }

    public String getPathWithoutName(String pathWithName) {
        String[] split = pathWithName.split("/");
        StringBuilder sum = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) {
            sum.append(split[i]);
            if(i < split.length - 2){
                sum.append("/");
            }
        }
        return sum.toString();
    }
}
