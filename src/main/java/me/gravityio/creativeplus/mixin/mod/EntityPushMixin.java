package me.gravityio.creativeplus.mixin.mod;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityPushMixin {
    @Shadow public World world;

    @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
    private void dontPushClientSideEntity(Entity entity, CallbackInfo ci) {
        if (!this.world.isClient || entity.getId() >= 0) return;
        ci.cancel();
    }
}
