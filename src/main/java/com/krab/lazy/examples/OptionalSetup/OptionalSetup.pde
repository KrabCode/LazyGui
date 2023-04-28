import com.krab.lazy.*;

LazyGui gui;

void setup() {
  size(800, 800, P2D);
  gui = new LazyGui(this, new LazyGuiSettings()
      // AUTOLOAD
      .setLoadLatestSaveOnStartup(false) // set as false to not load anything on startup
      .setLoadSpecificSaveOnStartup("1") // expects filenames like "1" or "auto.json", overrides 'load latest'

      // AUTOSAVE
      .setAutosaveOnExit(true)    // but the shutdown hook only works on graceful exit, for example the ESC button
      .setAutosaveLockGuardEnabled(true) // for not autosaving settings that locked the sketch in an endless loop
      .setAutosaveLockGuardMillisLimit(1000) // millis the last frame must be rendered faster than for autosave to work

      // MOUSE
      .setMouseHideWhenDragging(true) // when dragging a slider for example
      .setMouseConfineToWindow(false)

      // LAYOUT
      .setCellSize(22) // affects the size of the whole gui
      .setMainFontSize(16)
      .setSideFontSize(15)
      .setStartGuiHidden(false) // uncover hidden gui with the 'h' hotkey

      // THEME
      .setThemePreset("dark") // selected preset, one of "dark", "light", "pink", "blue"
      .setThemeCustom(
              color(0, 0, 255),   // window border color
              color(16),       // normal background color
              color(0, 0, 0),      // focused background color
              color(200),     // normal foreground color
              color(255))    // focused foreground color
       // custom theme overrides preset when not null
  );

}

void draw() {
  background(gui.colorPicker("background").hex);
  int number = gui.sliderInt("pick a number", 7);
  fill(255);
  textSize(64);
  text(number, 400, 500);
}