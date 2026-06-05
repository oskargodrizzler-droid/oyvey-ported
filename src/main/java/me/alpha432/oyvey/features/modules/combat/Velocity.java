package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;

public class Velocity extends Module {
    
    public Setting<Integer> horizontal = register(new Setting<>("Horizontal", 0, 0, 100));
    public Setting<Integer> vertical = register(new Setting<>("Vertical", 0, 0, 100));
    
    public Velocity() {
        super("Velocity", "Reduces knockback taken", Module.Category.COMBAT);
    }
    
    @Override
    public void onUpdate() {
        // Mixin handles the actual velocity modification
        // This module just enables/disables the mixin
    }
}
