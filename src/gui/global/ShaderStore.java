package gui.global;

import processing.core.PGraphics;
import processing.opengl.PShader;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

import static java.lang.Math.max;
import static java.lang.System.currentTimeMillis;
import static processing.core.PApplet.println;

public class ShaderStore {
    private static final ArrayList<ShaderSnapshot> snapshots = new ArrayList<>();
    private static final int shaderRefreshRateInMillis = 36;

    private ShaderStore() {

    }

    public static PShader lazyInitGetShader(String fragPath) {
        ShaderSnapshot snapshot = findSnapshotByPath(fragPath);
        snapshot = initIfNull(snapshot, fragPath, null);
        return snapshot.compiledShader;
    }

    public static PShader lazyInitGetShader(String fragPath, String vertPath) {
        ShaderSnapshot snapshot = findSnapshotByPath(fragPath);
        snapshot = initIfNull(snapshot, fragPath, vertPath);
        return snapshot.compiledShader;
    }

    public static  void hotFilter(String path, PGraphics canvas) {
        hotShader(path, null, true, canvas);
    }

    public static  void hotFilter(String path) {
        hotShader(path, null, true, State.app.g);
    }

    public static void hotShader(String fragPath, String vertPath, PGraphics canvas) {
        hotShader(fragPath, vertPath, false, canvas);
    }

    public static void hotShader(String fragPath, String vertPath) {
        hotShader(fragPath, vertPath, false, State.app.g);
    }

    public static void hotShader(String fragPath, PGraphics canvas) {
        hotShader(fragPath, null, false, canvas);
    }

    public static void hotShader(String fragPath) {
        hotShader(fragPath, null, false, State.app.g);
    }

    private static void hotShader(String fragPath, String vertPath, boolean filter, PGraphics canvas) {
        ShaderSnapshot snapshot = findSnapshotByPath(fragPath);
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

    private static ShaderSnapshot findSnapshotByPath(String localPath) {
        for (ShaderSnapshot snapshot : snapshots) {
            if (snapshot.fragPath.equals(localPath)) {
                return snapshot;
            }
        }
        return null;
    }


    protected static class ShaderSnapshot {
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
                compiledShader = State.app.loadShader(getFullPath(fragPath), getFullPath(vertPath));
                vertFile = State.app.dataFile(getFullPath(vertPath));
                vertLastKnownModified = vertFile.lastModified();
                if (!vertFile.isFile()) {
                    println("Could not find shader at " + vertFile.getPath());
                }
            } else {
                compiledShader = State.app.loadShader(getFullPath(fragPath));
            }
            fragFile = State.app.dataFile(getFullPath(fragPath));
            if (!fragFile.isFile()) {
                println("Could not find shader at " + fragFile.getPath());
            }
            fragLastKnownModified = fragFile.lastModified();
            this.fragPath = fragPath;
            this.vertPath = vertPath;
            tryCompileNewVersion(currentTimeMillis());
            lastChecked = currentTimeMillis();
        }

        @SuppressWarnings("UnnecessaryLocalVariable")
        private String getFullPath(String fragPath) {
            String path = Paths.get(Utils.getLibraryPath(), "shaders", fragPath).toString();
//            println(path);
            return path;
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
                    candidate = State.app.loadShader(getFullPath(fragPath));
                } else {
                    candidate = State.app.loadShader(getFullPath(fragPath), getFullPath(vertPath));
                }
                candidate.init();
                compiledShader = candidate;
                compiledOk = true;
                fragLastKnownModified = lastModified;
                println("compiled", fragPath != null ? fragFile.getName() : "",
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
