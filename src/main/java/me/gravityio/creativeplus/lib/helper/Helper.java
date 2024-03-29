package me.gravityio.creativeplus.lib.helper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;


/**
 * A Generic Helper class
 */
public class Helper {

    public static Text literal(String literal, Object... args) {
        return Text.literal(literal.formatted(args));
    }

    public static Text translatable(String key, Object... args) {
        return Text.translatable(key, args);
    }

    public static List<EntityType<?>> getOrderedEntities(MinecraftClient client) {
        List<EntityType<?>> list = new ArrayList<>();
        for (Map.Entry<RegistryKey<EntityType<?>>, EntityType<?>> entry : Registries.ENTITY_TYPE.getEntrySet()) {
            EntityType<? extends Entity> type = entry.getValue();
            if (type.create(client.world) == null) continue;
            list.add(type);
        }
        list.sort((a, b) -> {
            int ao = a.getSpawnGroup().ordinal();
            int bo = b.getSpawnGroup().ordinal();
            return ao - bo;
        });
        return list;
    }

    public static HitResult raycast(MinecraftClient client, float maxDistance, boolean includeFluids) {
        return client.cameraEntity.raycast(maxDistance, 1, includeFluids);
    }

    public static void addParticle(World world, ParticleEffect particle, Vec3d vec) {
        addParticle(world, particle, vec, 0, 0, 0);
    }

    public static void addParticle(World world, ParticleEffect particle, Vec3d vec, double vx, double vy, double vz) {
        world.addParticle(particle, vec.x, vec.y, vec.z, vx, vy, vz);
    }

    public static boolean rayIntersectsAABB(Vec3d rayOrigin, Vec3d rayDirection, Box box) {
        double t1 = (box.minX - rayOrigin.x) / rayDirection.x;
        double t2 = (box.maxX - rayOrigin.x) / rayDirection.x;

        double t3 = (box.minY - rayOrigin.y) / rayDirection.y;
        double t4 = (box.maxY - rayOrigin.y) / rayDirection.y;

        double t5 = (box.minZ - rayOrigin.z) / rayDirection.z;
        double t6 = (box.maxZ - rayOrigin.z) / rayDirection.z;

        double tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        double tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        return tmax >= 0 && tmin <= tmax;
    }

    public static boolean isLookingAt(Box box, Entity entity) {
        Vec3d start = entity.getCameraPosVec(1f);
        Vec3d end = entity.getRotationVec(1f);

        return rayIntersectsAABB(start, end, box);
    }

    public static @Nullable Entity getTargetedEntity(Predicate<Entity> predicate, float reach) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.cameraEntity == null) return null;
        if (client.world == null) return null;
        Entity camera = client.cameraEntity;

        Vec3d start = camera.getCameraPosVec(1f);
        Vec3d rotVec = camera.getRotationVec(1f);

        double distance;
        HitResult hit = camera.raycast(reach, 1, false);
        if (hit != null) {
            distance = hit.getPos().squaredDistanceTo(start);
        } else {
            distance = reach;
            distance *= distance;
        }

        Vec3d end = start.add(rotVec.x * reach, rotVec.y * reach, rotVec.z * reach);
        Box box = camera.getBoundingBox().stretch(rotVec.multiply(reach)).expand(1.0 ,1.0, 1.0);

        EntityHitResult entityHitResult = ProjectileUtil.raycast(camera, start, end, box, predicate, distance);
        return entityHitResult == null ? null : entityHitResult.getEntity();
    }

}
