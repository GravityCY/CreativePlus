package me.gravityio.creativeplus.handler;

import me.gravityio.creativeplus.CreativePlus;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

/**
 * A Handler for when the player clicks an assigned item runs the callback commands.
 */
public class CallbackItemHandler {

    private Item callbackItem;

    public void setCallbackItem(Item callbackItem) {
        this.callbackItem = callbackItem;
    }

    public TypedActionResult<ItemStack> onItemUse(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient) return null;

        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() != callbackItem) return null;
        ClientPlayerEntity client = (ClientPlayerEntity) player;
        CreativePlus.getModularCommands().run(client);
        return TypedActionResult.success(player.getStackInHand(hand), true);
    }

}
