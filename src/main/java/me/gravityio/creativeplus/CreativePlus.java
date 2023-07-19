package me.gravityio.creativeplus;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import io.wispforest.owo.ui.parsing.UIParsing;
import me.gravityio.creativeplus.api.placement.EntityMovementHandler;
import me.gravityio.creativeplus.commands.PlusCommand;
import me.gravityio.creativeplus.gui.ButtonList;
import me.gravityio.creativeplus.input.CallbackItemInputHandler;
import me.gravityio.creativeplus.input.ClientItemEntityInputHandler;
import me.gravityio.creativeplus.input.ClientMoveEntityInputHandler;
import me.gravityio.creativeplus.lib.idk.ClientInputListenerRegistry;
import me.gravityio.creativeplus.lib.idk.ClientServerCommunication;
import me.gravityio.creativeplus.lib.idk.CreativeEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreativePlus implements ClientModInitializer, PreLaunchEntrypoint {
    public static final String MOD_ID = "creativeplus";
    public static final Logger LOGGER = LoggerFactory.getLogger(CreativePlus.MOD_ID);
    private static final CallbackItemInputHandler CALLBACK_HANDLER = new CallbackItemInputHandler();
    private static final ModularCommands MODULAR_COMMANDS = new ModularCommands();
    private static boolean shouldRemoveHandler = false;
    private static EntityMovementHandler currentHandler;
    public static MinecraftClient client;

    public static ModularCommands getModularCommands() {
        return CreativePlus.MODULAR_COMMANDS;
    }

    public static CallbackItemInputHandler getCallbackHandler() {
        return CreativePlus.CALLBACK_HANDLER;
    }

    public static void setCurrentHandler(@Nullable EntityMovementHandler handler) {
        shouldRemoveHandler = CreativePlus.currentHandler != handler;
        CreativePlus.currentHandler = handler;
    }

    public static void unsetCurrentHandler() {
        setCurrentHandler(null);
    }

    public static EntityMovementHandler getCurrentHandler() {
        return CreativePlus.currentHandler;
    }

    public static boolean hasCurrentHandler() {
        return getCurrentHandler() != null;
    }

    public static void command(String command) {
        CreativePlus.LOGGER.debug("[CreativePlus] Sending Command: '%s'".formatted(command));
        client.getNetworkHandler().sendCommand(command);
    }

    @Override
    public void onPreLaunch() {
        MixinExtrasBootstrap.init();
    }

    @Override
    public void onInitializeClient() {
        CreativePlus.client = MinecraftClient.getInstance();

        UIParsing.registerFactory("buttonlist", element -> new ButtonList());

        registerHandlers();
        ClientServerCommunication.setMinecraftClient(client);
        CreativeBinds.setMinecraftClient(client);
        CreativeBinds.register();
        registerCommands();
        registerEvents();
    }

    private void registerHandlers() {
        ClientInputListenerRegistry.set("spawn_egg", new ClientItemEntityInputHandler(client));
        ClientInputListenerRegistry.set("move_entity", new ClientMoveEntityInputHandler(client));
    }

    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(PlusCommand.build()));
    }

    private void registerEvents() {
        CreativeEvents.ON_KEY_PRESSED.register(ClientInputListenerRegistry::onKey);
        CreativeEvents.ON_MOUSE_PRESSED.register(ClientInputListenerRegistry::onMouseButton);
        CreativeEvents.ON_MOUSE_SCROLLED.register(ClientInputListenerRegistry::onMouseScroll);
        CreativeEvents.ON_MOUSE_DELTA.register(ClientInputListenerRegistry::onMouseDelta);
        CreativeEvents.ON_CLIENT_TICK_END.register(this::onTick);
        CreativeEvents.ON_ITEM_USE.register(CALLBACK_HANDLER::onItemUse);
        CreativeEvents.ON_ALLOW_GAME_MESSAGE.register((text, overlay) -> {
            if (overlay) return true;
            return ClientServerCommunication.onCommand(text);
        });
    }

    private void onTick(MinecraftClient c) {
        if (shouldRemoveHandler) {
            ClientInputListenerRegistry.remove("handler");
            shouldRemoveHandler = false;
        } else if (currentHandler != null) {
            ClientInputListenerRegistry.set("handler", currentHandler);
        }

        CreativeBinds.tick();
        ClientInputListenerRegistry.onTick();
    }

}
