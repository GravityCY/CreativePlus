package me.gravityio.creativeplus.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.gravityio.creativeplus.CreativePlus;
import me.gravityio.creativeplus.lib.helper.Helper;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;

public class LoadCommand {

    public static LiteralArgumentBuilder<FabricClientCommandSource> build() {
        var presetArg = ClientCommandManager.argument("presetName", StringArgumentType.word())
                .executes(LoadCommand::load);

        return ClientCommandManager.literal("load")
                .then(presetArg)
                .executes(LoadCommand::empty);
    }

    private static  int load(CommandContext<FabricClientCommandSource> context) {
        var commands = CreativePlus.getModularCommands();

        PlayerEntity player = context.getSource().getPlayer();
        String presetName = context.getArgument("presetName", String.class);
        if (!commands.setCurrentPreset(presetName)) {
            player.sendMessage(Helper.literal(Formatting.RED + "Preset doesn't exist."));
        } else {
            player.sendMessage(Helper.literal("Loaded '%s'", presetName));
        }
        return 1;
    }

    private static int empty(CommandContext<FabricClientCommandSource> serverCommandSourceCommandContext) {
        return 1;
    }
}
