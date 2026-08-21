package dev.birinder.spyglass;

import dev.birinder.spyglass.gui.SpyglassHudOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.RenderGuiEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("insight_glass")
public class SpyglassInspectorMod {
    public static final String MOD_ID = "insight_glass";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientEvents {

        private static final SpyglassHudOverlay OVERLAY = new SpyglassHudOverlay();

        @SubscribeEvent
        public static void onRenderGui(RenderGuiEvent.Post event) {
            if (ModList.get().isLoaded("ancient_craft")) {
                return;
            }
            OVERLAY.render(event.getGuiGraphics(), event.getPartialTick());
        }
    }
}
