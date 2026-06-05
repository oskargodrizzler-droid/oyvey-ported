package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;

public class Triggerbot extends Module {
    
    public Setting<Float> range = register(new Setting<>("Range", 4.5f, 1f, 6f));
    public Setting<Integer> delay = register(new Setting<>("Delay (ms)", 50, 0, 500));
    public Setting<Boolean> onlyWeapon = register(new Setting<>("Only Weapon", true));
    public Setting<Boolean> throughWalls = register(new Setting<>("Through Walls", false));
    
    private long lastShoot = 0;
    
    public Triggerbot() {
        super("Triggerbot", "Auto shoots when crosshair over enemy", Module.Category.COMBAT);
    }
    
    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;
        if (onlyWeapon.getValue() && mc.player.getMainHandStack().isEmpty()) return;
        if (System.currentTimeMillis() - lastShoot < delay.getValue()) return;
        
        Entity target = mc.targetedEntity;
        if (target instanceof LivingEntity && target != mc.player) {
            if (mc.player.distanceTo(target) <= range.getValue()) {
                if (!throughWalls.getValue() && !mc.player.canSee(target)) return;
                
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(Hand.MAIN_HAND);
                lastShoot = System.currentTimeMillis();
            }
        }
    }
}
