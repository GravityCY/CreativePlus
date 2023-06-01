package me.gravityio.creativeplus.commands.plus.sub.sub.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.gravityio.creativeplus.CreativePlus;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SaveCommand implements Command<ServerCommandSource> {

    private final RequiredArgumentBuilder<ServerCommandSource, String> presetArg = CommandManager
            .argument("presetName", StringArgumentType.word())
            .executes(this);
    public final LiteralArgumentBuilder cmd = CommandManager
            .literal("save")
            .then(presetArg);

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        String presetName = context.getArgument("presetName", String.class);
        CreativePlus.savePreset(presetName);
        context.getSource().getPlayer().sendMessage(Text.of("Saved Current Preset as " + presetName));
        return 1;
    }
}