package me.gravityio.creativeplus.commands.plus.sub.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.gravityio.creativeplus.CreativePlus;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class EditCommand implements Command<ServerCommandSource> {

    private final RequiredArgumentBuilder<ServerCommandSource, String> newValueArg = CommandManager
            .argument("newValue", StringArgumentType.greedyString())
            .executes(this);
    private final RequiredArgumentBuilder<ServerCommandSource, Integer> indexArg = CommandManager
            .argument("index", IntegerArgumentType.integer())
            .then(newValueArg);
    public final LiteralArgumentBuilder cmd = CommandManager
            .literal("edit")
            .then(indexArg);

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        int index = context.getArgument("index", Integer.class);
        PlayerEntity player = context.getSource().getPlayer();
        if (CreativePlus.getCurrentPreset().isEmpty()) {
            player.sendMessage(Text.of(Formatting.RED + "Nothing to Edit."));
            return 1;
        }
        if (index < 1 || index > CreativePlus.getCurrentPreset().size()) {
            player.sendMessage(Text.of(Formatting.RED + "Index must be between 1 and " + CreativePlus.getCurrentPreset().size()));
            return 1;
        }
        String newValue = context.getArgument("newValue", String.class);
        CreativePlus.getCurrentPreset().set(index - 1, newValue);
        player.sendMessage(Text.of("Set Index " + index + " to: " + newValue));
        return 1;
    }
}
