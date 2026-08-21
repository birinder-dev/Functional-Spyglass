package dev.birinder.spyglass.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.birinder.spyglass.gui.SpyglassHudOverlay;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    @Inject(method = "renderNameTag", at = @At("HEAD"), cancellable = true)
    private void spyglass$hideLabelInPortrait(T entity, Component component, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick, CallbackInfo ci) {
        if (SpyglassHudOverlay.isRenderingPortrait) {
            ci.cancel();
        }
    }
}
