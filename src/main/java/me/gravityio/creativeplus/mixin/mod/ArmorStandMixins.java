package me.gravityio.creativeplus.mixin.mod;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.gravityio.creativeplus.CreativeConfig;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.*;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public abstract class ArmorStandMixins  {


    /**
     * Makes it so that if the config to make Armor Stands 'invisible'
     * also set's their hitbox to always be either of the baby's or the normal ones.
     * So that it doesn't check if the Armor Stand is a marker.
     */
    @Mixin(ArmorStandEntity.class)
    public static abstract class CustomDimensionsMixin extends LivingEntity {
        @Shadow @Final private static EntityDimensions SMALL_DIMENSIONS;

        protected CustomDimensionsMixin(EntityType<? extends LivingEntity> entityType, World world) {
            super(entityType, world);
        }

        @ModifyReturnValue(method = "getDimensions(Lnet/minecraft/entity/EntityPose;)Lnet/minecraft/entity/EntityDimensions;", at = @At("RETURN"))
        private EntityDimensions getCustomMarkerDimensions(EntityDimensions original) {
            if (!this.world.isClient || !CreativeConfig.SHOW_INVISIBLE) return original;

            return this.isBaby() ? SMALL_DIMENSIONS : this.getType().getDimensions();
        }
    }

    /**
     * Makes it so that if the config to make Armor Stands
     * 'invisible' makes the renderer always render the invisible Armor Stands
     */
    @Mixin(LivingEntityRenderer.class)
    public static class VisibilityMixin<T extends LivingEntity, M extends EntityModel<T>> {
        @ModifyReturnValue(method = "isVisible", at = @At("RETURN"))
        private boolean setVisibleArmorStand(boolean original, T entity) {
            if (entity instanceof ArmorStandEntity) {
                return CreativeConfig.SHOW_INVISIBLE || original;
            }
            return original;
        }
    }

    /**
     * Makes it so that if the config to make Armor Stands
     * 'invisible' it also renders its hitbox normally if F3 + B is enabled <br>
     * Usually doesn't render invisible hitboxes, which is why we have to do this. <br>
     * We never actually say the armor stand is invisible, we just render it visible.
     */
    @Mixin(EntityRenderDispatcher.class)
    public static class HitboxMixin {
        @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isInvisible()Z", ordinal = 1))
        private <E extends Entity> boolean setVisibleArmorStand(boolean original, E entity) {
            if (entity instanceof ArmorStandEntity && CreativeConfig.SHOW_INVISIBLE) {
                return false;
            }
            return original;
        }
    }
}
