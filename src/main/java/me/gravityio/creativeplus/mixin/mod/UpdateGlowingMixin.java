package me.gravityio.creativeplus.mixin.mod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class UpdateGlowingMixin {

    @Shadow @Final protected static TrackedData<Byte> FLAGS;
    @Shadow protected abstract boolean getFlag(int index);
    @Shadow @Final protected static int GLOWING_FLAG_INDEX;
    @Shadow public abstract void setGlowing(boolean glowing);

    @Shadow public World world;

    @Inject(method = "onTrackedDataSet", at = @At("TAIL"))
    private void updateLocalGlowing(TrackedData<?> data, CallbackInfo ci) {
        if (data.equals(FLAGS)) {
            this.setGlowing(this.getFlag(GLOWING_FLAG_INDEX));
        }
    }
}
