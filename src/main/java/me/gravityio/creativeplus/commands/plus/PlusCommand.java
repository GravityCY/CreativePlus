package me.gravityio.creativeplus.commands.plus;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.gravityio.creativeplus.commands.plus.sub.CallbackCommand;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class PlusCommand implements Command<ServerCommandSource> {
    private final CallbackCommand callbackCommand = new CallbackCommand();
    public final LiteralArgumentBuilder cmd = CommandManager
            .literal("plus")
            .then(callbackCommand.callbackCommand);

    @Override
    public int run(CommandContext context) {
        return 0;
    }
}
