package toolbox;

import processing.core.PGraphics;
import processing.opengl.PShader;
import toolbox.global.State;

import java.io.File;
import java.util.ArrayList;

import static java.lang.Math.max;
import static java.lang.System.currentTimeMillis;
import static processing.core.PApplet.println;

public class ShaderReloader {
    private static final ArrayList<ShaderSnapshot> snapshots = new ArrayList<>();
    private static final int shaderRefreshRateInMillis = 36;

    private ShaderReloader() {

    }

    public static PShader getShader(String fragPath) {
        ShaderSnapshot snapshot = findSnapshotByPath(fragPath, null);
        snapshot = initIfNull(snapshot, fragPath, null);
        return snapshot.compiledShader;
    }

    public static PShader getShader(String fragPath, String vertPath) {
        ShaderSnapshot snapshot = findSnapshotByPath(fragPath, vertPath);
        snapshot = initIfNull(snapshot, fragPath, vertPath);
        return snapshot.compiledShader;
    }

    public static void filter(String fragPath) {
        shader(fragPath, null, true, State.app.g);
    }

    public static void filter(String path, PGraphics pg) {
        shader(path, null, true, pg);
    }

    public static void filter(String fragPath, String vertPath, PGraphics pg) {
        shader(fragPath, vertPath, true, pg);
    }

    public static void shader(String fragPath, String vertPath, PGraphics canvas) {
        shader(fragPath, vertPath, false, canvas);
    }

    public static void shader(String fragPath, String vertPath) {
        shader(fragPath, vertPath, false, State.app.g);
    }

    public static void shader(String fragPath, PGraphics canvas) {
        shader(fragPath, null, false, canvas);
    }

    public static void shader(String fragPath) {
        shader(fragPath, null, false, State.app.g);
    }

    private static void shader(String fragPath, String vertPath, boolean filter, PGraphics canvas) {
        ShaderSnapshot snapshot = findSnapshotByPath(fragPath, vertPath);
        snapshot = initIfNull(snapshot, fragPath, vertPath);
        snapshot.update(filter, canvas);
    }

    private static ShaderSnapshot initIfNull(ShaderSnapshot snapshot, String fragPath, String vertPath) {
        if (snapshot == null) {
            snapshot = new ShaderSnapshot(fragPath, vertPath);
            snapshots.add(snapshot);
        }
        return snapshot;
    }

    private static ShaderSnapshot findSnapshotByPath(String fragPathQuery, String vertPathQuery) {
        for (ShaderSnapshot snapshot : snapshots) {
            if (vertPathQuery != null) {
                if (snapshot.vertPath.equals(vertPathQuery) && snapshot.fragPath.equals(fragPathQuery)) {
                    return snapshot;
                }
            } else {
                if (snapshot.fragPath.equals(fragPathQuery)) {
                    return snapshot;
                }
            }
        }
        return null;
    }

    private static class ShaderSnapshot {
        String fragPath;
        String vertPath;
        File fragFile;
        File vertFile;
        PShader compiledShader;
        long fragLastKnownModified, vertLastKnownModified, lastChecked;
        boolean compiledOk = false;
        long lastKnownUncompilable = -shaderRefreshRateInMillis;

        ShaderSnapshot(String fragPath, String vertPath) {
            if (vertPath != null) {
                compiledShader = State.app.loadShader(fragPath, vertPath);
                vertFile = State.app.dataFile(vertPath);
                vertLastKnownModified = vertFile.lastModified();
                if (!vertFile.isFile()) {
                    println("Could not find shader at " + vertFile.getPath());
                }
            } else {
                compiledShader = State.app.loadShader(fragPath);
            }
            fragFile = State.app.dataFile(fragPath);
            if (!fragFile.isFile()) {
                println("Could not find shader at " + fragFile.getPath());
            }
            fragLastKnownModified = fragFile.lastModified();
            this.fragPath = fragPath;
            this.vertPath = vertPath;
            tryCompileNewVersion(currentTimeMillis());
            lastChecked = currentTimeMillis();
        }

        void update(boolean shaderMode, PGraphics pg) {
            long currentTimeMillis = currentTimeMillis();
            long lastModified = fragFile.lastModified();
            if (vertFile != null) {
                lastModified = max(lastModified, vertFile.lastModified());
            }
            if (compiledOk && currentTimeMillis < lastChecked + shaderRefreshRateInMillis) {
//                println("working shader did not change, not checking, standard apply");
                applyShader(compiledShader, shaderMode, pg);
                return;
            }
            if (!compiledOk && lastModified > lastKnownUncompilable) {
//                println("file changed, trying to compile");
                tryCompileNewVersion(lastModified);
                return;
            }
            lastChecked = currentTimeMillis;
            if (lastModified > fragLastKnownModified && lastModified > lastKnownUncompilable) {
//                println("file changed, repeat try");
                tryCompileNewVersion(lastModified);
            } else if (compiledOk) {
//                println("file didn't change, standard apply");
                applyShader(compiledShader, shaderMode, pg);
            }
        }

        private void applyShader(PShader shader, boolean shaderMode, PGraphics pg) {
            if (shaderMode) {
                pg.filter(shader);
            } else {
                pg.shader(shader);
            }
        }

        private void tryCompileNewVersion(long lastModified) {
            try {
                PShader candidate;
                if (vertFile == null) {
                    candidate = State.app.loadShader(fragPath);
                } else {
                    candidate = State.app.loadShader(fragPath, vertPath);
                }
                candidate.init();
                compiledShader = candidate;
                compiledOk = true;
                fragLastKnownModified = lastModified;
                println("Compiled", fragPath != null ? fragFile.getName() : "",
                        vertPath != null ? vertFile.getName() : "");
            } catch (Exception ex) {
                lastKnownUncompilable = lastModified;
                println((fragPath != null ? " " + fragFile.getName() : ""),
                        (vertPath != null ? " or " + (vertFile != null ? vertFile.getName() : null) : "") + ":");
                println(ex.getMessage());
            }
        }

    }
}