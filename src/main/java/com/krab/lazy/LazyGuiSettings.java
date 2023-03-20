package com.krab.lazy;

import com.krab.lazy.stores.FontStore;
import com.krab.lazy.stores.JsonSaveStore;
import com.krab.lazy.stores.LayoutStore;
import com.krab.lazy.utils.MouseHiding;
import com.krab.lazy.themes.Theme;
import com.krab.lazy.themes.ThemeStore;
import com.krab.lazy.themes.ThemeType;

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
    private boolean startGuiHidden = false;
    private Theme themeCustom = null;
    private ThemeType themePreset = null;
    private String pathToSpecificSaveToLoadOnStartup = null;
    private String sketchNameOverride = null;

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
        this.startGuiHidden = LayoutStore.isGuiHidden();
        this.mainFontSize = FontStore.mainFontSizeDefault;
        this.sideFontSize = FontStore.sideFontSizeDefault;
        this.autosuggestWindowWidth = LayoutStore.getAutosuggestWindowWidth();
    }

    void applySettingsOntoGuiAtStartup() {
        JsonSaveStore.autosaveOnExitEnabled = autosaveOnExitEnabled;
        JsonSaveStore.autosaveLockGuardEnabled = autosaveLockGuardEnabled;
        JsonSaveStore.autosaveLockGuardMillisLimit = autosaveLockGuardMillisLimit;
        MouseHiding.shouldHideWhenDragging = mouseShouldHideWhenDragging;
        MouseHiding.shouldConfineToWindow = mouseShouldConfineToWindow;
        LayoutStore.cell = cellSize;
        LayoutStore.setAutosuggestWindowWidth(autosuggestWindowWidth);
        FontStore.mainFontSizeDefault = mainFontSize;
        FontStore.sideFontSizeDefault = sideFontSize;
        if (themeCustom != null) {
            ThemeStore.setCustomPaletteAndMakeDefaultBeforeInit(themeCustom);
        } else if (themePreset != null) {
            ThemeStore.selectThemeByTypeBeforeInit(themePreset);
        }
        LayoutStore.setIsGuiHidden(startGuiHidden);
        if(sketchNameOverride != null){
            LayoutStore.setOverridingSketchName(sketchNameOverride);
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
     * Should the gui start hidden? Toggle hiding with the 'H' hotkey.
     *
     * @param startGuiHidden whether the gui should start hidden
     * @return this settings object for chaining statements easily
     */
    public LazyGuiSettings setStartGuiHidden(boolean startGuiHidden) {
        this.startGuiHidden = startGuiHidden;
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
     * When the parameter is not null, this loads a specific save file on startup.
     * Also disables loading the latest save on startup.
     *
     * @param fileName name of the save file to load in the format "1" or "1.json"
     * @return this settings object for chaining statements easily
     * @see #setLoadLatestSaveOnStartup(boolean)
     */
    public LazyGuiSettings setLoadSpecificSaveOnStartup(String fileName) {
        this.pathToSpecificSaveToLoadOnStartup = fileName;
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
     * The GUI tries to make windows fit its contents snugly based on longest text in the row at window creation time.
     * Setting this to false disables this behavior and sets all windows to some default size fitting for the folder type.
     *
     * @return this settings object for chaining statements easily
     */
    public LazyGuiSettings setAutosuggestWindowWidth(boolean shouldAutosuggest){
        this.autosuggestWindowWidth = shouldAutosuggest;
        return this;
    }

    boolean getShouldLoadLatestSaveOnStartup() {
        return loadLatestSaveOnStartup;
    }

    String getSpecificSaveToLoadOnStartup() {
        return pathToSpecificSaveToLoadOnStartup;
    }
}
