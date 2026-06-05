package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;

public class Flight extends Module {

    public Setting<Float> speed = register(new Setting<>("Speed", 1.0f, 0.1f, 5.0f));

    public Flight() {
        super("Flight", "Allows you to fly in survival", Module.Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        if (mc.player != null) {
            mc.player.getAbilities().allowFlying = true;
        }
    }

    @Override
    public void onDisable() {
        if (mc.player != null && !mc.player.getAbilities().creativeMode) {
            mc.player.getAbilities().allowFlying = false;
            mc.player.getAbilities().flying = false;
        }
    }

    @Override
    public void onUpdate() {
        if (mc.player == null) return;
        mc.player.getAbilities().flying = true;
        mc.player.getAbilities().setFlySpeed(speed.getValue() / 10f);
    }
}
