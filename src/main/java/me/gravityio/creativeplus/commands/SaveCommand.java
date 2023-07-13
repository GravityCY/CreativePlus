package me.gravityio.creativeplus.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class SaveCommand {

    public static LiteralArgumentBuilder<FabricClientCommandSource> build() {
        var presetArg = ClientCommandManager.argument("presetName", StringArgumentType.word())
                .executes(SaveCommand::save);

        return ClientCommandManager.literal("save")
                .then(presetArg)
                .executes(SaveCommand::empty);
    }

    private static int empty(CommandContext<FabricClientCommandSource> serverCommandSourceCommandContext) {
        return 0;
    }

    private static int save(CommandContext<FabricClientCommandSource> serverCommandSourceCommandContext) {
        return 0;
    }
}