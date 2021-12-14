package toolbox.global;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.jogamp.newt.opengl.GLWindow;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PSurface;
import toolbox.Gui;
import toolbox.windows.nodes.*;
import toolbox.windows.nodes.colorPicker.ColorPickerFolderNode;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

import static processing.core.PApplet.*;
import static processing.core.PApplet.second;
import static processing.core.PConstants.HSB;
import static processing.core.PConstants.P2D;
import static toolbox.global.NodeTree.findNodeByPathInTree;


public class State {
    public static float cell = 24;
    public static PFont font = null;
    public static PApplet app = null;
    public static Gui gui = null;
    public static Robot robot = null;
    public static GLWindow window = null;
    public static String libraryPath = null;
    public static PGraphics colorProvider = null;
    public static float textMarginX = 5;
    public static String sketchName = null;
    public static File dir;

    public static int clipboardHex = 0;
    public static float clipboardFloat = 0;

    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
    private static ArrayList<File> saveFilesSorted;
    static Map<String, JsonElement> lastLoadedStateMap = new HashMap<>();



    public static void init(Gui gui, PApplet app){
        State.gui = gui;
        State.app = app;
        try {
            State.font = app.createFont("Calibri", 20);
        }catch(RuntimeException ex){
            if(ex.getMessage().contains("createFont() can only be used inside setup() or after setup() has been called")){
                throw new RuntimeException("the new Gui(this) constructor can only be used inside setup() or after setup() has been called");
            }

        }


        sketchName = app.getClass().getSimpleName();
        libraryPath = Utils.getLibraryPath();
        dir = new File(libraryPath + "/saves/" + sketchName);
        if(!dir.exists()){
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }

        colorProvider = app.createGraphics(256,256, P2D);
        State.colorProvider.colorMode(HSB,1,1,1,1);

        PSurface surface = State.app.getSurface();
        if (surface instanceof processing.opengl.PSurfaceJOGL) {
            window = (com.jogamp.newt.opengl.GLWindow) (surface.getNative());
        }

        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static String timestamp() {
        return year() + ""
                + nf(month(), 2) + ""
                + nf(day(), 2)+ "T"
                + nf(hour(), 2)+ ""
                + nf(minute(), 2)+ ""
                + nf(second(), 2);
    }

    public static void createTreeSaveFile(){
        String json = gson.toJson(NodeTree.mainRoot);
        BufferedWriter writer;
        String timestamp = timestamp();
        String filePath = dir.getAbsolutePath() + "/" + timestamp + ".json";
        try {
            writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveFilesSorted.add(0, new File(filePath));
    }

    public static void loadMostRecentSave(){
        File[] saveFiles = dir.listFiles();
        assert saveFiles != null;
        saveFilesSorted = new ArrayList<>(java.util.List.of(saveFiles));
        saveFilesSorted.removeIf(file -> !file.isFile());
        if(saveFilesSorted.size() == 0){
            return;
        }
        saveFilesSorted.sort((o1, o2) -> Long.compare(o2.lastModified(), o1.lastModified()));
        loadFromJson(saveFilesSorted.get(0));
    }

    public static void loadSave(String filename){
        for (File saveFile : saveFilesSorted) {
            if (saveFile.getName().equals(filename)) {
                loadFromJson(saveFile);
                return;
            }
        }
    }

    private static String readFile(File file) {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        assert lines != null;
        for(String line : lines){
            sb.append(line);
        }
        return sb.toString();
    }

    public static ArrayList<File> getSaveFileList(){
        return saveFilesSorted;
    }

    public static void loadFromJson(File jsonToLoad) {
        String json = readFile(jsonToLoad);
        // don't delete or do anything to the existing nodes, just overwrite their values
        JsonElement loadedRoot = gson.fromJson(json, JsonElement.class);
        lastLoadedStateMap.clear();
        Queue<JsonElement> queue = new LinkedList<>();
        queue.offer(loadedRoot);
        while (!queue.isEmpty()) {
            JsonElement loadedNode = queue.poll();
            String loadedPath = loadedNode.getAsJsonObject().get("path").getAsString();
            AbstractNode nodeToEdit = findNodeByPathInTree(loadedPath);
            if(nodeToEdit != null){
                lastLoadedStateMap.put(loadedPath, loadedNode);
                overwriteWithLoadedStateIfAny(nodeToEdit, loadedNode);
            }
            String loadedType = loadedNode.getAsJsonObject().get("type").getAsString();
            if (Objects.equals(loadedType, NodeType.FOLDER_ROW.toString())) {
                JsonArray loadedChildren = loadedNode.getAsJsonObject().get("children").getAsJsonArray();
                for (JsonElement child : loadedChildren) {
                    queue.offer(child);
                }
            }
        }
    }

    public static void overwriteWithLoadedStateIfAny(AbstractNode abstractNode){
        overwriteWithLoadedStateIfAny(abstractNode, lastLoadedStateMap.get(abstractNode.path));
    }

    public static void overwriteWithLoadedStateIfAny(AbstractNode abstractNode, JsonElement loadedNodeState) {
        if(loadedNodeState == null){
            return;
        }
        try{
            String className = loadedNodeState.getAsJsonObject().get("className").getAsString().toLowerCase();
            if(className.contains("sliderint")){
                ((SliderIntNode) abstractNode).valueFloat = loadedNodeState.getAsJsonObject().get("valueFloat").getAsFloat();
            }else if (className.contains("slider")){
                ((SliderNode) abstractNode).valueFloat = loadedNodeState.getAsJsonObject().get("valueFloat").getAsFloat();
            }else if(className.contains("toggle")){
                ((ToggleNode) abstractNode).valueBoolean = loadedNodeState.getAsJsonObject().get("valueBoolean").getAsBoolean();
            }else if(className.contains("colorpicker")){
                ((ColorPickerFolderNode) abstractNode).setHex(unhex(loadedNodeState.getAsJsonObject().get("hexString").getAsString()));
            }
        }catch(Exception ex){
            println("tree structure changed and old state no longer applies to new nodes, probably nothing to worry about, just save the new state to stop seeing this warning");
            println(ex.getMessage());
        }
    }
}
