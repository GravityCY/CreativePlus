package me.gravityio.creativeplus;

import me.gravityio.creativeplus.input.BindEditHelper;
import me.gravityio.creativeplus.screen.EntityMenuScreen;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class CreativeBinds {
    private static MinecraftClient client;
    private static final String CATEGORY = "key.category." + CreativePlus.MOD_ID;
    private static final String OPEN_EDIT_MENU_KEY = String.format("bind.%s.open_edit_menu", CreativePlus.MOD_ID);
    private static final KeyBinding OPEN_EDIT_MENU_BIND = new KeyBinding(OPEN_EDIT_MENU_KEY, GLFW.GLFW_KEY_Y, CATEGORY);
    private static final String OPEN_ENTITY_MENU_KEY = String.format("bind.%s.open_entity_menu", CreativePlus.MOD_ID);
    private static final KeyBinding OPEN_ENTITY_MENU_BIND = new KeyBinding(OPEN_ENTITY_MENU_KEY, GLFW.GLFW_KEY_U, CATEGORY);
    private static final String SHOW_INVISIBLE_KEY = String.format("bind.%s.show_armor_stands", CreativePlus.MOD_ID);
    private static final KeyBinding SHOW_INVISIBLE_BIND = new KeyBinding(SHOW_INVISIBLE_KEY, GLFW.GLFW_KEY_KP_9, CATEGORY);
    private static final String SHOW_BLOCK_DISPLAY_KEY = String.format("bind.%s.show_display_entities", CreativePlus.MOD_ID);
    private static final KeyBinding SHOW_BLOCK_DISPLAY_BIND = new KeyBinding(SHOW_BLOCK_DISPLAY_KEY, GLFW.GLFW_KEY_KP_8, CATEGORY);


    public static void setMinecraftClient(MinecraftClient client) {
        CreativeBinds.client = client;
    }

    public static void register() {
        KeyBindingHelper.registerKeyBinding(OPEN_ENTITY_MENU_BIND);
        KeyBindingHelper.registerKeyBinding(OPEN_EDIT_MENU_BIND);
        KeyBindingHelper.registerKeyBinding(SHOW_INVISIBLE_BIND);
        KeyBindingHelper.registerKeyBinding(SHOW_BLOCK_DISPLAY_BIND);
    }

    public static void tick() {

        while (SHOW_BLOCK_DISPLAY_BIND.wasPressed()) {
            CreativeConfig.SHOW_DISPLAY_ENTITY_HITBOX = !CreativeConfig.SHOW_DISPLAY_ENTITY_HITBOX;
            if (CreativeConfig.SHOW_DISPLAY_ENTITY_HITBOX) {
                client.player.sendMessage(Text.translatable("message.creativeplus.config.show_display_entity"), true);
            } else {
                client.player.sendMessage(Text.translatable("message.creativeplus.config.hide_display_entity"), true);
            }
        }

        while (SHOW_INVISIBLE_BIND.wasPressed()) {
            CreativeConfig.SHOW_INVISIBLE = !CreativeConfig.SHOW_INVISIBLE;
            if (CreativeConfig.SHOW_INVISIBLE) {
                client.player.sendMessage(Text.translatable("message.creativeplus.config.show_invisible"), true);
            } else {
                client.player.sendMessage(Text.translatable("message.creativeplus.config.hide_invisible"), true);
            }
        }

        while (OPEN_EDIT_MENU_BIND.wasPressed()) {
            BindEditHelper.onEditKeyPressed(client);
        }

        while (OPEN_ENTITY_MENU_BIND.wasPressed()) {
            if (!client.player.hasPermissionLevel(2)) {
                client.player.sendMessage(Text.translatable("message.creativeplus.no_permissions"), true);
                continue;
            }

            client.setScreen(new EntityMenuScreen());
        }
    }

}
