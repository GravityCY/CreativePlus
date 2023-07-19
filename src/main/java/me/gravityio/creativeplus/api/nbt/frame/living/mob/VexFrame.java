package me.gravityio.creativeplus.api.nbt.frame.living.mob;

import net.minecraft.util.math.BlockPos;

/**
 * {@link net.minecraft.entity.mob.VexEntity#writeCustomDataToNbt}
 */
public interface VexFrame {
    BlockPos getBoundsPos();
    int getBoundX();
    int getBoundY();
    int getBoundZ();
    int getLifeTicks();

    void setBoundsPos(BlockPos pos);
    void setBoundX(int boundX);
    void setBoundY(int boundY);
    void setBoundZ(int boundZ);
    void setLifeTicks(int lifeTicks);
}
