package lazy;

import lazy.stores.FontStore;
import lazy.stores.JsonSaveStore;
import lazy.stores.LayoutStore;
import lazy.themes.Theme;
import lazy.themes.ThemeStore;
import lazy.utils.MouseHiding;

/**
 * Settings to apply once inside the main LazyGui constructor on startup before loading any saves that might overwrite them.
 * To avoid these settings getting overwritten - disable loading the latest save in the settings.
 * Meant to be used like this:
 * <pre>
 *      gui = new LazyGui(this, new LazyGuiSettings()
 *          .setLoadLatestSaveOnStartup(false)
 *          .setAutosaveOnExitEnabled(false)
 *          .setMouseConfinedToWindow(true)
 *      );
 * </pre>
 */
public class LazyGuiSettings {

    private boolean loadLatestSaveOnStartup, autosaveEnabled, autosaveLockGuardEnabled,
            mouseHidesWhenDragging, mouseConfinedToWindow;
    private long autosaveLockGuardMillisLimit;
    private float cellSize;
    private int mainFontSize, sideFontSize;
    private Theme customTheme = null;

    public LazyGuiSettings(){
        autosaveEnabled = true;
        initializeDefaultsFromGlobalConstants();
    }

    void initializeDefaultsFromGlobalConstants(){
        this.loadLatestSaveOnStartup = JsonSaveStore.autosaveOnExitEnabled;
        this.autosaveLockGuardEnabled = JsonSaveStore.autosaveLockGuardEnabled;
        this.autosaveLockGuardMillisLimit = JsonSaveStore.autosaveLockGuardMillisLimit;
        this.mouseHidesWhenDragging = MouseHiding.shouldHideWhenDragging;
        this.mouseConfinedToWindow =  MouseHiding.shouldConfineToWindow;
        this.cellSize = LayoutStore.cell;
        this.mainFontSize = FontStore.mainFontSizeDefault;
        this.sideFontSize = FontStore.sideFontSizeDefault;
    }

    void overwriteGlobalConstantsWithTheseSettings(){
        JsonSaveStore.autosaveOnExitEnabled = this.autosaveEnabled;
        JsonSaveStore.autosaveLockGuardEnabled = this.autosaveLockGuardEnabled;
        JsonSaveStore.autosaveLockGuardMillisLimit = this.autosaveLockGuardMillisLimit;
        MouseHiding.shouldHideWhenDragging = this.mouseHidesWhenDragging;
        MouseHiding.shouldConfineToWindow = this.mouseConfinedToWindow;
        LayoutStore.cell = this.cellSize;
        FontStore.mainFontSizeDefault = this.mainFontSize;
        FontStore.sideFontSizeDefault = this.sideFontSize;
        if(customTheme != null){
            ThemeStore.setCustomPaletteAndMakeDefaultBeforeInit(customTheme);
        }
    }

    boolean getShouldLoadLatestSaveOnStartup() {
        return loadLatestSaveOnStartup;
    }

    public LazyGuiSettings setCustomTheme(Theme customTheme){
        this.customTheme = customTheme;
        return this;
    }

    public LazyGuiSettings setLoadLatestSaveOnStartup(boolean loadLatestSaveOnStartup) {
        this.loadLatestSaveOnStartup = loadLatestSaveOnStartup;
        return this;
    }

    public LazyGuiSettings setAutosaveOnExitEnabled(boolean autosaveEnabled) {
        this.autosaveEnabled = autosaveEnabled;
        return this;
    }

    public LazyGuiSettings setAutosaveLockGuardMillisLimit(int autosaveLockGuardMillisLimit) {
        this.autosaveLockGuardMillisLimit = autosaveLockGuardMillisLimit;
        return this;
    }

    public LazyGuiSettings setMouseHidesWhenDragging(boolean mouseHidesWhenDragging) {
        this.mouseHidesWhenDragging = mouseHidesWhenDragging;
        return this;
    }

    public LazyGuiSettings setMouseConfinedToWindow(boolean mouseConfinedToWindow) {
        this.mouseConfinedToWindow = mouseConfinedToWindow;
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
}
