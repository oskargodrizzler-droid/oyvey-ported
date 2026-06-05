package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AimAssist extends Module {
    
    public Setting<Float> range = register(new Setting<>("Range", 4.5f, 1f, 8f));
    public Setting<Float> fov = register(new Setting<>("FOV", 90f, 10f, 180f));
    public Setting<Float> smoothing = register(new Setting<>("Smoothing", 50f, 1f, 100f));
    public Setting<Boolean> autoClick = register(new Setting<>("Auto Click", true));
    public Setting<Integer> cps = register(new Setting<>("CPS", 12, 1, 20));
    
    private long lastClick = 0;
    
    public AimAssist() {
        super("AimAssist", "Automatically aims at enemies", Module.Category.COMBAT);
    }
    
    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;
        
        LivingEntity target = getTarget();
        if (target == null) return;
        
        // Aim at target
        Vec3d direction = target.getEyePos().subtract(mc.player.getEyePos());
        float yaw = (float) Math.toDegrees(MathHelper.atan2(direction.z, direction.x)) - 90;
        float pitch = (float) -Math.toDegrees(MathHelper.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));
        
        float smooth = smoothing.getValue() / 100f;
        float newYaw = mc.player.getYaw() + (yaw - mc.player.getYaw()) * smooth;
        float newPitch = mc.player.getPitch() + (pitch - mc.player.getPitch()) * smooth;
        
        mc.player.setYaw(newYaw);
        mc.player.setPitch(newPitch);
        
        // Auto click
        if (autoClick.getValue() && System.currentTimeMillis() - lastClick > 1000 / cps.getValue()) {
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(Hand.MAIN_HAND);
            lastClick = System.currentTimeMillis();
        }
    }
    
    private LivingEntity getTarget() {
        double closestDistance = fov.getValue();
        LivingEntity closest = null;
        
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity)) continue;
            if (entity == mc.player) continue;
            if (entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative()) continue;
            
            double distance = mc.player.distanceTo(entity);
            if (distance > range.getValue()) continue;
            
            // Check FOV
            double angle = getAngleToEntity(entity);
            if (angle > fov.getValue()) continue;
            
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = (LivingEntity) entity;
            }
        }
        return closest;
    }
    
    private double getAngleToEntity(Entity entity) {
        Vec3d eyePos = mc.player.getEyePos();
        Vec3d targetPos = entity.getBoundingBox().getCenter();
        double dx = targetPos.x - eyePos.x;
        double dz = targetPos.z - eyePos.z;
        double dy = targetPos.y - eyePos.y;
        
        double yaw = Math.toDegrees(Math.atan2(dz, dx)) - 90;
        double pitch = -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx*dx + dz*dz)));
        
        double deltaYaw = Math.abs(yaw - mc.player.getYaw()) % 360;
        if (deltaYaw > 180) deltaYaw = 360 - deltaYaw;
        double deltaPitch = Math.abs(pitch - mc.player.getPitch());
        
        return Math.sqrt(deltaYaw * deltaYaw + deltaPitch * deltaPitch);
    }
}
