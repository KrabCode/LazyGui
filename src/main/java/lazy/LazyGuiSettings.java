package lazy;

import lazy.stores.JsonSaveStore;
import lazy.themes.Theme;
import lazy.themes.ThemeStore;
import lazy.utils.MouseHiding;

public class LazyGuiSettings {

    private boolean loadLatestSaveOnStartup, autosaveEnabled, autosaveLockGuardEnabled,
            mouseHidesWhenDragging, mouseConfinedToWindow;
    private long autosaveLockGuardMillisLimit;
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
    }

    void overwriteGlobalConstantsWithTheseSettings(){
        JsonSaveStore.autosaveOnExitEnabled = this.autosaveEnabled;
        JsonSaveStore.autosaveLockGuardEnabled = this.autosaveLockGuardEnabled;
        JsonSaveStore.autosaveLockGuardMillisLimit = this.autosaveLockGuardMillisLimit;
        MouseHiding.shouldHideWhenDragging = this.mouseHidesWhenDragging;
        MouseHiding.shouldConfineToWindow = this.mouseConfinedToWindow;
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
}
