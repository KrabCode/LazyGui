package com.krab.lazy;

import com.krab.lazy.stores.FontStore;
import com.krab.lazy.stores.JsonSaveStore;
import com.krab.lazy.stores.LayoutStore;
import com.krab.lazy.utils.MouseHiding;
import com.krab.lazy.themes.Theme;
import com.krab.lazy.themes.ThemeStore;
import com.krab.lazy.themes.ThemeType;

import static com.krab.lazy.stores.GlobalReferences.gui;

/**
 * Settings to apply once inside the main LazyGui constructor on startup before loading any saves that might overwrite them.
 * To avoid these settings getting overwritten - disable loading the latest save in the settings.
 * Meant to be used like this in setup():
 * <pre>
 *      gui = new LazyGui(this, new LazyGuiSettings()
 *          .setLoadLatestSaveOnStartup(false)
 *          .setAutosaveOnExit(false)
 *          // ...
 *      );
 * </pre>
 */
@SuppressWarnings("unused")
public class LazyGuiSettings {
    private boolean loadLatestSaveOnStartup;
    private boolean autosaveOnExitEnabled;
    private boolean autosaveLockGuardEnabled;
    private boolean mouseShouldHideWhenDragging;
    private boolean mouseShouldConfineToWindow;
    private boolean autosuggestWindowWidth;
    private long autosaveLockGuardMillisLimit;
    private float cellSize;
    private int mainFontSize, sideFontSize;
    private boolean startWithGuiHidden = false;
    private boolean hideBuiltInFolders = false;
    private boolean hideRadioValue = false;
    private Theme themeCustom = null;
    private ThemeType themePreset = null;
    private String pathToSpecificSaveToLoadOnStartup = null;
    private String pathToSpecificSaveToLoadOnStartupOnce = null;
    private String sketchNameOverride = null;
    private int smoothingValue;

    /**
     * Constructor, call this before any other function here.
     * Meant to be used like this in setup():
     * * <pre>
     *  *      gui = new LazyGui(this, new LazyGuiSettings()
     *  *          .setLoadLatestSaveOnStartup(false)
     *  *          .setAutosaveOnExit(false)
     *  *          // ...
     *  *      );
     *  * </pre>
     */
    public LazyGuiSettings() {
        initializeDefaultsFromGlobalConstants();
    }

    void initializeDefaultsFromGlobalConstants() {
        this.autosaveOnExitEnabled = JsonSaveStore.autosaveOnExitEnabled;
        this.loadLatestSaveOnStartup = JsonSaveStore.shouldLoadLatestSaveOnStartupByDefault;
        this.autosaveLockGuardEnabled = JsonSaveStore.autosaveLockGuardEnabled;
        this.autosaveLockGuardMillisLimit = JsonSaveStore.autosaveLockGuardMillisLimit;
        this.mouseShouldHideWhenDragging = MouseHiding.shouldHideWhenDragging;
        this.mouseShouldConfineToWindow = MouseHiding.shouldConfineToWindow;
        this.cellSize = LayoutStore.cell;
        this.startWithGuiHidden = LayoutStore.isGuiHidden();
        this.smoothingValue = LayoutStore.getSmoothingValue();
        this.autosuggestWindowWidth = LayoutStore.getAutosuggestWindowWidth();
        this.mainFontSize = FontStore.mainFontSizeDefault;
        this.sideFontSize = FontStore.sideFontSizeDefault;
    }

    void applyEarlyStartupSettings() {
        JsonSaveStore.autosaveOnExitEnabled = autosaveOnExitEnabled;
        JsonSaveStore.autosaveLockGuardEnabled = autosaveLockGuardEnabled;
        JsonSaveStore.autosaveLockGuardMillisLimit = autosaveLockGuardMillisLimit;
        MouseHiding.shouldHideWhenDragging = mouseShouldHideWhenDragging;
        MouseHiding.shouldConfineToWindow = mouseShouldConfineToWindow;
        LayoutStore.cell = cellSize;
        LayoutStore.setAutosuggestWindowWidth(autosuggestWindowWidth);
        LayoutStore.setSmoothingValue(smoothingValue);
        FontStore.mainFontSizeDefault = mainFontSize;
        FontStore.sideFontSizeDefault = sideFontSize;
        if (themeCustom != null) {
            ThemeStore.setCustomPaletteAndMakeDefaultBeforeInit(themeCustom);
        } else if (themePreset != null) {
            ThemeStore.selectThemeByTypeBeforeInit(themePreset);
        }
        LayoutStore.setIsGuiHidden(startWithGuiHidden);
        LayoutStore.setHideRadioValue(hideRadioValue);
        if(sketchNameOverride != null){
            LayoutStore.setOverridingSketchName(sketchNameOverride);
        }
    }

    void applyLateStartupSettings() {
        if(hideBuiltInFolders){
            gui.hide(gui.optionsFolderName);
            gui.hide(gui.savesFolderName);
        }
    }

