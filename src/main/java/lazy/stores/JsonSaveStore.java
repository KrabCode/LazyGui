package lazy.stores;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import lazy.nodes.AbstractNode;
import lazy.nodes.FolderNode;
import lazy.nodes.NodeType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static lazy.stores.GlobalReferences.app;
import static processing.core.PApplet.max;
import static processing.core.PApplet.println;

public class JsonSaveStore {
    private static final Map<String, JsonElement> lastLoadedStateMap = new HashMap<>();
    private static File saveDir;
    private static ArrayList<File> saveFilesSorted;
    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
    public static boolean autosaveEnabled = true;
    public static boolean autosaveLockGuardEnabled = true;
    private final static String JSON_FILE_TYPE_SUFFIX = ".json";

    private static long lastFrameMillis;
    static final long lastFrameMillisStuckLimit = 1000;

    public static void registerExitHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(JsonSaveStore::createAutosave));
    }

    static void createAutosave() {
        if (!autosaveEnabled) {
            return;
        }
        if (autosaveLockGuardEnabled && isSketchStuckInEndlessLoop()) {
            println("not autosaving," +
                    " because autosave lock guard was enabled and" +
                    " the last frame took more than " + lastFrameMillisStuckLimit + " ms," +
                    " which looks like the program stopped due to an exception or reached an endless loop");
            return;
        }
        JsonSaveStore.createTreeSaveFiles("auto");
        println("auto-saved gui");
    }

    public static void updateEndlessLoopDetection() {
        lastFrameMillis = app.millis();
    }

    public static boolean isSketchStuckInEndlessLoop() {
        long timeSinceLastFrame = app.millis() - lastFrameMillis;
        return timeSinceLastFrame > lastFrameMillisStuckLimit;
    }

    private static void lazyInitSaveDir() {
        saveDir = new File(getGuiDataFolderPath("/saves/"));
        if (!saveDir.exists()) {
            boolean dirCreationResult = saveDir.mkdirs();
            if(!dirCreationResult){
                println("could not create save folder at path: " + saveDir.getPath());
            }
        }
    }

    public static void createTreeSaveFiles(String filenameWithoutSuffix) {
        // save main json
        String jsonPath = getFullJsonFilePathWithFileTypeSuffix(filenameWithoutSuffix);
        overwriteFile(jsonPath, getTreeAsJsonString());
//        println("Saved current state to: " + jsonPath);
    }

    public static void loadMostRecentSave() {
        reloadSaveFolderContents();
        if (saveFilesSorted.size() > 0) {
            loadStateFromFile(saveFilesSorted.get(0));
        }
    }


    private static void reloadSaveFolderContents() {
        lazyInitSaveDir();
        File[] saveFiles = saveDir.listFiles();
        assert saveFiles != null;
        saveFilesSorted = new ArrayList<>(Arrays.asList(saveFiles));
        saveFilesSorted.removeIf(file -> !file.isFile() || !file.getAbsolutePath().contains(".json"));
        if (saveFilesSorted.size() == 0) {
            return;
        }
        saveFilesSorted.sort((o1, o2) -> Long.compare(o2.lastModified(), o1.lastModified()));
    }

    public static void loadStateFromFile(String filename) {
        for (File saveFile : saveFilesSorted) {
            if (saveFile.getName().equals(filename)) {
                loadStateFromFile(saveFile);
                return;
            }
        }
    }

    private static String readFile(File file) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line);
        }
        return sb.toString();
    }

    private static String getFullJsonFilePathWithFileTypeSuffix(String filenameWithoutSuffix) {
        return getFullFilePathWithoutTypeSuffix(filenameWithoutSuffix + JSON_FILE_TYPE_SUFFIX);
    }

    private static String getFullFilePathWithoutTypeSuffix(String filenameWithSuffix) {
        return saveDir.getAbsolutePath() + "\\" + filenameWithSuffix;
    }

    static void overwriteFile(String fullPath, String content) {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(fullPath, false));
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<File> getSaveFileList() {
        reloadSaveFolderContents();
        return saveFilesSorted;
    }

    static void loadStateFromFile(File file) {
        if (!file.exists()) {
            println("Error: save file doesn't exist");
            return;
        }
        String json;
        try {
            json = readFile(file);
        } catch (IOException e) {
            println("Error loading state from file", e.getMessage());
            return;
        }
        JsonElement root = getJsonElementFromString(json);
        loadStateFromJsonElement(root, null);
    }


    public static String getTreeAsJsonString() {
        return gson.toJson(NodeTree.getRoot());
    }

    public static String getFolderAsJsonString(FolderNode folder){
        return gson.toJson(folder);
    }

    public static JsonElement getJsonElementFromString(String json) {
        return gson.fromJson(json, JsonElement.class);
    }

    public static void loadStateFromJsonString(String json){
        loadStateFromJsonString(json, NodeTree.getRoot().path);
    }

    public static void loadStateFromJsonString(String json, String path){
        loadStateFromJsonElement(gson.fromJson(json, JsonElement.class), path);
    }

    static void loadStateFromJsonElement(JsonElement root, String outputRootPath) {
        lastLoadedStateMap.clear();
        Queue<JsonElement> queue = new LinkedList<>();
        queue.offer(root);
        String inputRootPath = root.getAsJsonObject().get("path").getAsString();
        while (!queue.isEmpty()) {
            JsonElement loadedNode = queue.poll();
            String loadedPath = loadedNode.getAsJsonObject().get("path").getAsString();
            if(outputRootPath != null){
                // used for copy/pasting sub-folders and not the entire tree
                loadedPath = loadedPath.replace(inputRootPath, outputRootPath);
            }
            AbstractNode nodeToEdit = NodeTree.findNode(loadedPath);
            if (nodeToEdit != null) {
                overwriteWithLoadedStateIfAny(nodeToEdit, loadedNode);
            }
            lastLoadedStateMap.put(loadedPath, loadedNode);
            String loadedType = loadedNode.getAsJsonObject().get("type").getAsString();
            if (Objects.equals(loadedType, NodeType.FOLDER.toString())) {
                JsonArray loadedChildren = loadedNode.getAsJsonObject().get("children").getAsJsonArray();
                for (JsonElement child : loadedChildren) {
                    queue.offer(child);
                }
            }
        }
    }

    public static void overwriteWithLoadedStateIfAny(AbstractNode abstractNode) {
        overwriteWithLoadedStateIfAny(abstractNode, lastLoadedStateMap.get(abstractNode.path));
    }

    static void overwriteWithLoadedStateIfAny(AbstractNode abstractNode, JsonElement loadedNodeState) {
        if (loadedNodeState == null) {
            return;
        }
        abstractNode.overwriteState(loadedNodeState);
    }


    public static void createNewSave() {
        String newName = getNextUnusedIntegerFileNameInFolder(saveDir);
        createTreeSaveFiles(newName);
    }

    public static File getSaveDir(){
        return saveDir;
    }

    public static String getNextUnusedIntegerFileNameInFolder(File folder){
        if(!folder.isDirectory()){
            return null;
        }
        String[] existingFiles = folder.list();
        if(existingFiles == null || existingFiles.length == 0){
            return "1";
        }
        int highestNumber = -1;
        for(String existingFileName : existingFiles){
            String filenameWithoutSuffix = existingFileName.split("\\.")[0];
            try{
                int filenameInteger = Integer.parseInt(filenameWithoutSuffix);
                highestNumber = max(filenameInteger, highestNumber);
            } catch(NumberFormatException nfex){
                // just ignore files that do not match a simple integer numbering scheme
            } catch(Exception ex){
                println(ex);
            }
        }
        return String.valueOf(highestNumber + 1);
    }

    public static String getGuiDataFolderPath(String innerPath) {
        return app.dataPath("gui/" + app.getClass().getSimpleName() + innerPath);
    }
}
