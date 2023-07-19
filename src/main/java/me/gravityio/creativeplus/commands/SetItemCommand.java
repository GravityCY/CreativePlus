package me.gravityio.creativeplus.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.gravityio.creativeplus.CreativePlus;
import me.gravityio.creativeplus.lib.helper.Helper;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.argument.RegistryKeyArgumentType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Formatting;

public class SetItemCommand {

    public static LiteralArgumentBuilder<FabricClientCommandSource> build() {

        var idArg = ClientCommandManager.argument("item", RegistryKeyArgumentType.registryKey(RegistryKeys.ITEM))
                .executes(SetItemCommand::setItem);

        return ClientCommandManager.literal("item")
                .then(idArg)
                .executes(SetItemCommand::setHeldItem);
    }

    private static int setHeldItem(CommandContext<FabricClientCommandSource> context) {
        return 1;
    }

    private static int setItem(CommandContext<FabricClientCommandSource> context) {
        ClientPlayerEntity player = context.getSource().getPlayer();
        RegistryKey<Item> itemKey = context.getArgument("item", RegistryKey.class);
        Item item =  Registries.ITEM.get(itemKey);
        if (item == null) {
            player.sendMessage(Helper.literal(Formatting.RED + "No Such Item!"));
            return 1;
        }

        CreativePlus.getCallbackHandler().setCallbackItem(item);
        player.sendMessage(Helper.literal(Formatting.GREEN + "Set Callback Item to %s", itemKey.getValue().toString()));
        return 1;
    }
}
