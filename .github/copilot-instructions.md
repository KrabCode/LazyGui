# LazyGui - Copilot Coding Agent Instructions

## Repository Overview

LazyGui is a GUI library for Processing (Java-based creative coding framework). It provides a feature-rich, visually minimalist GUI with lazy initialization of control elements - you don't register controls in `setup()`, you just ask for their values in `draw()` at unique string paths.

**Repository Stats:**
- Size: ~2.1MB
- Language: Java (79 source files)
- Build System: Gradle 8.0
- Java Compatibility: Java 8+ (sourceCompatibility 1.8)
- Main Dependencies: Processing 3.3.7, Gson 2.8.9
- No automated tests exist in this repository

## Build and Validation Steps

### Prerequisites
- Java 8 or later (tested with Java 17)
- Gradle 8.0 (included via wrapper)
- 7z (for deployment only)

### Build Commands (Ordered by Frequency of Use)

**IMPORTANT:** Always make gradlew executable before first use:
```bash
chmod +x gradlew
```

**1. Standard Build (fast, ~1-2 seconds)**
```bash
./gradlew build
```
- Compiles Java sources
- Creates jar in `build/libs/LazyGui.jar` (~186KB)
- No tests run (repository has no test suite)
- Safe to run anytime

**2. Clean Build (when in doubt)**
```bash
./gradlew clean build
```
- Removes `build/` directory
- Full recompile from scratch
- Use when build artifacts seem stale

**3. Shadow JAR (for releases)**
```bash
./gradlew shadowJar
```
- Creates fat jar with bundled Gson dependency: `build/libs/LazyGui-with-gson.jar` (~670KB)
- Excludes Processing (users provide their own Processing installation)
- Required before running deploy.sh

**4. Deployment (maintainer only)**
```bash
chmod +x deploy.sh
./gradlew shadowJar && ./deploy.sh
```
- Packages library for Processing IDE distribution
- Creates .jar, .txt, .zip in `build/deploy/`
- Requires shadowJar to be run first
- Deletes `data/gui/` (test saves) before packaging

### Common Build Issues and Solutions

**Issue: Permission denied on gradlew**
- Solution: Run `chmod +x gradlew` before executing gradle commands

**Issue: Permission denied on deploy.sh**
- Solution: Run `chmod +x deploy.sh` before executing

**Issue: Build artifacts from old code**
- Solution: Run `./gradlew clean` before building

**No automated CI/CD:** This repository has no GitHub Actions workflows or automated testing. Manual validation is required.

## Project Architecture and Layout

### Source Structure
```
src/main/java/com/krab/lazy/
├── LazyGui.java                    # Main API class (1435 lines)
├── LazyGuiSettings.java            # Configuration builder
├── Input.java                      # Keyboard input utility
├── ShaderReloader.java             # Live shader reload utility
├── PickerColor.java                # Color picker return type
├── ColorPoint.java                 # Gradient color point
├── KeyState.java                   # Input state tracking
├── examples/                       # Processing IDE examples (.pde files)
│   ├── BasicExample/
│   ├── GeneralOverview/
│   ├── GradientColorAt/
│   ├── IndexedPaths/
│   ├── InputWatcher/
│   ├── MouseDrawing/
│   ├── OptionalSetup/
│   ├── PathHiding/
│   ├── PeasyCamExample/
│   ├── ShaderReloading/
│   ├── TexturedTriangleFan/
│   └── UtilityMethods/
├── examples_intellij/              # IntelliJ/IDE examples (.java files)
│   ├── Controls.java
│   ├── ExampleSketch.java
│   ├── Gradient.java
│   ├── GradientSimple.java
│   ├── ReadmeVisualsGenerator.java
│   ├── SimpleShape.java
│   └── ... (14 total)
├── nodes/                          # GUI control element implementations
│   ├── AbstractNode.java
│   ├── ButtonNode.java
│   ├── ColorPickerFolderNode.java
│   ├── FolderNode.java
│   ├── SliderNode.java
│   └── ... (~30 node types)
├── stores/                         # Data persistence
├── themes/                         # GUI theming
├── utils/                          # Utility classes
├── windows/                        # Window management
└── input/                          # Input handling internals
```

### Key Configuration Files
- `build.gradle` - Build configuration, dependencies, shadowJar setup
- `settings.gradle` - Basic Gradle project settings
- `library.properties` - Processing library metadata (version, description, etc.)
- `.gitignore` - Excludes: `.idea/`, `build/`, `data/gui/`, `.gradle/`

### Resource Files
- `data/shaders/` - GLSL shaders for GUI rendering and gradients
  - `gradient.glsl` - Main gradient rendering shader
  - `sliderBackground.glsl`, `sliderBackgroundColor.glsl` - Slider visuals
  - `checkerboard.glsl`, `guideGridPoints.glsl`, `testShader.glsl`
- `data/JetBrainsMono-Regular.ttf` - Default GUI font
- `docs/` - Generated Javadoc (regenerated from IntelliJ, published to GitHub Pages)

### Public API (only ~6 classes)
The public-facing API that users interact with:
1. `LazyGui.java` - Main GUI class with all control methods
2. `LazyGuiSettings.java` - Constructor settings builder
3. `Input.java` - Keyboard input utility
4. `ShaderReloader.java` - Shader hot-reload utility
5. `PickerColor.java` - Color picker return value
6. `ColorPoint.java` - Gradient color point

