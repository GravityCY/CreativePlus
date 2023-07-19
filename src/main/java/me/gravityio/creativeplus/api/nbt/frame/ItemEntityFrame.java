package me.gravityio.creativeplus.api.nbt.frame;

import net.minecraft.item.ItemStack;

import java.util.UUID;

/**
 * {@link net.minecraft.entity.ItemEntity#writeCustomDataToNbt}
 */
public interface ItemEntityFrame {
    short getHealth();
    short getAge();
    short getPickupDelay();
    UUID getThrower();
    UUID getOwner();
    ItemStack getItem();

    void setHealth(short health);
    void setAge(short age);
    void setPickupDelay(short pickupDelay);
    void setThrower(UUID thrower);
    void setOwner(UUID owner);
    void setItem(ItemStack item);
}
