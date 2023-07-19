package me.gravityio.creativeplus.api.nbt.frame.living;

import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * {@link net.minecraft.entity.decoration.ArmorStandEntity#writeCustomDataToNbt}
 */
public interface ArmorStandFrame {

    boolean isInvisible();
    boolean isSmall();
    boolean isMarker();
    boolean isBaseplateHidden();
    boolean canShowArms();
    int getDisabledSlots();
    List<ItemStack> getArmorItems();
    List<ItemStack> getHandItems();
//    getPose()?

    void setInvisible(boolean v);
    void setSmall(boolean v);
    void setMarker(boolean v);
    void setBaseplateHidden(boolean v);
    void setCanShowArms(boolean v);
    void setDisabledSlots(int v);
    void setArmorItems(List<ItemStack> v);
    void setHandItems(List<ItemStack> v);

}
