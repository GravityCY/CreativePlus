package me.gravityio.creativeplus.commands.plus.sub;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

public class PrecisionCommand implements Command<ServerCommandSource> {


    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return 0;
    }
}