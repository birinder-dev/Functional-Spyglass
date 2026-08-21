package dev.birinder.spyglass.mixin;

import dev.birinder.spyglass.util.SpyglassTargetUtil;
import net.minecraftforge.fml.ModList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

    @Shadow
    public LocalPlayer player;

    @Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
    private void spyglass$shouldEntityAppearGlowing(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (ModList.get().isLoaded("ancient_craft")) {
            return;
        }

        if (this.player != null && this.player.isUsingItem() && this.player.getUseItem().is(Items.SPYGLASS)) {
            if (SpyglassTargetUtil.isCurrentTarget(entity)) {
                cir.setReturnValue(true);
            }
        }
    }
}
