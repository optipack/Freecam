package net.xolt.freecam.mixins;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static net.xolt.freecam.Freecam.MC;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @ModifyArg(method = "onItemPickupAnimation", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getEntityById(I)Lnet/minecraft/entity/Entity;"))
    private int onItemPickupAnimation(int entityId) {
        if (Freecam.isEnabled() && ModConfig.INSTANCE.showClone && entityId == MC.player.getId()) {
            return Freecam.getClone().getId();
        }
        return entityId;
    }
}