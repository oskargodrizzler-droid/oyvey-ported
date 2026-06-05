package me.alpha432.oyvey.mixin.entity;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.modules.player.VelocityModule;
import me.alpha432.oyvey.features.modules.player.NoFallModule;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    /**
     * Velocity (knockback) modification.
     * Cancels knockback entirely if horizontal/vertical are set to 0.
     */
    @Inject(method = "applyDamage", at = @At("HEAD"), cancellable = true)
    private void onApplyDamage(DamageSource source, float amount, CallbackInfo ci) {
        VelocityModule velocity = OyVey.moduleManager.getModuleByClass(VelocityModule.class);
        if (velocity != null && velocity.isEnabled()) {
            if (velocity.horizontal.getValue() == 0 && velocity.vertical.getValue() == 0) {
                ci.cancel();  // No knockback at all
            }
        }
    }

    /**
     * No fall damage.
     */
    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
    private void onHandleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        NoFallModule noFall = OyVey.moduleManager.getModuleByClass(NoFallModule.class);
        if (noFall != null && noFall.isEnabled()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
