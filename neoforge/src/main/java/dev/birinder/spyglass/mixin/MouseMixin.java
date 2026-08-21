package dev.birinder.spyglass.mixin;

import dev.birinder.spyglass.util.SpyglassZoomUtil;
import net.neoforged.fml.ModList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseMixin {

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void spyglass$onScroll(long windowPointer, double xOffset, double yOffset, CallbackInfo ci) {
        if (ModList.get().isLoaded("ancient_craft")) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        if (client.player != null && client.player.isUsingItem() && client.player.getUseItem().is(Items.SPYGLASS)) {
            if (yOffset != 0.0D) {
                SpyglassZoomUtil.onScroll(yOffset);
                ci.cancel();
            }
        }
    }
}
