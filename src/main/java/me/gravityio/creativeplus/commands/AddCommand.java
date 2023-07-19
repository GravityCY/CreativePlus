package me.gravityio.creativeplus.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.gravityio.creativeplus.CreativePlus;
import me.gravityio.creativeplus.lib.helper.Helper;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.entity.player.PlayerEntity;

public class AddCommand {

    public static LiteralArgumentBuilder<FabricClientCommandSource> build() {

        var valueArg = ClientCommandManager.argument("value", StringArgumentType.greedyString())
                .executes(AddCommand::add);

        return ClientCommandManager.literal("add")
                .then(valueArg)
                .executes(AddCommand::empty);
    }

    private static int add(CommandContext<FabricClientCommandSource> context) {
        String value = context.getArgument("value", String.class);
        PlayerEntity player = context.getSource().getPlayer();
        if (player == null) return -1;
        if (CreativePlus.getModularCommands().add(value)) {
            player.sendMessage(Helper.literal("Added %s", value));
            return 1;
        }
        return 1;
    }

    private static int empty(CommandContext<FabricClientCommandSource> serverCommandSourceCommandContext) {

        return 1;
    }
}
