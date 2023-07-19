package me.gravityio.creativeplus.api.nbt.frame;

import net.minecraft.block.BlockState;

/**
 * {@link net.minecraft.entity.vehicle.AbstractMinecartEntity#writeCustomDataToNbt}
 */
public interface AbstractMinecartFrame {
    boolean hasCustomDisplayState();
    BlockState getCustomDisplayState();
    int getDisplayOffset();

    void setHasCustomDisplayState(boolean hasCustomDisplayTile);
    void setCustomDisplayState(BlockState displayState);
    void setDisplayOffset(int displayOffset);
}
