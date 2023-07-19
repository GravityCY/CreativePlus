package me.gravityio.creativeplus.mixin.mod;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftClient.class)
public class OverrideGlowingRendering {
    @ModifyReturnValue(method = "hasOutline", at = @At("RETURN"))
    private boolean hasOutlineLocal(boolean original, Entity entity) {
        return entity.isGlowingLocal() || original;
    }
}
