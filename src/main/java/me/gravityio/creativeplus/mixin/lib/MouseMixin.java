package me.gravityio.creativeplus.mixin.lib;

import me.gravityio.creativeplus.lib.idk.CreativeEvents;
import net.minecraft.client.Mouse;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {

    @Shadow private double x;

    @Shadow private double y;

    @Inject(method = "onCursorPos", at = @At(value = "HEAD"), cancellable = true)
    private void onMouseMoveAny(long window, double x, double y, CallbackInfo ci) {
        boolean cancel = false;

        if (CreativeEvents.ON_MOUSE_MOVED.invoker().onMouseMoved(window, x, y) == ActionResult.FAIL) {
            cancel = true;
        }

        if (CreativeEvents.ON_MOUSE_DELTA.invoker().onMouseMoved(window, this.x - x, this.y - y) == ActionResult.FAIL) {
            cancel = true;
        }

        if (!cancel) return;
        this.x = x;
        this.y = y;
        ci.cancel();
    }

    @Inject(method = "onMouseScroll", at = @At(value = "HEAD"), cancellable = true)
    private void onScrollAny(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (CreativeEvents.ON_MOUSE_SCROLLED.invoker().onScroll(window, horizontal, vertical) == ActionResult.FAIL)
            ci.cancel();
    }

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMousePressedAny(long window, int button, int action, int mods, CallbackInfo ci) {
        if (CreativeEvents.ON_MOUSE_PRESSED.invoker().onMouseButton(window, button, action, mods) == ActionResult.FAIL)
            ci.cancel();
    }

    @Inject(method = "onMouseButton", at = @At("TAIL"))
    private void onMousePressedAnyAfter(long window, int button, int action, int mods, CallbackInfo ci) {
        CreativeEvents.ON_AFTER_MOUSE_PRESSED.invoker().onAfterMouseButton(window, button, action, mods);
    }

}
