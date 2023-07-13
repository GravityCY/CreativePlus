package me.gravityio.creativeplus.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.gravityio.creativeplus.CreativePlus;
import me.gravityio.creativeplus.lib.Helper;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CallbackCommand {
    public static LiteralArgumentBuilder<FabricClientCommandSource> build() {
        return ClientCommandManager.literal("callback")
                .then(AddCommand.build())
                .then(EditCommand.build())
                .then(RemoveCommand.build())
                .then(PresetCommand.build())
                .then(SetItemCommand.build())
                .executes(CallbackCommand::run);
    }

    private static int run(CommandContext<FabricClientCommandSource> context) {
        var current = CreativePlus.getModularCommands().getCurrent();

        PlayerEntity player = context.getSource().getPlayer();
        if (current.isEmpty()) {
            player.sendMessage(Helper.literal(Formatting.RED + "No Callbacks."));
            return 1;
        }

        player.sendMessage(Helper.literal("Callbacks: "));
        for (int i = 0; i < current.size(); i++) {
            player.sendMessage(Text.of((i + 1) + ": " + current.get(i)));
        }
        return 1;
    }
}
