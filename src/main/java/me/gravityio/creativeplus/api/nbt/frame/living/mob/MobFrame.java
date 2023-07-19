package me.gravityio.creativeplus.api.nbt.frame.living.mob;

import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * {@link net.minecraft.entity.mob.MobEntity#writeCustomDataToNbt}
 */
public interface MobFrame {

    boolean canPickupLoot();
    boolean isPersistent();
    boolean isLeftHanded();
    boolean isAiDisabled();
    List<ItemStack> getArmorItems();
    List<ItemStack> getHandItems();

    void setCanPickupLoot(boolean v);
    void setPersistent(boolean v);
    void setLeftHanded(boolean v);
    void setAiDisabled(boolean v);
    void setArmorItems(List<ItemStack> v);
    void setHandItems(List<ItemStack> v);

}
