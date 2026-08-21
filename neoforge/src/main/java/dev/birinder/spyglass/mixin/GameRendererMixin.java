package dev.birinder.spyglass.mixin;

import dev.birinder.spyglass.util.SpyglassZoomUtil;
import net.neoforged.fml.ModList;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void spyglass$getFov(Camera camera, float partialTick, boolean useFovSetting, CallbackInfoReturnable<Double> cir) {
        if (ModList.get().isLoaded("ancient_craft")) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        if (client.player != null && client.player.isUsingItem() && client.player.getUseItem().is(Items.SPYGLASS)) {
            double currentFov = cir.getReturnValueD();
            float zoomFactor = SpyglassZoomUtil.getZoomFactor();
            cir.setReturnValue(currentFov / (double) zoomFactor);
        }
    }
}
