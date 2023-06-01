package me.gravityio.creativeplus.commands.plus.sub.sub.sub;

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
import net.minecraft.util.Formatting;

public class LoadCommand implements Command<ServerCommandSource> {

    private final RequiredArgumentBuilder<ServerCommandSource, String> presetArg = CommandManager
            .argument("presetName", StringArgumentType.word())
            .executes(this);
    public final LiteralArgumentBuilder cmd = CommandManager
            .literal("load")
            .then(presetArg);

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        String presetName = context.getArgument("presetName", String.class);
        PlayerEntity player = context.getSource().getPlayer();
        if (!CreativePlus.loadPreset(presetName)) {
            player.sendMessage(Text.of(Formatting.RED + "Preset doesn't exist."));
        } else {
            player.sendMessage(Text.of(presetName + " loaded."));
        }
        return 1;
    }
}
