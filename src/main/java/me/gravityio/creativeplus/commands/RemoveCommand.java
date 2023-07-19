package me.gravityio.creativeplus.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.gravityio.creativeplus.CreativePlus;
import me.gravityio.creativeplus.lib.helper.Helper;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class RemoveCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> build() {
        var indexArg = ClientCommandManager
                .argument("index", IntegerArgumentType.integer())
                .executes(RemoveCommand::remove);

        return ClientCommandManager.literal("remove")
                .then(indexArg)
                .executes(RemoveCommand::empty);
    }

    private static int empty(CommandContext<FabricClientCommandSource> context) {
        PlayerEntity player = context.getSource().getPlayer();
        player.sendMessage(Text.of("No Args."));
        return 1;
    }

    private static int remove(CommandContext<FabricClientCommandSource> context) {
        var current = CreativePlus.getModularCommands().getCurrent();

        int index = context.getArgument("index", Integer.class);
        PlayerEntity player = context.getSource().getPlayer();
        if (current.isEmpty()) {
            player.sendMessage(Helper.literal(Formatting.RED + "Nothing to Remove."));
            return 1;
        }
        if (index < 1 || index > current.size()) {
            player.sendMessage(Helper.literal(Formatting.RED + "Indexing must be between '1' and '%d'",  current.size()));
            return 1;
        }
        player.sendMessage(Helper.literal("Removing Index '%d'u" + index + " " + current.get(index - 1)));
        current.remove(index - 1);
        return 1;
    }
}
