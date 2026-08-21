package dev.birinder.spyglass.mixin;

import dev.birinder.spyglass.util.SpyglassTargetUtil;
import net.neoforged.fml.ModList;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "getTeamColor", at = @At("HEAD"), cancellable = true)
    private void spyglass$getTeamColor(CallbackInfoReturnable<Integer> cir) {
        if (ModList.get().isLoaded("ancient_craft")) {
            return;
        }

        Entity self = (Entity) (Object) this;
        if (SpyglassTargetUtil.isCurrentTarget(self)) {
            cir.setReturnValue(SpyglassTargetUtil.getTargetColor(self));
        }
    }
}
