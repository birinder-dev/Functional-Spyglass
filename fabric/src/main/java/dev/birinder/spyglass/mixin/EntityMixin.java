package dev.birinder.spyglass.mixin;

import dev.birinder.spyglass.util.SpyglassTargetUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
    private void spyglass$getTeamColorValue(CallbackInfoReturnable<Integer> cir) {
        // If full Ancient Craft mod is active, yield to Ancient Craft
        if (FabricLoader.getInstance().isModLoaded("ancient_craft")) {
            return;
        }

        Entity self = (Entity) (Object) this;
        if (SpyglassTargetUtil.isCurrentTarget(self)) {
            cir.setReturnValue(SpyglassTargetUtil.getTargetColor(self));
        }
    }
}
