package me.gravityio.creativeplus.mixin.mod;

import me.gravityio.creativeplus.CreativeConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class OverrideInvisibleArmorStand {


    @Shadow public World world;

    @Inject(method = "isInvisible", at = @At("RETURN"), cancellable = true)
    private void setVisibleArmorStand(CallbackInfoReturnable<Boolean> cir) {
        if (!this.world.isClient || !CreativeConfig.SHOW_INVISIBLE) return;

        Entity self = (Entity) (Object) this;
        if (self instanceof ArmorStandEntity) {
            cir.setReturnValue(false);
        }
    }

}
