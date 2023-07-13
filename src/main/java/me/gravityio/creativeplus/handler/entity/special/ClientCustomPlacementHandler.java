package me.gravityio.creativeplus.handler.entity.special;

import me.gravityio.creativeplus.handler.entity.ClientEntityMovementHandler;
import me.gravityio.creativeplus.handler.entity.ClientEntityPlacementHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

/**
 * Replaces (kind of) the spawn egg and the armor stand items to use our custom placement handler
 */
public class ClientCustomPlacementHandler extends ClientEntityPlacementHandler {

    private ItemStack current = null;

    public ClientCustomPlacementHandler(MinecraftClient client) {
        super(client, null);
        super.onPlace(super::reset);
        super.onCancel(this::onDiscard);
    }

    @Override
    public void tick() {
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        ItemStack handStack = player.getMainHandStack();
        if (handStack == current) {
            onItemSame(handStack);
        } else {
            onItemChange(handStack);
        }
    }

    private void onItemSame(ItemStack same) {
        if (same == null || same.isEmpty() || super.entity == null) return;
        super.tick();
    }

    private void onItemChange(ItemStack newStack) {
        // Discard Previous
        onDiscard();
        if (newStack != null && !newStack.isEmpty()) {
            boolean isEgg = newStack.getItem() instanceof SpawnEggItem;
            boolean isArmorStand = newStack.getItem() == Items.ARMOR_STAND;

            if (isEgg || isArmorStand) {
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

                super.setEntityType(type);
                super.startPlacing(entityTag);
            }
        }
        this.current = newStack;
    }

    private void onDiscard() {
        super.discardNull();
        super.reset();
    }
}
