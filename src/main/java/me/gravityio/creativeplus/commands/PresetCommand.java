package me.gravityio.creativeplus.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.gravityio.creativeplus.CreativePlus;
import me.gravityio.creativeplus.lib.helper.Helper;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;

import java.util.List;

public class PresetCommand {

    public static LiteralArgumentBuilder<FabricClientCommandSource> build() {
        return ClientCommandManager.literal("preset")
                .then(SaveCommand.build())
                .then(LoadCommand.build())
                .executes(PresetCommand::list);
    }

    private static int list(CommandContext<FabricClientCommandSource> context) {
        var commands = CreativePlus.getModularCommands();

        PlayerEntity player = context.getSource().getPlayer();
        List<String> presets = commands.getAllPresets();
        if (presets.isEmpty()) {
            player.sendMessage(Helper.literal(Formatting.RED + "No Presets."));
            return 1;
        }
        player.sendMessage(Helper.literal("Current Preset: "));
        for (int i = 0; i < presets.size(); i++)
            player.sendMessage(Helper.literal("  Command (%d): %s", i + 1, presets.get(i)));
        return 1;
    }
}
