package me.gravityio.creativeplus.commands.plus.sub.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.gravityio.creativeplus.CreativePlus;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class AddCommand implements Command<ServerCommandSource> {

    private final RequiredArgumentBuilder<ServerCommandSource, String> valueArg = CommandManager
            .argument("value", StringArgumentType.greedyString())
            .executes(this);
    public final LiteralArgumentBuilder<ServerCommandSource> cmd = CommandManager
            .literal("add")
            .then(valueArg)
            .executes(this::runEmpty);

    private int runEmpty(CommandContext<ServerCommandSource> serverCommandSourceCommandContext) {

        return 1;
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        String value = context.getArgument("value", String.class);
        PlayerEntity player = context.getSource().getPlayer();
        if (player == null) return -1;
        player.sendMessage(Text.of("Added " + value));
        CreativePlus.getCurrentPreset().add(value);
        return 1;
    }
}
