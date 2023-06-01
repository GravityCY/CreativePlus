package me.gravityio.creativeplus.client;

import io.wispforest.owo.ui.parsing.UIParsing;
import me.gravityio.creativeplus.CreativePlus;
import me.gravityio.creativeplus.owogui.ButtonList;
import me.gravityio.creativeplus.client.screen.ArmorStandScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class CreativePlusClient implements ClientModInitializer {

    private static final String BIND_KEY = String.format("%s.bind.test", CreativePlus.MOD_ID);
    private static final String DEV_KEY = String.format("%s.bind.dev", CreativePlus.MOD_ID);
    private static final KeyBinding DEV = new KeyBinding(DEV_KEY, GLFW.GLFW_KEY_Y, CreativePlus.MOD_ID);
    private static final KeyBinding BIND = new KeyBinding(BIND_KEY, GLFW.GLFW_KEY_I, CreativePlus.MOD_ID);

    private static ArmorStandScreenHandler armorStandScreenHandler;
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> armorStandScreenHandler = new ArmorStandScreenHandler(client));
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> armorStandScreenHandler.tick());
        KeyBindingHelper.registerKeyBinding(BIND);
        KeyBindingHelper.registerKeyBinding(DEV);
        UIParsing.registerFactory("buttonlist", element -> new ButtonList());
    }

    public void tick(MinecraftClient client) {
        while (DEV.wasPressed()) {
            client.setScreen(new ArmorStandScreen(armorStandScreenHandler));
        }
        while (BIND.wasPressed()) {
            armorStandScreenHandler.create();
        }
//        armorStandScreenHandler.tick();
    }
}
