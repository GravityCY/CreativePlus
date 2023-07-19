package me.gravityio.creativeplus.lib.idk;

import net.minecraft.util.ActionResult;

public interface ClientInputListener {
  void tick();
  ActionResult onMouseDelta(long window, double nx, double ny);
  ActionResult onMouseScroll(long window, double horizontal, double vertical);
  ActionResult onMouseButton(long window, int button, int action, int mods);
  ActionResult onKey(long window, int key, int scancode, int action, int mods);
}
