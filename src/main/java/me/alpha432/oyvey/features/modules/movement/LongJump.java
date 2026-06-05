package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.util.math.Vec3d;

public class LongJump extends Module {

    public Setting<Float> boost = register(new Setting<>("Boost", 1.5f, 1.0f, 3.0f));
    public Setting<Boolean> autoJump = register(new Setting<>("Auto Jump", true));

    private boolean jumped = false;

    public LongJump() {
        super("LongJump", "Jump further and higher", Module.Category.MOVEMENT);
    }

    @Override
    public void onUpdate() {
        if (mc.player == null) return;

        if (autoJump.getValue() && mc.player.isOnGround() && !jumped) {
            mc.player.jump();
            jumped = true;
        }

        if (!mc.player.isOnGround() && jumped) {
            double boostValue = boost.getValue();
            Vec3d velocity = mc.player.getVelocity();
            float yaw = mc.player.getYaw();
            float forward = mc.player.forwardSpeed;
            float strafe = mc.player.sidewaysSpeed;

            double rad = Math.toRadians(yaw);
            double newX = velocity.x + (forward * boostValue * Math.cos(rad) + strafe * boostValue * Math.sin(rad));
            double newZ = velocity.z + (forward * boostValue * Math.sin(rad) - strafe * boostValue * Math.cos(rad));

            mc.player.setVelocity(newX, velocity.y, newZ);
            jumped = false;
        }
    }

    @Override
    public void onDisable() {
        jumped = false;
    }
}
