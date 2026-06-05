package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;

public class Speed extends Module {
    
    public Setting<Float> multiplier = register(new Setting<>("Multiplier", 1.5f, 1.0f, 5.0f));
    
    public Speed() {
        super("Speed", "Increases movement speed", Module.Category.MOVEMENT);
    }
    
    @Override
    public void onUpdate() {
        if (mc.player == null || mc.player.forwardSpeed == 0) return;
        
        float speed = multiplier.getValue();
        float forward = mc.player.forwardSpeed * speed;
        float strafe = mc.player.sidewaysSpeed * speed;
        float yaw = mc.player.getYaw();
        
        mc.player.setVelocity(
            forward * Math.cos(Math.toRadians(yaw)) + strafe * Math.sin(Math.toRadians(yaw)),
            mc.player.getVelocity().y,
            forward * Math.sin(Math.toRadians(yaw)) - strafe * Math.cos(Math.toRadians(yaw))
        );
    }
}
