package net.xolt.freecam.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.BlockState;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.Packet;
import net.minecraft.util.math.BlockPos;
import net.xolt.freecam.config.ModConfig;

import java.util.UUID;

import static net.xolt.freecam.Freecam.MC;

public class FreeCamera extends ClientPlayerEntity {

    private static final ClientPlayNetworkHandler NETWORK_HANDLER = new ClientPlayNetworkHandler(MC, MC.currentScreen, MC.getNetworkHandler().getConnection(), new GameProfile(UUID.randomUUID(), "FreeCamera"), MC.createTelemetrySender()) {
        @Override
        public void sendPacket(Packet<?> packet) {
        }
    };

    public FreeCamera() {
        super(MC, MC.world, NETWORK_HANDLER, MC.player.getStatHandler(), MC.player.getRecipeBook(), false, false);

        copyPositionAndRotation(MC.player);
        renderPitch = getPitch();
        renderYaw = getYaw();
        lastRenderPitch = renderPitch; // Prevents camera from rotating upon entering freecam.
        lastRenderYaw = renderYaw;
        getAbilities().flying = true;
        input = new KeyboardInput(MC.options);
    }

    public void spawn() {
        if (clientWorld != null) {
            clientWorld.addEntity(getId(), this);
        }
    }

    public void despawn() {
        if (clientWorld != null && clientWorld.getEntityById(getId()) != null) {
            clientWorld.removeEntity(getId(), RemovalReason.DISCARDED);
        }
    }

    // Prevents fall damage sound when FreeCamera touches ground with noClip disabled.
    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
    }

    // Needed for hand swings to be shown in freecam since the player is replaced by FreeCamera in HeldItemRenderer.renderItem()
    @Override
    public float getHandSwingProgress(float tickDelta) {
        return MC.player.getHandSwingProgress(tickDelta);
    }

    // Needed for item use animations to be shown in freecam since the player is replaced by FreeCamera in HeldItemRenderer.renderItem()
    @Override
    public int getItemUseTimeLeft() {
        return MC.player.getItemUseTimeLeft();
    }

    // Also needed for item use animations to be shown in freecam.
    @Override
    public boolean isUsingItem() {
        return MC.player.isUsingItem();
    }

    // Prevents shadow being cast when Iris is enabled.
    @Override
    public boolean isSpectator() {
        return true;
    }

    // Makes night vision apply to FreeCamera when Iris is enabled.
    @Override
    public StatusEffectInstance getStatusEffect(StatusEffect effect) {
        return MC.player.getStatusEffect(effect);
    }

    // Prevents the pose of the FreeCamera from being modified.
    @Override
    public void setPose(EntityPose pose) {
    }

    @Override
    public void tickMovement() {
//        noClip = ModConfig.INSTANCE.noclip;
    	noClip = false;
        if (ModConfig.INSTANCE.flightMode.equals(ModConfig.FlightMode.DEFAULT)) {
            getAbilities().setFlySpeed(0);
            Motion.doMotion(this, ModConfig.INSTANCE.horizontalSpeed, ModConfig.INSTANCE.verticalSpeed);
        } else {
            getAbilities().setFlySpeed((float) ModConfig.INSTANCE.verticalSpeed / 10);
        }
        super.tickMovement();
        getAbilities().flying = true;
        onGround = false;
    }
}
