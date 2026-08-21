package dev.birinder.spyglass.gui;

import dev.birinder.spyglass.util.SpyglassTargetUtil;
import dev.birinder.spyglass.util.SpyglassZoomUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class SpyglassHudOverlay {

    public static boolean isRenderingPortrait = false;

    public void render(GuiGraphics drawContext, float partialTick) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) {
            return;
        }

        // Only render HUD card when actively scoping through Spyglass
        if (!client.player.isUsingItem() || !client.player.getUseItem().is(Items.SPYGLASS)) {
            return;
        }

        LivingEntity target = SpyglassTargetUtil.getTargetedLivingEntity(client, 64.0F);
        if (target == null) {
            return;
        }

        int screenWidth = drawContext.guiWidth();
        int cardWidth = 195;
        int cardX = (screenWidth - cardWidth) / 2;
        int cardY = 4; // Shifted up near top border

        // Portrait Box dimensions on the left (Ultra-compact 34x34 face frame)
        int portraitBoxSize = 34;
        int portraitX = cardX + 4;
        int portraitY = cardY + 4;

        List<Component> lines = new ArrayList<>();

        // 1. Entity Name & Classification
        String nameStr = target.getDisplayName().getString();
        ChatFormatting nameColor = ChatFormatting.YELLOW;
        if (target instanceof Player) {
            nameColor = ChatFormatting.AQUA;
        } else if (target instanceof AbstractHorse) {
            nameColor = ChatFormatting.GOLD;
        } else if (!target.getType().canSerialize()) {
            nameColor = ChatFormatting.WHITE;
        }
        lines.add(Component.literal("◆ " + nameStr).withStyle(nameColor, ChatFormatting.BOLD));

        // 2. Health & Effective Health (EHP) with Armor Calculation
        float health = target.getHealth();
        float maxHealth = target.getMaxHealth();
        int armor = target.getArmorValue();
        float damageReduction = Math.min(0.80F, armor * 0.04F);
        float effectiveHealth = damageReduction < 1.0F ? health / (1.0F - damageReduction) : health;

        String hpStr = String.format("HP: %.1f/%.1f", health, maxHealth);
        ChatFormatting hpColor = (health / maxHealth > 0.5F) ? ChatFormatting.GREEN : (health / maxHealth > 0.25F ? ChatFormatting.YELLOW : ChatFormatting.RED);

        if (armor > 0) {
            lines.add(Component.literal("♥ " + hpStr + String.format(" (EHP: %.0f)", effectiveHealth)).withStyle(hpColor));
            lines.add(Component.literal(String.format("🛡 Armor: %d (%.0f%%)", armor, damageReduction * 100.0F)).withStyle(ChatFormatting.AQUA));
        } else {
            lines.add(Component.literal("♥ " + hpStr).withStyle(hpColor));
        }

        // 3. Mount Stats (Combined on one compact line with distinct colors and 'blocks'!)
        if (target instanceof AbstractHorse horse) {
            AttributeInstance speedAttr = horse.getAttribute(Attributes.MOVEMENT_SPEED);
            double speed = speedAttr != null ? speedAttr.getValue() * 43.17D : 0.0D;
            double jumpStrength = horse.getAttributeValue(Attributes.JUMP_STRENGTH);
            double jumpHeight = -0.18D + 3.0D * jumpStrength + 4.0D * jumpStrength * jumpStrength;

            Component mountText = Component.literal("🐎 ")
                    .withStyle(ChatFormatting.LIGHT_PURPLE)
                    .append(Component.literal(String.format("%.2f m/s  ", speed)).withStyle(ChatFormatting.LIGHT_PURPLE))
                    .append(Component.literal(String.format("▲ %.2f blocks", jumpHeight)).withStyle(ChatFormatting.GOLD));

            lines.add(mountText);
        }

        // 4. Taming Status (Pink for tamed!)
        if (target instanceof TamableAnimal tameable) {
            if (tameable.isTame()) {
                LivingEntity owner = tameable.getOwner();
                String ownerName = owner != null ? owner.getName().getString() : "Tamed";
                lines.add(Component.literal("★ Tamed: " + ownerName).withStyle(ChatFormatting.LIGHT_PURPLE));
            } else {
                lines.add(Component.literal("☆ Wild").withStyle(ChatFormatting.GRAY));
            }
        }

        // 5. Active Potion Effects
        for (MobEffectInstance effect : target.getActiveEffects()) {
            String effectName = Component.translatable(effect.getEffect().value().getDescriptionId()).getString();
            int durationSecs = effect.getDuration() / 20;
            lines.add(Component.literal(String.format("✦ %s (%ds)", effectName, durationSecs)).withStyle(ChatFormatting.DARK_AQUA));
        }

        // 6. Current Zoom Level
        lines.add(Component.literal(String.format("🔍 Zoom: %.1fx", SpyglassZoomUtil.getZoomFactor())).withStyle(ChatFormatting.GRAY));

        // Calculate card height dynamically (Comfortable 9px spacing to prevent text congestion)
        int lineHeight = 9;
        int textSectionHeight = lines.size() * lineHeight;
        int cardHeight = Math.max(portraitBoxSize + 7, textSectionHeight + 7);

        // Render Semi-Transparent Dark Card Background & Gold Border
        drawContext.fill(cardX, cardY, cardX + cardWidth, cardY + cardHeight, 0xDD101015);
        drawContext.renderOutline(cardX, cardY, cardWidth, cardHeight, 0xFFDAA520);

        // Render 3D Portrait Frame
        drawContext.fill(portraitX, portraitY, portraitX + portraitBoxSize, portraitY + portraitBoxSize, 0xBB1A1A22);
        drawContext.renderOutline(portraitX, portraitY, portraitBoxSize, portraitBoxSize, 0xFF888888);

        // Render Pure Matrix Eye-Anchored 3D Entity (Zero world mob stutter & universal face centering!)
        try {
            isRenderingPortrait = true;
            drawContext.enableScissor(portraitX + 1, portraitY + 1, portraitX + portraitBoxSize - 1, portraitY + portraitBoxSize - 1);

            float entityWidth = target.getBbWidth();
            float entityHeight = target.getBbHeight();
            float eyeHeight = target.getEyeHeight();
            float maxDimension = Math.max(entityWidth, entityHeight);

            // Large wide mobs (Ghasts, Dragons, Elder Guardians, Giant Slimes) fit full body;
            // standard/tall biped mobs (Endermen, Players, Horses) zoom cleanly into face & eyes
            boolean isHugeMob = entityWidth > 1.8F || maxDimension > 3.5F;
            int renderSize;
            if (isHugeMob) {
                renderSize = Math.max(3, (int) (20.0F / maxDimension));
            } else {
                float headSpan = Math.max(0.55F, Math.max(entityWidth * 0.7F, Math.min(entityHeight * 0.35F, 1.2F)));
                renderSize = Math.max(8, Math.min(22, (int) (15.0F / headSpan)));
            }

            // Mathematically anchors the eyes/face dead-center in the portrait box
            int centerY = portraitY + portraitBoxSize / 2;
            int entityBottomY = centerY + (int) (eyeHeight * renderSize);

            // Small creatures & fish tilt slightly downwards from above (-16F pitch)
            float pitchAngle = (entityHeight < 0.6F || target instanceof AbstractFish) ? -16.0F : 0.0F;

            // Matrix-only rotation (+25F yaw makes them face right!)
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                    drawContext,
                    portraitX + 2, portraitY + 2,
                    portraitX + portraitBoxSize - 2, entityBottomY,
                    renderSize,
                    0.0625F,
                    25.0F, pitchAngle,
                    target
            );

            drawContext.disableScissor();
        } catch (Throwable t) {
            drawContext.disableScissor();
            drawContext.drawString(client.font, Component.literal("?").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), portraitX + 14, portraitY + 12, 0xFFDAA520, false);
        } finally {
            isRenderingPortrait = false;
        }

        // Health Bar Visualization (Full Card Width at Bottom)
        int barX = cardX + 5;
        int barY = cardY + cardHeight - 4;
        int barWidth = cardWidth - 10;
        int healthFill = (int) (barWidth * Math.min(1.0F, Math.max(0.0F, health / maxHealth)));

        drawContext.fill(barX, barY, barX + barWidth, barY + 2, 0xFF441111);
        drawContext.fill(barX, barY, barX + healthFill, barY + 2, 0xFF00FF55);

        // If mob has armor, draw cyan armor segment overlay on top of health bar
        if (armor > 0) {
            int armorFill = (int) (barWidth * Math.min(1.0F, armor / 20.0F));
            drawContext.fill(barX, barY - 1, barX + armorFill, barY, 0xFF00DDFF);
        }

        // Render text lines to the right of the 3D portrait
        int textX = portraitX + portraitBoxSize + 5;
        int textY = cardY + 3;
        for (Component line : lines) {
            drawContext.drawString(client.font, line, textX, textY, 0xFFFFFFFF, false);
            textY += lineHeight;
        }
    }
}
