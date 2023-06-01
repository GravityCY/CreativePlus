package me.gravityio.creativeplus.commands.plus.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.gravityio.creativeplus.CreativePlus;
import me.gravityio.creativeplus.commands.plus.sub.sub.AddCommand;
import me.gravityio.creativeplus.commands.plus.sub.sub.EditCommand;
import me.gravityio.creativeplus.commands.plus.sub.sub.PresetCommand;
import me.gravityio.creativeplus.commands.plus.sub.sub.RemoveCommand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CallbackCommand implements Command<ServerCommandSource> {

    private final AddCommand addCommand = new AddCommand();
    private final RemoveCommand removeCommand = new RemoveCommand();
    private final EditCommand editCommand = new EditCommand();
    private final PresetCommand presetCommand = new PresetCommand();
    public final LiteralArgumentBuilder callbackCommand = (LiteralArgumentBuilder) CommandManager
            .literal("callback")
            .then(addCommand.cmd)
            .then(editCommand.cmd)
            .then(removeCommand.cmd)
            .then(presetCommand.cmd)
            .executes(this);

    @Override
    public int run(CommandContext<ServerCommandSource> context) {
        PlayerEntity player = context.getSource().getPlayer();
        if (CreativePlus.getCurrentPreset().isEmpty()) {
            player.sendMessage(Text.of(Formatting.RED + "No Callbacks."));
            return 1;
        }
        player.sendMessage(Text.of("Callbacks: "));
        for (int i = 0; i < CreativePlus.getCurrentPreset().size(); i++)
            player.sendMessage(Text.of((i + 1) + ": " + CreativePlus.getCurrentPreset().get(i)));
        return 1;
    }
}
