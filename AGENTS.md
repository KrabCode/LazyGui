# LazyGui

GUI library for [Processing](https://processing.org) (Java). Controls are created lazily: sketches never register anything in `setup()`, they just ask for a value in `draw()` at a unique string path (`gui.slider("folder/name")`) and the library creates the control, window and folder on first use. Slash `/` in a path makes a folder, `\/` escapes it.

## Build and verify

- `./gradlew compileJava` is the quick check. `./gradlew build` also makes the jar. Java 8 source level, Gradle wrapper included.
- There are no automated tests and no CI. Verify by compiling, then run a sketch from `src/main/java/com/krab/lazy/examples_intellij/` (each has a `main()`) when the change is visual or interactive. Say so when you could not run one.
- `./gradlew shadowJar` makes `build/libs/LazyGui-with-gson.jar` (Gson bundled, Processing excluded) for the Processing IDE release. `./gradlew publishToCentral` publishes to Maven Central. Releases are the maintainer's job, see `README_DEPLOY.md` and do not do them unasked.

## Layout

`src/main/java/com/krab/lazy/`
- Public API, the only classes users touch: `LazyGui`, `LazyGuiSettings` (fluent builder), `Input`, `ShaderReloader`, `PickerColor`, `ColorPoint`.
- Internals: `nodes/` (one class per control type, all extend `AbstractNode`), `windows/` (window drawing, drag, resize, scroll), `stores/` (global state: layout, theme, save/load, undo), `themes/`, `input/`, `utils/`.
- `examples/` are Processing `.pde` sketches, `examples_intellij/` are runnable Java equivalents. Add or update both when changing user-visible behavior.
- `data/` (font and GLSL shaders) is packaged as jar resources. `docs/` is generated Javadoc.

## Conventions

- Stay on Java 8 language features and the Processing 3.3.7 API (it must also work on Processing 4).
- Match the surrounding code. There is no formatter or linter. UTF-8 everywhere.
- Layout sizes derive from `LayoutStore.cell`, colors come from `ThemeStore.getColor(...)`. Do not hardcode pixel sizes or colors in nodes and windows.
- Saves are JSON via Gson using `@Expose`. Renaming or removing an exposed field breaks users' existing saves.
- New user-facing settings usually need three places: `LayoutStore` (or the relevant store), a setter in `LazyGuiSettings` with Javadoc, and the live "options" folder in the GUI.
- Changing public API means the Javadoc in `docs/` must be regenerated (IntelliJ, maintainer step, see `README_DEPLOY.md`). Mention it instead of editing `docs/` by hand.

## Git

- Work on `develop`, not `master`. Contributor PRs target `develop`.
- Never commit `build/`, `.gradle/`, `.idea/`, `*.iml` or `data/gui/` (runtime saves).
- Do not bump `library.properties` versions unless asked.
