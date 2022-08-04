package toolbox.windows.nodes.toolbar;

import processing.core.PGraphics;
import toolbox.global.State;
import toolbox.global.themes.Theme;
import toolbox.global.themes.ThemeColorType;
import toolbox.global.themes.ThemeStore;
import toolbox.global.themes.ThemeType;
import toolbox.windows.nodes.NodeFolder;

public class ThemePickerFolder extends NodeFolder {

    public ThemePickerFolder(String path, NodeFolder parent) {
        super(path, parent);
    }

    protected void updateDrawInlineNode(PGraphics pg) {
        // don't draw any inline nodes, update picker state instead
        updateThemePicker();
    }

    private void updateThemePicker() {
        String defaultPaletteName = "dark";
        Theme defaultTheme = ThemeType.getPalette(ThemeType.DARK);
        assert defaultTheme != null;

        String userSelection = State.gui.stringPicker(path + "/preset", ThemeType.getAllNames(), defaultPaletteName);
        if (!userSelection.equals(ThemeType.getName(ThemeStore.currentSelection))) {
            ThemeStore.currentSelection = ThemeType.getValue(userSelection);
        }
        String customDefinitionPath = path + "/custom";
        ThemeStore.setCustomColor(ThemeColorType.FOCUS_FOREGROUND,
                State.gui.colorPicker(customDefinitionPath + "/focus foreground", defaultTheme.focusForeground).hex);
        ThemeStore.setCustomColor(ThemeColorType.FOCUS_BACKGROUND,
                State.gui.colorPicker(customDefinitionPath + "/focus background", defaultTheme.focusBackground).hex);
        ThemeStore.setCustomColor(ThemeColorType.NORMAL_FOREGROUND,
                State.gui.colorPicker(customDefinitionPath + "/normal foreground", defaultTheme.normalForeground).hex);
        ThemeStore.setCustomColor(ThemeColorType.NORMAL_BACKGROUND,
                State.gui.colorPicker(customDefinitionPath + "/normal background", defaultTheme.normalBackground).hex);
        ThemeStore.setCustomColor(ThemeColorType.WINDOW_BORDER,
                State.gui.colorPicker(customDefinitionPath + "/window border", defaultTheme.windowBorder).hex);
    }

}
