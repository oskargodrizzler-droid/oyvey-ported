package me.alpha432.oyvey.features.modules.client;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.gui.AimbotGui;

public class ClickGUIModule extends Module {

    public ClickGUIModule() {
        super("ClickGUI", "Opens the aimbot configuration GUI", Module.Category.CLIENT);
    }

    @Override
    public void onEnable() {
        AimbotGui.open();
        this.disable();
    }
}
