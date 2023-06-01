package me.gravityio.creativeplus;

import me.gravityio.creativeplus.commands.plus.PlusCommand;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class Commands {

    private static final PlusCommand plusCommand = new PlusCommand();

    public static void onInitialize() {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> dispatcher.register(plusCommand.cmd)
        );
    }

}
