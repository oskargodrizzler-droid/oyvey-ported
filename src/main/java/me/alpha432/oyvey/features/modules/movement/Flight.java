package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class Flight extends Module {
    
    public Setting<Float> speed = register(new Setting<>("Speed", 1.0f, 0.1f, 5.0f));
    public Setting<Boolean> antiKick = register(new Setting<>("Anti Kick", true));
    
    private int tickCounter = 0;
    
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
        
        if (antiKick.getValue()) {
            tickCounter++;
            if (tickCounter > 40) {
                sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(false));
                tickCounter = 0;
            }
        }
    }
}
