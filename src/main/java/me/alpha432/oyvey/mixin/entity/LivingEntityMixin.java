package me.alpha432.oyvey.mixin.entity;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.modules.combat.AimAssist;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    /**
     * Optional: small damage boost when AimAssist is active.
     * (Requires AimAssist module to have a "damageBoost" setting)
     */
    @ModifyArg(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V"), index = 1)
    private float modifyDamage(float amount) {
        AimAssist aimAssist = OyVey.moduleManager.getModuleByClass(AimAssist.class);
        if (aimAssist != null && aimAssist.isEnabled() && aimAssist.autoClick.getValue()) {
            // 5% damage boost – you can make this configurable
            return amount * 1.05f;
        }
        return amount;
    }
}
