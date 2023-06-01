package me.gravityio.creativeplus.commands.plus.sub.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.gravityio.creativeplus.CreativePlus;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class RemoveCommand implements Command<ServerCommandSource> {

    private final RequiredArgumentBuilder<ServerCommandSource, Integer> indexArg = CommandManager
            .argument("index", IntegerArgumentType.integer())
            .executes(this);
    public final LiteralArgumentBuilder cmd = CommandManager
            .literal("remove")
            .then(indexArg)
            .executes(this::runNoArg);

    public int runNoArg(CommandContext<ServerCommandSource> context) {
        PlayerEntity player = context.getSource().getPlayer();
        player.sendMessage(Text.of("No Args."));
        return 1;
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        int index = context.getArgument("index", Integer.class);
        PlayerEntity player = context.getSource().getPlayer();
        if (CreativePlus.getCurrentPreset().isEmpty()) {
            player.sendMessage(Text.of(Formatting.RED + "Nothing to Remove."));
            return 1;
        }
        if (index < 1 || index > CreativePlus.getCurrentPreset().size()) {
            player.sendMessage(Text.of(Formatting.RED + "Indexing must be between 1 and " + CreativePlus.getCurrentPreset().size()));
            return 1;
        }
        player.sendMessage(Text.of("Removing Index " + index + " " + CreativePlus.getCurrentPreset().get(index - 1)));
        CreativePlus.getCurrentPreset().remove(index - 1);
        return 1;
    }
}
