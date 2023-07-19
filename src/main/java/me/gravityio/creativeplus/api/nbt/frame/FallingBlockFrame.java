package me.gravityio.creativeplus.api.nbt.frame;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;

/**
 * {@link net.minecraft.entity.FallingBlockEntity#writeCustomDataToNbt}
 */
public interface FallingBlockFrame {
    BlockState getBlockState();
    int getTime();
    boolean canDropItem();
    boolean canHurtEntities();
    float getFallHurtAmount();
    float getFallHurtMax();
    NbtCompound getTileEntityData();

    void setBlockState(BlockState state);
    void setTime(int time);
    void setCanDropItem(boolean canDropItem);
    void setCanHurtEntities(boolean canHurtEntities);
    void setFallHurtAmount(float fallHurtAmount);
    void setFallHurtMax(float fallHurtMax);
    void setTileEntityData(NbtCompound tileEntityData);

}
