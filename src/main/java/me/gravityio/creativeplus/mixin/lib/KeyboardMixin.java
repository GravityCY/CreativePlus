package me.gravityio.creativeplus.mixin.lib;

import me.gravityio.creativeplus.lib.idk.CreativeEvents;
import net.minecraft.client.Keyboard;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKeyAny(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (CreativeEvents.ON_KEY_PRESSED.invoker().onKey(window, key, scancode, action, modifiers) == ActionResult.FAIL)
            ci.cancel();
    }
}
