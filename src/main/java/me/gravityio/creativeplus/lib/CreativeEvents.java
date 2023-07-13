package me.gravityio.creativeplus.lib;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * Some events needed for CreativePlus
 */
public class CreativeEvents {
    public static final Event<ClientReceiveMessageEvents.AllowGame> ON_ALLOW_GAME_MESSAGE =  ClientReceiveMessageEvents.ALLOW_GAME;
    public static Event<OnKeyPressed> ON_KEY_PRESSED = EventFactory.createArrayBacked(OnKeyPressed.class,
        listeners -> (window, button, scancode, action, mods) -> {
            for (OnKeyPressed listener : listeners) {
                ActionResult result = listener.onKey(window, button, scancode, action, mods);
                if (result != ActionResult.PASS)
                    return result;
            }
            return ActionResult.PASS;
        });

    public static Event<OnMouseScrolled> ON_MOUSE_SCROLLED = EventFactory.createArrayBacked(OnMouseScrolled.class,
        listeners -> (window, horizontal, vertical) -> {
            for (OnMouseScrolled listener : listeners) {
                ActionResult result = listener.onScroll(window, horizontal, vertical);
                if (result != ActionResult.PASS)
                    return result;
            }
            return ActionResult.PASS;
        });

    public static Event<OnMousePressed> ON_MOUSE_PRESSED = EventFactory.createArrayBacked(OnMousePressed.class,
        listeners -> (window, button, action, mods) -> {
            for (OnMousePressed listener : listeners) {
                ActionResult result = listener.onMouseButton(window, button, action, mods);
                if (result != ActionResult.PASS)
                    return result;
            }
            return ActionResult.PASS;
        });

    public static Event<OnMousePressedAfter> ON_AFTER_MOUSE_PRESSED = EventFactory.createArrayBacked(OnMousePressedAfter.class,
            listeners -> (window, button, action, mods) -> {
                for (OnMousePressedAfter listener : listeners)
                    listener.onAfterMouseButton(window, button, action, mods);
            });

    /**
     * Whenever a player uses an item
     */
    public static Event<OnItemUse> ON_ITEM_USE = EventFactory.createArrayBacked(OnItemUse.class,
            listeners -> (world, player, hand) -> {
                for (OnItemUse listener : listeners) {
                    TypedActionResult<ItemStack> result = listener.onUse(world, player, hand);
                    if (result != null && result.getResult() != ActionResult.PASS)
                        return result;
                }
                return null;
            });

    public static final Event<ServerTickEvents.StartTick> ON_SERVER_TICK = ServerTickEvents.START_SERVER_TICK;
    public static final Event<ServerTickEvents.EndTick> ON_SERVER_TICK_END = ServerTickEvents.END_SERVER_TICK;

    public static Event<ClientTickEvents.StartTick> ON_CLIENT_TICK = ClientTickEvents.START_CLIENT_TICK;
    public static Event<ClientTickEvents.EndTick> ON_CLIENT_TICK_END = ClientTickEvents.END_CLIENT_TICK;

    public interface OnMouseScrolled {
        ActionResult onScroll(long window, double horizontal, double vertical);
    }

    public interface OnMousePressed {
        ActionResult onMouseButton(long window, int button, int action, int mods);
    }

    public interface OnMousePressedAfter {
        void onAfterMouseButton(long window, int button, int action, int mods);
    }

    public interface OnKeyPressed {
        ActionResult onKey(long window, int key, int scancode, int action, int mods);
    }

    public interface OnItemUse {
        TypedActionResult<ItemStack> onUse(World world, PlayerEntity player, Hand hand);
    }
}
