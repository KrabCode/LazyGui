package lazy;

import lazy.stores.FontStore;
import lazy.stores.JsonSaveStore;
import lazy.stores.LayoutStore;
import lazy.themes.Theme;
import lazy.themes.ThemeStore;
import lazy.themes.ThemeType;
import lazy.utils.MouseHiding;

/**
 * Settings to apply once inside the main LazyGui constructor on startup before loading any saves that might overwrite them.
 * To avoid these settings getting overwritten - disable loading the latest save in the settings.
 * Meant to be used like this:
 * <pre>
 *      gui = new LazyGui(this, new LazyGuiSettings()
 *          .setLoadLatestSaveOnStartup(false)
 *          .setAutosaveOnExitEnabled(false)
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
    private long autosaveLockGuardMillisLimit;
    private float cellSize;
    private int mainFontSize, sideFontSize;
    private Theme themeCustom = null;
    private ThemeType themePreset = null;
    private String pathToSpecificSaveToLoadOnStartup = null;

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
        this.mainFontSize = FontStore.mainFontSizeDefault;
        this.sideFontSize = FontStore.sideFontSizeDefault;
    }

    void applySettingsOntoGui() {
        JsonSaveStore.autosaveOnExitEnabled = this.autosaveOnExitEnabled;
        JsonSaveStore.autosaveLockGuardEnabled = this.autosaveLockGuardEnabled;
        JsonSaveStore.autosaveLockGuardMillisLimit = this.autosaveLockGuardMillisLimit;
        MouseHiding.shouldHideWhenDragging = this.mouseShouldHideWhenDragging;
        MouseHiding.shouldConfineToWindow = this.mouseShouldConfineToWindow;
        LayoutStore.cell = this.cellSize;
        FontStore.mainFontSizeDefault = this.mainFontSize;
        FontStore.sideFontSizeDefault = this.sideFontSize;
        if (themeCustom != null) {
            ThemeStore.setCustomPaletteAndMakeDefaultBeforeInit(themeCustom);
        } else if (themePreset != null){
            ThemeStore.selectThemeByTypeBeforeInit(themePreset);
        }
    }

    public LazyGuiSettings setThemePreset(ThemeType themePreset) {
        this.themePreset = themePreset;
        return this;
    }

    public LazyGuiSettings setThemeCustom(Theme themeCustom) {
        this.themeCustom = themeCustom;
        return this;
    }

    public LazyGuiSettings setLoadSpecificSave(String pathToJsonFile) {
        this.pathToSpecificSaveToLoadOnStartup = pathToJsonFile;
        return this;
    }

    public LazyGuiSettings setLoadLatestSaveOnStartup(boolean loadLatestSaveOnStartup) {
        this.loadLatestSaveOnStartup = loadLatestSaveOnStartup;
        return this;
    }

    public LazyGuiSettings setAutosaveOnExit(boolean autosaveEnabled) {
        this.autosaveOnExitEnabled = autosaveEnabled;
        return this;
    }

    public LazyGuiSettings setAutosaveLockGuardEnabled(boolean autosaveLockGuardEnabled) {
        this.autosaveLockGuardEnabled = autosaveLockGuardEnabled;
        return this;
    }

    public LazyGuiSettings setAutosaveLockGuardMillisLimit(long autosaveLockGuardMillisLimit) {
        this.autosaveLockGuardMillisLimit = autosaveLockGuardMillisLimit;
        return this;
    }

    public LazyGuiSettings setAutosaveLockGuardMillisLimit(int autosaveLockGuardMillisLimit) {
        this.autosaveLockGuardMillisLimit = autosaveLockGuardMillisLimit;
        return this;
    }

    public LazyGuiSettings setMouseShouldHideWhenDragging(boolean mouseShouldHideWhenDragging) {
        this.mouseShouldHideWhenDragging = mouseShouldHideWhenDragging;
        return this;
    }

    public LazyGuiSettings setMouseShouldConfineToWindow(boolean mouseShouldConfineToWindow) {
        this.mouseShouldConfineToWindow = mouseShouldConfineToWindow;
        return this;
    }

    public LazyGuiSettings setCellSize(float cellSize) {
        this.cellSize = cellSize;
        return this;
    }

    public LazyGuiSettings setMainFontSize(int mainFontSize) {
        this.mainFontSize = mainFontSize;
        return this;
    }

    public LazyGuiSettings setSideFontSize(int sideFontSize) {
        this.sideFontSize = sideFontSize;
        return this;
    }

    boolean getShouldLoadLatestSaveOnStartup() {
        return loadLatestSaveOnStartup;
    }

    public String getSpecificSaveToLoadOnStartup() {
        return pathToSpecificSaveToLoadOnStartup;
    }
}
