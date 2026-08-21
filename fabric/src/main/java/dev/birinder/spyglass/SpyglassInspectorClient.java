package dev.birinder.spyglass;

import dev.birinder.spyglass.gui.SpyglassHudOverlay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpyglassInspectorClient implements ClientModInitializer {
    public static final String MOD_ID = "spyglass_inspector";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        // If the player also has the full Ancient Craft mod installed,
        // Ancient Craft's advanced stats HUD takes priority with ZERO crash!
        if (FabricLoader.getInstance().isModLoaded("ancient_craft")) {
            LOGGER.info("Ancient Craft detected! Yielding Spyglass Inspector to Ancient Craft's advanced stats engine.");
            return;
        }

        HudRenderCallback.EVENT.register(new SpyglassHudOverlay());
        LOGGER.info("Spyglass Inspector initialized successfully!");
    }
}
