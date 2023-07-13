package me.gravityio.creativeplus;

import io.wispforest.owo.ui.parsing.UIParsing;
import me.gravityio.creativeplus.commands.PlusCommand;
import me.gravityio.creativeplus.gui.ButtonList;
import me.gravityio.creativeplus.handler.CallbackItemHandler;
import me.gravityio.creativeplus.handler.entity.special.ClientMoveEntityHandler;
import me.gravityio.creativeplus.handler.entity.special.ClientCustomPlacementHandler;
import me.gravityio.creativeplus.lib.ClientInputListenerRegistry;
import me.gravityio.creativeplus.lib.ClientServerCommunication;
import me.gravityio.creativeplus.lib.CreativeEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreativePlus implements ClientModInitializer {
    public static final String MOD_ID = "creativeplus";
    public static final Logger LOGGER = LoggerFactory.getLogger(CreativePlus.MOD_ID);

    public static MinecraftClient CLIENT;
    private static final CallbackItemHandler CALLBACK_HANDLER = new CallbackItemHandler();
    private static final ModularCommands MODULAR_COMMANDS = new ModularCommands();

    public static ModularCommands getModularCommands() {
        return CreativePlus.MODULAR_COMMANDS;
    }

    public static CallbackItemHandler getCallbackHandler() {
        return CreativePlus.CALLBACK_HANDLER;
    }

    public static void command(String command) {
        CreativePlus.LOGGER.debug("[CreativePlus] Sending Command: '%s'".formatted(command));
        CLIENT.getNetworkHandler().sendCommand(command);
    }

    @Override
    public void onInitializeClient() {
        CreativePlus.CLIENT = MinecraftClient.getInstance();

        UIParsing.registerFactory("buttonlist", element -> new ButtonList());

        registerHandlers();
        ClientServerCommunication.setMinecraftClient(CLIENT);
        CreativeBinds.setMinecraftClient(CLIENT);
        CreativeBinds.register();
        registerCommands();
        registerEvents();
    }

    private void registerHandlers() {
        ClientInputListenerRegistry.add("spawn_egg", new ClientCustomPlacementHandler(CLIENT));
        ClientInputListenerRegistry.add("move_entity", new ClientMoveEntityHandler(CLIENT));
    }


    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(PlusCommand.build()));
    }

    private void registerEvents() {
        CreativeEvents.ON_KEY_PRESSED.register(ClientInputListenerRegistry::onKey);
        CreativeEvents.ON_MOUSE_PRESSED.register(ClientInputListenerRegistry::onMouseButton);
        CreativeEvents.ON_MOUSE_SCROLLED.register(ClientInputListenerRegistry::onMouseScroll);
        CreativeEvents.ON_CLIENT_TICK_END.register(this::onTick);
        CreativeEvents.ON_ITEM_USE.register(CALLBACK_HANDLER::onItemUse);
        CreativeEvents.ON_ALLOW_GAME_MESSAGE.register((text, overlay) -> {
            if (overlay) return true;
            return ClientServerCommunication.onCommand(text);
        });
    }

    private void onTick(MinecraftClient c) {
        CreativeBinds.tick();
        ClientInputListenerRegistry.onTick();
    }



}
