package me.gravityio.creativeplus;

import me.gravityio.creativeplus.handler.entity.ClientEntityMovementHandler;
import me.gravityio.creativeplus.handler.entity.ClientEntityPlacementHandler;
import me.gravityio.creativeplus.lib.Helper;
import me.gravityio.creativeplus.lib.NbtHelper;
import me.gravityio.creativeplus.screen.EditEntityScreen;
import me.gravityio.creativeplus.screen.EntityMenuScreen;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class CreativeBinds {
    private static MinecraftClient CLIENT;
    private static final String OPEN_EDIT_MENU_KEY = String.format("%s.bind.open_edit_menu", CreativePlus.MOD_ID);
    private static final String OPEN_ENTITY_MENU_KEY = String.format("%s.bind.open_entity_menu", CreativePlus.MOD_ID);
    private static final String SHOW_INVISIBLE_KEY = String.format("%s.bind.show_invisible", CreativePlus.MOD_ID);
    private static final KeyBinding OPEN_EDIT_MENU_BIND = new KeyBinding(OPEN_EDIT_MENU_KEY, GLFW.GLFW_KEY_Y, CreativePlus.MOD_ID);
    private static final KeyBinding OPEN_ENTITY_MENU_BIND = new KeyBinding(OPEN_ENTITY_MENU_KEY, GLFW.GLFW_KEY_U, CreativePlus.MOD_ID);
    private static final KeyBinding SHOW_INVISIBLE_BIND = new KeyBinding(SHOW_INVISIBLE_KEY, GLFW.GLFW_KEY_KP_9, CreativePlus.MOD_ID);

    public static void setMinecraftClient(MinecraftClient client) {
        CreativeBinds.CLIENT = client;
    }

    public static void register() {
        KeyBindingHelper.registerKeyBinding(OPEN_ENTITY_MENU_BIND);
        KeyBindingHelper.registerKeyBinding(OPEN_EDIT_MENU_BIND);
        KeyBindingHelper.registerKeyBinding(SHOW_INVISIBLE_BIND);
    }

    public static void tick() {
        while (SHOW_INVISIBLE_BIND.wasPressed()) {
            CreativeConfig.SHOW_INVISIBLE = !CreativeConfig.SHOW_INVISIBLE;
            if (CreativeConfig.SHOW_INVISIBLE) {
                CLIENT.player.sendMessage(Text.translatable("message.creativeplus.config.show_invisible"), true);
            } else {
                CLIENT.player.sendMessage(Text.translatable("message.creativeplus.config.hide_invisible"), true);
            }
        }

        while (OPEN_EDIT_MENU_BIND.wasPressed()) {
            if (!CLIENT.player.hasPermissionLevel(2)) {
                CLIENT.player.sendMessage(Text.translatable("message.creativeplus.no_permissions"), true);
                continue;
            }

            Entity entity = Helper.getTargetedEntity(EntityPredicates.EXCEPT_SPECTATOR, 20);
            if (entity == null || ClientEntityMovementHandler.inUse) return;
            CLIENT.setScreen(new EditEntityScreen(entity));
        }

        while (OPEN_ENTITY_MENU_BIND.wasPressed()) {
            if (!CLIENT.player.hasPermissionLevel(2)) {
                CLIENT.player.sendMessage(Text.translatable("message.creativeplus.no_permissions"), true);
                continue;
            }

            CLIENT.setScreen(new EntityMenuScreen());
        }
    }

}
