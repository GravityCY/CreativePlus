package me.gravityio.creativeplus.lib.idk;

import net.minecraft.util.ActionResult;

public interface DefaultInputListener extends ClientInputListener {

    @Override
    default void tick() {};

    @Override
    default ActionResult onMouseDelta(long window, double nx, double ny) { return ActionResult.PASS; }

    @Override
    default ActionResult onMouseScroll(long window, double horizontal, double vertical) { return ActionResult.PASS; }

    @Override
    default ActionResult onMouseButton(long window, int button, int action, int mods) { return ActionResult.PASS; }

    @Override
    default ActionResult onKey(long window, int key, int scancode, int action, int mods) { return ActionResult.PASS; }
}
