package me.gravityio.creativeplus.input;

import me.gravityio.creativeplus.CreativePlus;
import me.gravityio.creativeplus.api.placement.ClientEntitySummonHandler;
import me.gravityio.creativeplus.lib.idk.DefaultInputListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;

/**
 * Replaces (kind of) the spawn egg and the armor stand items to use our custom placement handler
 */
public class ItemEntityInputHandler implements DefaultInputListener {

    private final MinecraftClient client;
    private final ClientEntitySummonHandler summonHandler;
    private ItemStack current = null;

    public ItemEntityInputHandler(MinecraftClient client) {
        this.client = client;
        this.summonHandler = new ClientEntitySummonHandler(this.client);
        this.summonHandler.setResetOnPlace(false);
        this.summonHandler.onCancelCallback(output -> CreativePlus.unsetCurrentHandler());
    }

    @Override
    public void tick() {
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        ItemStack handStack = player.getMainHandStack();
        if (handStack == this.current) return;
        onItemChange(handStack);
    }

    private void onItemChange(ItemStack newStack) {
        if (summonHandler.isActive()) {
            summonHandler.reset();
        }

        if (newStack != null && !newStack.isEmpty()) {
            Item item = newStack.getItem();
            boolean isEgg = item instanceof SpawnEggItem;
            boolean isArmorStand = item == Items.ARMOR_STAND;

            if (!isEgg && !isArmorStand) return;

            NbtCompound nbt = newStack.getNbt();
            NbtCompound entityTag = new NbtCompound();
            if (nbt != null)
                entityTag = nbt.getCompound(EntityType.ENTITY_TAG_KEY);

            EntityType<? extends Entity> type;
            if (isEgg) {
                type = ((SpawnEggItem) newStack.getItem()).getEntityType(nbt);
            } else {
                type = EntityType.ARMOR_STAND;
            }

            summonHandler.setupEntity(type, entityTag);
            CreativePlus.setCurrentHandler(summonHandler);
            summonHandler.start();
        } else {
            if (CreativePlus.getCurrentHandler() == summonHandler) {
                CreativePlus.unsetCurrentHandler();
            }
        }
        this.current = newStack;
    }
}
