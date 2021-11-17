package toolbox.tree;

import java.util.LinkedList;
import java.util.Queue;

import static processing.core.PApplet.println;

public class Tree {
    TreeFolder rootFolder = new TreeFolder("", null);

    public Tree() {
        lazyCreateFolderPath("L1/L2/L3");
        lazyCreateFolderPath("M1/M2/L3");
        insertNodeAtPath(new TreeNode("R1/R2/R3/hello", NodeType.BUTTON));
        insertNodeAtPath(new TreeNode("R1/R2/R3/hello", NodeType.BUTTON));
        println(findNodeByPathInTree("R1/R2/R3/hello"));
    }

    public TreeNode findNodeByPathInTree(String path) {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(rootFolder);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node.path.equals(path)) {
                return node;
            }
            if (node.type == NodeType.FOLDER) {
                TreeFolder folder = (TreeFolder) node;
                for (TreeNode child : folder.children) {
                    queue.offer(child);
                }
            }
        }
        return null;
    }

    public void lazyCreateFolderPath(String path) {
        String[] split = path.split("/");
        String runningPath = split[0];
        TreeFolder parentFolder = null;
        for (int i = 0; i < split.length; i++) {
            TreeNode n = findNodeByPathInTree(runningPath);
            if (n == null) {
                if (parentFolder == null) {
                    parentFolder = rootFolder;
                }
                n = new TreeFolder(runningPath, parentFolder);
                parentFolder.children.add(n);
                parentFolder = (TreeFolder) n;
            }else if (n.type == NodeType.FOLDER) {
                parentFolder = (TreeFolder) n;
            }else{
                println("wtf");
            }
            if(i < split.length - 1){
                runningPath += "/" + split[i + 1];
            }
        }
    }

    public void insertNodeAtPath(TreeNode node) {
        if(findNodeByPathInTree(node.path) != null){
            return;
        }
        String folderPath = getPathWithoutName(node.path);
        lazyCreateFolderPath(folderPath);
        TreeFolder folder = (TreeFolder) findNodeByPathInTree(folderPath);
        folder.children.add(node);
    }

    public String getPathWithoutName(String pathWithName) {
        String[] split = pathWithName.split("/");
        StringBuilder sum = new StringBuilder();
        for (int i = 0; i < split.length - 1; i++) {
            sum.append(split[i]).append("/");
        }
        if(sum.length() > 0){

            sum.delete(sum.length() - 1, sum.length());
        }
        return sum.toString();
    }
}
