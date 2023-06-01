package me.gravityio.creativeplus.commands.plus.sub.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.gravityio.creativeplus.CreativePlus;
import me.gravityio.creativeplus.commands.plus.sub.sub.sub.LoadCommand;
import me.gravityio.creativeplus.commands.plus.sub.sub.sub.SaveCommand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class PresetCommand implements Command<ServerCommandSource> {

    private final SaveCommand saveCommand = new SaveCommand();
    private final LoadCommand loadCommand = new LoadCommand();
    public final LiteralArgumentBuilder cmd = (LiteralArgumentBuilder) CommandManager
            .literal("preset")
            .then(saveCommand.cmd)
            .then(loadCommand.cmd)
            .executes(this);

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        PlayerEntity player = context.getSource().getPlayer();
        List<String> presets = CreativePlus.getAllPresets();
        if (presets.isEmpty()) {
            player.sendMessage(Text.of(Formatting.RED + "No Presets."));
            return 1;
        }
        player.sendMessage(Text.of("Presets: "));
        for (int i = 0; i < presets.size(); i++)
            player.sendMessage(Text.of("  Preset: " + (i + 1) + ": " + presets.get(i)));
        return 1;
    }
}