    /**
     * Sets one theme from the preset options on startup.
     *
     * @param themePreset selected preset, one of "dark", "light", "pink", "blue"
     * @return this settings object for chaining statements easily
     */
    public LazyGuiSettings setThemePreset(String themePreset) {
        ThemeType foundTheme = ThemeType.getValue(themePreset);
        if (foundTheme != null) {
            this.themePreset = foundTheme;
        }
        return this;
    }

    /**
     * Sets a custom theme defined by individual hex colors.
     *
     * @param windowBorderColor     color of the window border
     * @param normalBackgroundColor normal background color
     * @param focusBackgroundColor  focus background color
     * @param normalForegroundColor normal foreground color
     * @param focusForegroundColor  focus foreground color
     * @return this settings object for chaining statements easily
     */
    public LazyGuiSettings setThemeCustom(int windowBorderColor, int normalBackgroundColor, int focusBackgroundColor, int normalForegroundColor, int focusForegroundColor) {
        this.themeCustom = new Theme(windowBorderColor, normalBackgroundColor, focusBackgroundColor, normalForegroundColor, focusForegroundColor);
        return this;
    }


    /**
     * Should the built-in folders start hidden?
     * That is the automatically created gui folders like "options" and "saves".
     * They will still exist and will try to load their values from json
     *  (unless overriden with other constructor settings)
     * and the gui will still use their current values, they will just hide from the user.
     * You can reveal them again after initialization with <code>gui.show("options")</code> and <code>gui.show("saves")</code>
     *
     * @param shouldHideFolders whether the built in folders should start hidden
     * @return this settings object for chaining statements easily
     * @see #setLoadLatestSaveOnStartup(boolean)
     * @see #setAutosaveOnExit(boolean)
     *
     */
    public LazyGuiSettings setHideBuiltInFolders(boolean shouldHideFolders) {
        this.hideBuiltInFolders = shouldHideFolders;
        return this;
    }

    /**
     * Should the gui start hidden? Toggle hiding with the 'H' hotkey.
     *
     * @param shouldHideGui whether the gui should start hidden
     * @return this settings object for chaining statements easily
     */
    public LazyGuiSettings setStartGuiHidden(boolean shouldHideGui) {
        this.startWithGuiHidden = shouldHideGui;
        return this;
    }

    /**
     * When this is set to false it disables the default attempt to load the latest save.
     *
     * @param loadLatestSaveOnStartup load the last modified save when gui starts?
     * @return this settings object for chaining statements easily
     */
    public LazyGuiSettings setLoadLatestSaveOnStartup(boolean loadLatestSaveOnStartup) {
        this.loadLatestSaveOnStartup = loadLatestSaveOnStartup;
        return this;
    }

    /**
     * Loads a specific save file on startup, tries to look inside the save folder for the file first before assuming the user gave the absolute path.
     * Also disables loading the latest save on startup.
     *
     * @param fileName name of the save file inside the save folder to load in the format "1" or "1.json" or a full absolute path to it anywhere on disk
     * @return this settings object for chaining statements easily
     * @see #setLoadLatestSaveOnStartup(boolean)
     * @see #setLoadSpecificSaveOnStartupOnce(String)
     */
    public LazyGuiSettings setLoadSpecificSaveOnStartup(String fileName) {
        this.pathToSpecificSaveToLoadOnStartup = fileName;
        return this;
    }

    /**
     * Loads a specific save file on startup if the gui finds its save folder empty.
     * Can be useful for fine-tuning global initial settings for all your gui sketches.
     * Does not disable loading latest save on startup.
     *
     * @param fileName name of the save file to load in the format "1" or "1.json" in the save folder or a full absolute path to it anywhere on disk
     * @return this settings object for chaining statements easily
     * @see #setLoadLatestSaveOnStartup(boolean)
     * @see #setLoadSpecificSaveOnStartup(String)
     */
    public LazyGuiSettings setLoadSpecificSaveOnStartupOnce(String fileName) {
        this.pathToSpecificSaveToLoadOnStartupOnce = fileName;
        return this;
    }

    /**
     * Should the GUI try to autosave its state before closing gracefully?
     *
     * @param autosaveEnabled should the sketch try to save when closing
     * @return this settings object for chaining statements easily
     */
    public LazyGuiSettings setAutosaveOnExit(boolean autosaveEnabled) {
        this.autosaveOnExitEnabled = autosaveEnabled;
        return this;
    }

    /**
     * When the lock guard is enabled it checks whether the last frame took too long and does not autosave if it did.
     * Autosaving isn't always a good idea - this protects the user against cases when the sketch gets stuck in an endless loop that may have been caused by selecting some dangerous gui values.
     *
     * @param autosaveLockGuardEnabled should the autosave be guarded against saving bad
     * @return this settings object for chaining statements easily
     * @see #setAutosaveLockGuardMillisLimit(int)
     */
    public LazyGuiSettings setAutosaveLockGuardEnabled(boolean autosaveLockGuardEnabled) {
        this.autosaveLockGuardEnabled = autosaveLockGuardEnabled;
        return this;
    }

