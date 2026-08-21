package dev.birinder.spyglass.mixin;

import dev.birinder.spyglass.util.SpyglassTargetUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow
    public ClientPlayerEntity player;

    @Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
    private void spyglass$hasOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        // If full Ancient Craft mod is active, yield to Ancient Craft
        if (FabricLoader.getInstance().isModLoaded("ancient_craft")) {
            return;
        }

        if (this.player != null && this.player.isUsingItem() && this.player.getActiveItem().isOf(Items.SPYGLASS)) {
            if (SpyglassTargetUtil.isCurrentTarget(entity)) {
                cir.setReturnValue(true);
            }
        }
    }
}
