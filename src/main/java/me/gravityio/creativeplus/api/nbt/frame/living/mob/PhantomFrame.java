package me.gravityio.creativeplus.api.nbt.frame.living.mob;

import net.minecraft.util.math.BlockPos;

/**
 * {@link net.minecraft.entity.mob.PhantomEntity#writeCustomDataToNbt}
 */
public interface PhantomFrame {
    BlockPos getCirclingPos();
    int getAX();
    int getAY();
    int getAZ();
    int getSize();


    void setCirclingPos(BlockPos pos);
    void setAX(int ax);
    void setAY(int ay);
    void setAZ(int az);
    void setSize(int size);
}
