package dev.birinder.spyglass.mixin;

import dev.birinder.spyglass.util.SpyglassZoomUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void spyglass$onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        // If full Ancient Craft mod is active, yield to Ancient Craft
        if (FabricLoader.getInstance().isModLoaded("ancient_craft")) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && client.player.isUsingItem() && client.player.getActiveItem().isOf(Items.SPYGLASS)) {
            if (vertical != 0.0D) {
                SpyglassZoomUtil.onScroll(vertical);
                ci.cancel(); // Prevents hotbar item switching while zooming through spyglass!
            }
        }
    }
}
