package me.gravityio.creativeplus.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class PlusCommand {

    public static LiteralArgumentBuilder<FabricClientCommandSource> build() {
        return ClientCommandManager.literal("plus")
                .then(CallbackCommand.build())
                .executes(PlusCommand::empty);
    }

    private static int empty(CommandContext<FabricClientCommandSource> context) {

        return 1;
    }
}
