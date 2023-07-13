package me.gravityio.creativeplus.mixin.mod;

import me.gravityio.creativeplus.CreativeConfig;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStandEntity.class)
public abstract class ArmorStandHitbox extends LivingEntity {
    @Shadow @Final private static EntityDimensions SMALL_DIMENSIONS;

    protected ArmorStandHitbox(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getDimensions(Lnet/minecraft/entity/EntityPose;)Lnet/minecraft/entity/EntityDimensions;", at = @At("HEAD"), cancellable = true)
    private void getCustomMarkerDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (!this.world.isClient || !CreativeConfig.SHOW_INVISIBLE) return;

        cir.setReturnValue(this.isBaby() ? SMALL_DIMENSIONS : this.getType().getDimensions());
    }
}
