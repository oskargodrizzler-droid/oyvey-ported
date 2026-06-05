package me.alpha432.oyvey.mixin.network;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.modules.movement.Flight;
import me.alpha432.oyvey.features.modules.movement.Speed;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    /**
     * Tick hook – allows Flight to keep flying flag enabled each tick.
     * Also lets Speed module adjust velocity if needed.
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        Flight flight = OyVey.moduleManager.getModuleByClass(Flight.class);
        if (flight != null && flight.isEnabled()) {
            // Ensure flying flag stays true (bypasses some anti-cheats)
            ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
            player.getAbilities().flying = true;
        }

        Speed speed = OyVey.moduleManager.getModuleByClass(Speed.class);
        if (speed != null && speed.isEnabled()) {
            // Additional speed logic if needed (though onUpdate already handles it)
        }
    }
}
