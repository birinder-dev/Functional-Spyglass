package dev.birinder.spyglass.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class SpyglassTargetUtil {

    private static LivingEntity currentTarget = null;
    private static long lastUpdateTime = 0;

    public static LivingEntity getTargetedLivingEntity(Minecraft client, float maxDistance) {
        if (client.player == null || client.level == null) {
            currentTarget = null;
            return null;
        }

        if (!client.player.isUsingItem() || !client.player.getUseItem().is(Items.SPYGLASS)) {
            currentTarget = null;
            return null;
        }

        long now = System.currentTimeMillis();
        if (now - lastUpdateTime < 15 && currentTarget != null && currentTarget.isAlive()) {
            return currentTarget;
        }
        lastUpdateTime = now;

        Entity camera = client.getCameraEntity();
        if (camera == null) {
            camera = client.player;
        }

        Vec3 cameraPos = camera.getEyePosition(1.0F);
        Vec3 rotationVec = camera.getViewVector(1.0F);
        Vec3 endPos = cameraPos.add(rotationVec.x * maxDistance, rotationVec.y * maxDistance, rotationVec.z * maxDistance);

        AABB box = camera.getBoundingBox().expandTowards(rotationVec.scale(maxDistance)).inflate(1.0D, 1.0D, 1.0D);

        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
                camera,
                cameraPos,
                endPos,
                box,
                entity -> !entity.isSpectator() && entity.isPickable() && entity instanceof LivingEntity,
                maxDistance * maxDistance
        );

        if (entityHitResult != null && entityHitResult.getEntity() instanceof LivingEntity living) {
            currentTarget = living;
            return living;
        }

        currentTarget = null;
        return null;
    }

    public static boolean isCurrentTarget(Entity entity) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || !client.player.isUsingItem() || !client.player.getUseItem().is(Items.SPYGLASS)) {
            return false;
        }
        return currentTarget != null && currentTarget == entity;
    }

    public static int getTargetColor(Entity entity) {
        // Tamed Animals (Dogs, Cats, Horses, Parrots) -> Pink!
        if (entity instanceof TamableAnimal tameable && tameable.isTame()) {
            return 0xFF69B4; // Vibrant Pink (Tamed Pet)
        }
        if (entity instanceof AbstractHorse horse && horse.isTamed()) {
            return 0xFF69B4; // Vibrant Pink (Tamed Mount)
        }

        // Red = Aggressive (Monster or Angerable mob that is currently angry)
        if (entity instanceof NeutralMob neutral && neutral.isAngry()) {
            return 0xFF2222; // Vibrant Red
        }
        if (entity instanceof Monster) {
            return 0xFF2222; // Vibrant Red
        }

        // White = Neutral (Passive until attacked: Wolf, Bee, Iron Golem, Spider in daytime, Piglin, Enderman)
        if (entity instanceof NeutralMob || entity instanceof Spider) {
            return 0xFFFFFF; // Pure White
        }

        // Green = Passive (Pigs, Cows, Sheep, Chickens, Horses, Villagers)
        return 0x33FF44; // Vibrant Green
    }
}