    /**
     * The millis limit for the last frame for the autosave lock guard to take effect and block autosaving.
     * Only has an effect when autosave lock guard is enabled.
     *
     * @param autosaveLockGuardMillisLimit last frame limit in millis
     * @return this settings object for chaining statements easily
     * @see #setAutosaveLockGuardEnabled(boolean)
     */
    public LazyGuiSettings setAutosaveLockGuardMillisLimit(int autosaveLockGuardMillisLimit) {
        this.autosaveLockGuardMillisLimit = autosaveLockGuardMillisLimit;
        return this;
    }

    /**
     * Should the mouse be hidden when dragging a slider or a plot?
     * Hiding the mouse can give the user a more immersive feeling, but it can also be disorienting.
     * The mouse can still hit the corners of your screen when hidden.
     * On mouse released - the hidden mouse position resets to where the dragging started.
     *
     * @param mouseShouldHideWhenDragging should the mouse hide when dragging an element like a slider?
     * @return this settings object for chaining statements easily
     */
    public LazyGuiSettings setMouseHideWhenDragging(boolean mouseShouldHideWhenDragging) {
        this.mouseShouldHideWhenDragging = mouseShouldHideWhenDragging;
        return this;
    }

    /** Should the mouse be locked inside the sketch window? You can still exit the sketch with ESC.
     *
     * @param mouseShouldConfineToWindow confine mouse to sketch window
     * @return this settings object for chaining statements easily
     */
    public LazyGuiSettings setMouseConfineToWindow(boolean mouseShouldConfineToWindow) {
        this.mouseShouldConfineToWindow = mouseShouldConfineToWindow;
        return this;
    }

    /**
     * This sets the cell size that all gui controls use to draw themselves.
     * Also sets the distance between guide grid dots in the background.
     *
     * @param cellSize global cell size
     * @return this settings object for chaining statements easily
     */
    public LazyGuiSettings setCellSize(float cellSize) {
        this.cellSize = cellSize;
        return this;
    }

    /**
     * This sets the main font size used everywhere by the gui.
     * If you'd like to also change the text color, use a custom theme - text falls under foreground color there.
     *
     * @param mainFontSize main font size
     * @return this settings object for chaining statements easily
     * @see #setThemeCustom(int, int, int, int, int)
     */
    public LazyGuiSettings setMainFontSize(int mainFontSize) {
        this.mainFontSize = mainFontSize;
        return this;
    }

    /**
     * This sets the usually smaller side font size used in a few places by the gui.
     * If you'd like to also change the text color, use a custom theme - text falls under foreground color there.
     *
     * @param sideFontSize side font size
     * @return this settings object for chaining statements easily
     * @see #setThemeCustom(int, int, int, int, int)
     */
    public LazyGuiSettings setSideFontSize(int sideFontSize) {
        this.sideFontSize = sideFontSize;
        return this;
    }

    /**
     * Overrides what the root window title displays. By default this is the name of your sketch.
     *
     * @param sketchNameOverride name to display in root window title
     * @return this settings object for chaining statements easily
     */
    public LazyGuiSettings setSketchNameOverride(String sketchNameOverride) {
        this.sketchNameOverride = sketchNameOverride;
        return this;
    }

    /**
     * Override the GUI trying to make window widths fit its contents snugly based on longest text in the row at window opening time.
     * Setting this to false disables this behavior and sets all windows to some default size fitting for the folder type.
     *
     * @param shouldAutosuggest should the windows try to auto-detect optimal width?
     * @return this settings object for chaining statements easily
     */
    public LazyGuiSettings setAutosuggestWindowWidth(boolean shouldAutosuggest){
        this.autosuggestWindowWidth = shouldAutosuggest;
        return this;
    }

    /**
     * Override the default antialiasing level of `smooth(4)` for the GUI canvas.
     * A value of 0 will set `noSmooth()` instead.
     * This will not affect the smoothing level of your entire sketch - it will only affect the GUI, which will still be drawn as an image on top of your potentially differently smoothed sketch.
     *
     * @param smoothValue the value to be passed to `smooth()` for the gui canvas - if 0 then `noSmooth()` is used
     * @return this settings object for chaining statements easily
     * @see <a href="https://processing.org/reference/smooth_.html">smooth()</a>
     * @see <a href="https://github.com/processing/processing4/issues/694">a value of 8 breaks PGraphics on some machines</a>
     */
    public LazyGuiSettings setSmooth(int smoothValue){
        this.smoothingValue = smoothValue;
        return this;
    }

    /**
     * Hide the selected value text on the right of the radio row and replace it with a generic folder icon.
     * This can be useful when you want to use radio buttons with longer string values.
     * The option strings will still be visible once you open the radio folder.
     * This applies globally to all radio buttons and the value text is visible by default.
     * @return this settings object for chaining statements easily
     */
    public LazyGuiSettings setHideRadioValue(){
        this.hideRadioValue = true;
        return this;
    }

    boolean getShouldLoadLatestSaveOnStartup() {
        return loadLatestSaveOnStartup;
    }

    String getSpecificSaveToLoadOnStartup() {
        return pathToSpecificSaveToLoadOnStartup;
    }

    String getSpecificSaveToLoadOnStartupOnce() {
        return pathToSpecificSaveToLoadOnStartupOnce;
    }

}
