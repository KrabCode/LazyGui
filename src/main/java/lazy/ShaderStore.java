package lazy;

import processing.opengl.PShader;

import java.util.HashMap;
import java.util.Map;

import static lazy.Globals.app;

class ShaderStore {
    private static final Map<String, PShader> shaders = new HashMap<>();
    private static final String shaderFolder = "shaders/";

    private ShaderStore() {

    }

    static PShader getShader(String path) {
        String fullPath = shaderFolder + path;
        if(!shaders.containsKey(fullPath)) {
            shaders.put(fullPath, app.loadShader(fullPath));
        }
        return shaders.get(fullPath);
    }
}
