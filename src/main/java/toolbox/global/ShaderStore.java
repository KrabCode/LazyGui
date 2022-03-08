package toolbox.global;

import processing.core.PGraphics;
import processing.opengl.PShader;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.max;
import static processing.core.PApplet.println;

public class ShaderStore {
    private static final Map<String, PShader> shaders = new HashMap<>();

    private ShaderStore() {

    }

    public static PShader lazyInitGetShader(String path) {
        if(!shaders.containsKey(path)) {
            shaders.put(path, State.app.loadShader(path));
        }
        return shaders.get(path);
    }

    public static  void hotFilter(String path, PGraphics pg) {
        pg.filter(shaders.get(path));
    }

    public static void hotShader(String path, PGraphics pg) {
        pg.shader(shaders.get(path));
    }
}
