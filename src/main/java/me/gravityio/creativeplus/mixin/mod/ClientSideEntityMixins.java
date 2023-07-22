package me.gravityio.creativeplus.mixin.mod;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public abstract class ClientSideEntityMixins {

    /**
     * Entity Collisions happen on the client side, so we need to disable entities pushing the player
     * if they're the ones we spawn manually on the client when we move entities etc.
     */
    @Mixin(Entity.class)
    public static abstract class ClientCollisionMixin {
        @Shadow public World world;
        @Shadow public abstract Vec3d getPos();
        @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
        private void dontPushClientSideEntity(Entity entity, CallbackInfo ci) {
            if (!this.world.isClient || entity.getId() >= 0) return;
            ci.cancel();
        }
    }

    /**
     * Whenever the server updates the glowing of an entity, we also
     * set the local glowing of the client. So it syncs up. <br><br>
     *
     * Actually need to redesign this, this is not actually the behaviour we want, I fudged up
     */
    // TODO: Make a better way to do this
    @Mixin(Entity.class)
    public static abstract class ClientGlowingMixin {
        @Shadow @Final protected static TrackedData<Byte> FLAGS;
        @Shadow private boolean glowing;
        @Shadow public abstract boolean isGlowing();

        @Inject(method = "onTrackedDataSet", at = @At("TAIL"))
        private void updateLocalGlowing(TrackedData<?> data, CallbackInfo ci) {
            if (data.equals(FLAGS)) {
                this.glowing = this.isGlowing();
            }
        }
    }

    /**
     * We render the outline of entities depending on if they're local glowing
     * is true, rather than prioritizing the data tracked value. <br><br>
     * Makes it so that the client can render the glowing independent of the server.
     */
    @Mixin(MinecraftClient.class)
    public static class ClientGlowRenderMixin {
        @ModifyReturnValue(method = "hasOutline", at = @At("RETURN"))
        private boolean hasOutlineLocal(boolean original, Entity entity) {
            return entity.isGlowingLocal() || original;
        }
    }

}
