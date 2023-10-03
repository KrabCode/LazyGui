package com.krab.lazy.stores;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.krab.lazy.nodes.FolderNode;
import com.krab.lazy.nodes.NodeType;
import com.krab.lazy.nodes.AbstractNode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static processing.core.PApplet.max;
import static processing.core.PApplet.println;

public class JsonSaveStore {
    private final static String JSON_TYPE_EXTENSION = ".json";
    public static boolean autosaveOnExitEnabled = true;
    public static boolean autosaveLockGuardEnabled = true;
    public static boolean shouldLoadLatestSaveOnStartupByDefault = true;
    public static long autosaveLockGuardMillisLimit = 1000;
    private static long lastFrameMillisForLockGuard;
    private static final Map<String, JsonElement> lastLoadedStateMap = new HashMap<>();
    private static File saveDir;
    private static ArrayList<File> saveFilesSorted;
    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

    public static void registerExitHandler() {
        Runtime.getRuntime().addShutdownHook(new Thread(JsonSaveStore::createNewAutosave));
    }

    static void createNewAutosave() {
        if (!autosaveOnExitEnabled) {
            return;
        }
        if (autosaveLockGuardEnabled && isSketchStuckInEndlessLoop()) {
            println("Autosave lock guard prevented the autosave, because the last frame took more than " + autosaveLockGuardMillisLimit + " ms," +
                    " which looks like the program stopped due to an exception or reached an endless loop");
            return;
        }
        JsonSaveStore.createNewSaveInGuiFolder("auto");
    }

    public static void updateEndlessLoopDetection() {
        lastFrameMillisForLockGuard = GlobalReferences.app.millis();
    }

    public static boolean isSketchStuckInEndlessLoop() {
        long timeSinceLastFrame = GlobalReferences.app.millis() - lastFrameMillisForLockGuard;
        return timeSinceLastFrame > autosaveLockGuardMillisLimit;
    }

    private static void lazyInitSaveDir() {
        saveDir = new File(getGuiDataFolderPath("saves"));
        lazyInitDir(saveDir);
    }

    private static void lazyInitDir(File dir) {
        if (!dir.exists()) {
            boolean dirCreationResult = dir.mkdirs();
            if (!dirCreationResult) {
                println("Could not create save folder at path: " + dir.getPath());
            }
        }
    }

    public static void createSaveAtRelativeOrAbsolutePath(String ambiguousPath){
        boolean isAbsolute = Paths.get(ambiguousPath).isAbsolute();
        if(isAbsolute){
            createNewSaveAtAbsolutePath(ambiguousPath);
        }else{
            createNewSaveInGuiFolder(ambiguousPath);
        }
    }

    public static void createNextSaveInGuiFolder() {
        String nextName = getNextUnusedIntegerFileNameInFolder(saveDir);
        createNewSaveInGuiFolder(nextName);
    }

    public static void createNewSaveInGuiFolder(String fileName) {
        // save main json
        String fullSavePath = getFullFilePathWithJsonFileType(fileName);
        createNewSaveAtAbsolutePath(fullSavePath);
    }

    public static void createNewSaveAtAbsolutePath(String fileName) {
        String fileNameWithType = String.valueOf(Paths.get(appendJsonFileTypeIfNeeded(fileName)));
        overwriteFile(fileNameWithType, getTreeAsJsonString());
    }

    public static void loadLatestSave() {
        reloadSaveFolderContents();
        if (!saveFilesSorted.isEmpty()) {
            loadStateFromFile(saveFilesSorted.get(0));
        }
    }

    private static void reloadSaveFolderContents() {
        lazyInitSaveDir();
        File[] saveFiles = saveDir.listFiles();
        assert saveFiles != null;
        saveFilesSorted = new ArrayList<>(Arrays.asList(saveFiles));
        saveFilesSorted.removeIf(file -> !file.isFile() || !file.getAbsolutePath().contains(JSON_TYPE_EXTENSION));
        if (saveFilesSorted.size() == 0) {
            return;
        }
        saveFilesSorted.sort((o1, o2) -> Long.compare(o2.lastModified(), o1.lastModified()));
    }

