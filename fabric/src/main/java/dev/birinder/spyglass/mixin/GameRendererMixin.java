package dev.birinder.spyglass.mixin;

import dev.birinder.spyglass.util.SpyglassZoomUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void spyglass$getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        // If full Ancient Craft mod is active, yield to Ancient Craft
        if (FabricLoader.getInstance().isModLoaded("ancient_craft")) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && client.player.isUsingItem() && client.player.getActiveItem().isOf(Items.SPYGLASS)) {
            double currentFov = cir.getReturnValueD();
            float zoomFactor = SpyglassZoomUtil.getZoomFactor();
            cir.setReturnValue(currentFov / (double) zoomFactor);
        }
    }
}
