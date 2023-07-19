package me.gravityio.creativeplus.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.gravityio.creativeplus.CreativePlus;
import me.gravityio.creativeplus.lib.helper.Helper;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;

public class EditCommand {

    public static LiteralArgumentBuilder<FabricClientCommandSource> build() {

        var newValueArg = ClientCommandManager
                .argument("newValue", StringArgumentType.greedyString())
                .executes(EditCommand::edit);

        var indexArg = ClientCommandManager
                .argument("index", IntegerArgumentType.integer())
                .then(newValueArg)
                .executes(EditCommand::noNewValue);

        return ClientCommandManager.literal("edit")
                .then(indexArg)
                .executes(EditCommand::empty);
    }

    public static int edit(CommandContext<FabricClientCommandSource> context) {
        var current = CreativePlus.getModularCommands().getCurrent();

        int index = context.getArgument("index", Integer.class);
        PlayerEntity player = context.getSource().getPlayer();
        if (current.isEmpty()) {
            player.sendMessage(Helper.literal(Formatting.RED + "Nothing to Edit."));
            return 1;
        }

        if (index < 1 || index > current.size()) {
            player.sendMessage(Helper.literal(Formatting.RED + "Index must be between 1 and %d", current.size()));
            return 1;
        }


        String newValue = context.getArgument("newValue", String.class);
        current.set(index - 1, newValue);
        String display = newValue.length() > 20 ? newValue.substring(0, 20) + "..." : newValue;
        player.sendMessage(Helper.literal("Set Index %d to: '%s'", index, display));
        return 1;
    }


    private static int noNewValue(CommandContext<FabricClientCommandSource> context) {
        return 1;
    }

    private static int empty(CommandContext<FabricClientCommandSource> context) {
        return 1;
    }




}