### Runtime Behavior
- GUI saves are stored in `data/gui/` during development (excluded from git)
- Autosave on graceful exit (ESC key)
- Saves are JSON files using Gson with @Expose annotations
- Examples can be run directly from IntelliJ (have `main()` methods)
- Processing IDE examples are .pde files (not directly runnable from Gradle)

## Code Conventions

### File Organization
- Processing examples: `.pde` files in `examples/` subdirectories
- IntelliJ examples: `.java` files in `examples_intellij/` 
- Main library: All in `com.krab.lazy` package
- Internal implementation in subpackages: nodes, stores, themes, utils, windows

### Code Style
- Java 8 compatibility (no newer language features)
- No formal linter or formatter configured
- UTF-8 encoding for all Java files
- Follow existing code patterns when making changes

### Key Design Patterns
- Lazy initialization: GUI elements created on first call
- Path-based hierarchy: Forward slash `/` creates folders, escape with `\\/`
- Fluent settings builder: `LazyGuiSettings` uses method chaining
- Gson serialization: Uses `@Expose` annotations for save/load

## Making Changes

### Workflow for Contributors
1. Fork repository
2. Create branch based on `develop` (NOT `master`)
3. Make changes and test locally
4. Submit pull request to `develop` branch

### Testing Your Changes
Since there are no automated tests:
1. Build with `./gradlew clean build` to verify compilation
2. Run examples from `examples_intellij/` to manually test GUI changes
3. Verify examples still work correctly
4. Test save/load functionality if you modified persistence

### Adding New Examples
- For Processing IDE: Add .pde file in `examples/<ExampleName>/`
- For IntelliJ: Add .java file in `examples_intellij/`
- Include `public static void main()` in IntelliJ examples
- Test by running directly (no Gradle run task exists)

### Modifying Public API
If changing public-facing API classes:
1. Update code
2. Regenerate Javadocs (IntelliJ → Generate Javadoc)
   - Scope: Only `com.krab.lazy.*` (the ~6 public classes)
   - Output: `docs/` directory
   - Visibility: Public only
3. Add new doc pages to git (or they'll 404 on GitHub Pages)
4. Docs auto-publish to https://krabcode.github.io/LazyGui/ when pushed to `master`

### Version Updates (Maintainer Only)
1. Update `library.properties` (version, prettyVersion)
2. Run `./gradlew shadowJar`
3. Run `./deploy.sh` 
4. Upload artifacts to GitHub release at tag `latest`
5. Processing IDE pulls from `latest` tag automatically

## Important Caveats

### What NOT to Do
- Do NOT remove or modify working code unnecessarily
- Do NOT add testing frameworks (no tests exist, keep it that way unless specifically requested)
- Do NOT modify `.gitignore` to commit `build/`, `data/gui/`, or `.gradle/`
- Do NOT change Java compatibility below 1.8 (Processing 3.3.7 requirement)
- Do NOT update Processing or Gson versions without careful consideration

### Build Artifacts to Ignore
These are auto-generated and should NEVER be committed:
- `build/` directory (all build outputs)
- `data/gui/` (runtime GUI saves)
- `.gradle/` (Gradle cache)
- `*.iml` files (IntelliJ project files)
- `.idea/` (IntelliJ settings)

### Known Limitations
- No automated tests - all testing is manual
- No CI/CD pipeline - builds/releases are manual
- No code formatter or linter configured
- Examples in `examples/` are .pde files and cannot be run via Gradle
- Only examples in `examples_intellij/` can be run directly from IDE

## Development Environment

### Recommended Setup (from README)
1. Clone repository
2. Open in IntelliJ IDEA
3. "Link Gradle project" (auto-runs `gradle build` and configures source folders)
4. Edit and run examples from `examples_intellij/` to test changes
5. Create new example sketches there when adding features

### Running Examples
- IntelliJ examples: Right-click .java file → Run (they have `main()` methods)
- Processing examples: Copy to Processing IDE or use PDE contribution manager

### Debugging
- Add example sketches in `examples_intellij/` for reproducible test cases
- Use existing examples as templates
- GUI elements can be inspected by enabling options in the GUI itself

## Quick Reference

### Common File Paths
- Main library JAR: `build/libs/LazyGui.jar`
- Shadow JAR (with Gson): `build/libs/LazyGui-with-gson.jar`
- Deployment output: `build/deploy/LazyGui.*`
- Public API classes: `src/main/java/com/krab/lazy/{LazyGui,LazyGuiSettings,Input,ShaderReloader,PickerColor,ColorPoint}.java`
- GUI control nodes: `src/main/java/com/krab/lazy/nodes/`

### Build Times (for timeout estimation)
- Clean build: ~1-2 seconds
- Shadow JAR: ~1 second (if build up-to-date)
- Full clean + shadowJar: ~2-3 seconds
- Deploy script: <1 second (after shadowJar)

### Dependency Information
- Processing 3.3.7 (compile only, users provide runtime)
- Gson 2.8.9 (bundled in shadowJar)
- Compatible with Processing 3.3.7+ and Processing 4.x
- Runs on Windows, Linux, macOS (including Apple Silicon)

---

**Trust these instructions.** Only search for additional information if something here is incomplete, incorrect, or if you encounter unexpected behavior. The build process is straightforward: make gradlew executable, run `./gradlew build`, verify compilation, and manually test with examples. There are no automated tests or CI checks to worry about.
