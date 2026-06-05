package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class LongJump extends Module {
    
    public Setting<Float> speed = register(new Setting<>("Speed", 2.0f, 1.0f, 5.0f));
    public Setting<Float> height = register(new Setting<>("Height", 1.2f, 0.5f, 2.0f));
    public Setting<Boolean> lagCompensate = register(new Setting<>("Lag Compensate", true));
    
    private int jumpTicks = 0;
    private boolean hasJumped = false;
    
    public LongJump() {
        super("LongJump", "Jump further and higher", Module.Category.MOVEMENT);
    }
    
    @Override
    public void onUpdate() {
        if (mc.player == null) return;
        
        if (mc.player.isOnGround() && !hasJumped) {
            mc.player.jump();
            hasJumped = true;
            jumpTicks = 0;
        }
        
        if (hasJumped && !mc.player.isOnGround()) {
            jumpTicks++;
            
            // Apply motion for the first few ticks
            if (jumpTicks <= 10) {
                float yaw = mc.player.getYaw();
                float forward = mc.player.forwardSpeed;
                float strafe = mc.player.sidewaysSpeed;
                
                double rad = Math.toRadians(yaw);
                double boost = speed.getValue() * 0.3;
                
                double addX = (forward * boost * Math.cos(rad) + strafe * boost * Math.sin(rad));
                double addZ = (forward * boost * Math.sin(rad) - strafe * boost * Math.cos(rad));
                
                Vec3d vel = mc.player.getVelocity();
                mc.player.setVelocity(vel.x + addX, height.getValue() * 0.3, vel.z + addZ);
                
                if (lagCompensate.getValue()) {
                    sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                        mc.player.getX() + addX,
                        mc.player.getY() + 0.42,
                        mc.player.getZ() + addZ,
                        false
                    ));
                }
            }
        }
    }
    
    @Override
    public void onDisable() {
        hasJumped = false;
        jumpTicks = 0;
    }
}
