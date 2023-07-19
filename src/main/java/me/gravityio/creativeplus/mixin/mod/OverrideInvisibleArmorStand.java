package me.gravityio.creativeplus.mixin.mod;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.gravityio.creativeplus.CreativeConfig;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Debug(export = true)

public class OverrideInvisibleArmorStand {

    @Debug(export = true)
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

    @Debug(export = true)
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
