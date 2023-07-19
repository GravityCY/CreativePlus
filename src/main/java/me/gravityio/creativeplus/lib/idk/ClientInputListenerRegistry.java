package me.gravityio.creativeplus.lib.idk;

import net.minecraft.util.ActionResult;

import java.util.HashMap;
import java.util.Map;

/**
 * A Registry for everything that needs to listen to client input events and getting ticked
 */
public class ClientInputListenerRegistry {

    private static final Map<Object, ClientInputListener> clientInputListeners = new HashMap<>();


    public static void set(Object object, ClientInputListener listener) {
        clientInputListeners.put(object, listener);
    }

    public static ClientInputListener get(Object object) {
        return clientInputListeners.get(object);
    }

    public static void remove(Object object) {
        clientInputListeners.remove(object);
    }

    public static ActionResult onKey(long window, int key, int scancode, int action, int mods) {
        for (ClientInputListener value : clientInputListeners.values()) {
            ActionResult result = value.onKey(window, key, scancode, action, mods);
            if (result != ActionResult.PASS)
                return result;
        }
        return ActionResult.PASS;
    }

    public static ActionResult onMouseDelta(long window, double nx, double ny) {
        for (ClientInputListener value : clientInputListeners.values()) {
            ActionResult result = value.onMouseDelta(window, nx, ny);
            if (result != ActionResult.PASS)
                return result;
        }
        return ActionResult.PASS;
    }

    public static ActionResult onMouseButton(long window, int button, int action, int mods) {
        for (ClientInputListener value : clientInputListeners.values()) {
            ActionResult result = value.onMouseButton(window, button, action, mods);
            if (result != ActionResult.PASS)
                return result;
        }
        return ActionResult.PASS;
    }

    public static ActionResult onMouseScroll(long window, double horizontal, double vertical) {
        for (ClientInputListener value : clientInputListeners.values()) {
            ActionResult result = value.onMouseScroll(window, horizontal, vertical);
            if (result != ActionResult.PASS)
                return result;
        }
        return ActionResult.PASS;
    }

    public static void onTick() {
        for (ClientInputListener value : clientInputListeners.values()) {
            value.tick();
        }
    }
}
