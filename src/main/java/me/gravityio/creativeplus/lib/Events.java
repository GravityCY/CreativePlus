package me.gravityio.creativeplus.lib;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;

public class Events {

    public static Event<OnKeyPressed> ON_KEY_PRESSED = EventFactory.createArrayBacked(OnKeyPressed.class,
        listeners -> (window, button, scancode, action, mods) -> {
            for (OnKeyPressed listener : listeners) {
                ActionResult result = listener.pressed(window, button, scancode, action, mods);
                if (result != ActionResult.PASS)
                    return result;
            }
            return ActionResult.PASS;
        });

    public static Event<OnMouseScrolled> ON_MOUSE_SCROLLED = EventFactory.createArrayBacked(OnMouseScrolled.class,
        listeners -> (window, horizontal, vertical) -> {
            for (OnMouseScrolled listener : listeners) {
                ActionResult result = listener.scroll(window, horizontal, vertical);
                if (result != ActionResult.PASS)
                    return result;
            }
            return ActionResult.PASS;
        });

    public static Event<OnMousePressed> ON_MOUSE_PRESSED = EventFactory.createArrayBacked(OnMousePressed.class,
        listeners -> (window, button, action, mods) -> {
            for (OnMousePressed listener : listeners) {
                ActionResult result = listener.pressed(window, button, action, mods);
                if (result != ActionResult.PASS)
                    return result;
            }
            return ActionResult.PASS;
        });

    public static Event<OnMousePressedAfter> ON_AFTER_MOUSE_PRESSED = EventFactory.createArrayBacked(OnMousePressedAfter.class,
            listeners -> (window, button, action, mods) -> {
                for (OnMousePressedAfter listener : listeners)
                    listener.pressed(window, button, action, mods);
            });

    public interface OnMouseScrolled {
        ActionResult scroll(long window, double horizontal, double vertical);
    }

    public interface OnMousePressed {
        ActionResult pressed(long window, int button, int action, int mods);
    }

    public interface OnMousePressedAfter {
        void pressed(long window, int button, int action, int mods);
    }

    public interface OnKeyPressed {
        ActionResult pressed(long window, int key, int scancode, int action, int mods);
    }
}