    private static String readFile(File file) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line);
        }
        return sb.toString();
    }

    static void overwriteFile(String fullPath, String content) {
        Path path = Paths.get(fullPath);
        lazyInitDir(path.getParent().toFile());
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(fullPath, false));
            writer.write(content);
            writer.close();
            println("Created new save: " + fullPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<File> getSaveFileList() {
        reloadSaveFolderContents();
        return saveFilesSorted;
    }

    public static void loadStateFromFilePath(String filename) {
        filename = appendJsonFileTypeIfNeeded(filename);
        // first try to find the strictest match inside the save folder
        for (File saveFile : saveFilesSorted) {
            if (saveFile.getName().equals(filename) || saveFile.getName().equals(filename + JSON_TYPE_EXTENSION)) {
                loadStateFromFile(saveFile);
                return;
            }
        }

        // then relax and allow filenames like 'auto' without the '.json' suffix (still inside the save folder)
        for (File saveFile : saveFilesSorted) {
            if (saveFile.getName().startsWith(filename)) {
                loadStateFromFile(saveFile);
                return;
            }
        }

        // if all else fails this may be an absolute path anywhere on disk, we need an exact match there
        try {
            loadStateFromFile(new File(filename));
        } catch (Exception ex) {
            println(ex, ex.getMessage());
        }
    }

    static void loadStateFromFile(File file) {
        if (!file.exists()) {
            println("Error: save file doesn't exist at \"" + file.getAbsolutePath() + "\"");
            return;
        }
        String json;
        try {
            json = readFile(file);
        } catch (IOException e) {
            println("Error loading state from file: ", e.getMessage());
            return;
        }
        JsonElement root = getJsonElementFromString(json);
        loadStateFromJsonElement(root, null);
        println("Loaded gui state from: " + file.getPath());
    }


    public static String getTreeAsJsonString() {
        return gson.toJson(NodeTree.getRoot());
    }

    public static String getFolderAsJsonString(FolderNode folder) {
        return gson.toJson(folder);
    }

    public static JsonElement getJsonElementFromString(String json) {
        return gson.fromJson(json, JsonElement.class);
    }

    public static void loadStateFromJsonString(String json) {
        loadStateFromJsonString(json, NodeTree.getRoot().path);
    }

    public static void loadStateFromJsonString(String json, String path) {
        loadStateFromJsonElement(gson.fromJson(json, JsonElement.class), path);
    }

    static void loadStateFromJsonElement(JsonElement root, String outputRootPath) {
        lastLoadedStateMap.clear();
        Queue<JsonElement> queue = new LinkedList<>();
        queue.offer(root);
        try {
            String inputRootPath = root.getAsJsonObject().get("path").getAsString();
            while (!queue.isEmpty()) {
                JsonElement loadedNode = queue.poll();
                String loadedPath = loadedNode.getAsJsonObject().get("path").getAsString();
                if (outputRootPath != null) {
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
        } catch (Exception ex) {
            println("Loading gui state from json failed with: " + ex.getClass().getSimpleName());
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

    public static String getNextUnusedIntegerFileNameInFolder(File folder) {
        if (!folder.isDirectory()) {
            return null;
        }
        int maxNumberFound = 0;
        String[] existingFiles = folder.list();
        if (existingFiles != null) {
            for (String filenameWithExtension : existingFiles) {
                String filenameWithoutExtension = getFileNameWithoutTypeExtension(filenameWithExtension);
                try {
                    int filenameInteger = Integer.parseInt(filenameWithoutExtension);
                    maxNumberFound = max(filenameInteger, maxNumberFound);
                } catch (NumberFormatException nfex) {
                    // just ignore files that do not match a simple integer numbering scheme
                } catch (Exception ex) {
                    println(ex);
                }
            }
        }
        return String.valueOf(maxNumberFound + 1);
    }

    public static String getGuiDataFolderPath(String innerPath) {
        return GlobalReferences.app.dataPath(
                Paths.get("gui", GlobalReferences.app.getClass().getSimpleName(), innerPath).toString());
    }

    private static String getFullFilePathWithJsonFileType(String filename) {
        return Paths.get(saveDir.getAbsolutePath(), appendJsonFileTypeIfNeeded(filename)).toString();
    }

    private static String appendJsonFileTypeIfNeeded(String filename){
        return filename.endsWith(JSON_TYPE_EXTENSION) ?
                filename :
                filename + JSON_TYPE_EXTENSION;
    }

    static String getFileNameWithoutTypeExtension(String filenameWithTypeExtension) {
        return filenameWithTypeExtension.split("\\.")[0];
    }

    public static File getSaveDir() {
        return saveDir;
    }
}
