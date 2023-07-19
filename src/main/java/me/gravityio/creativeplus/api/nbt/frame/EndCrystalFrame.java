package me.gravityio.creativeplus.api.nbt.frame;

import net.minecraft.util.math.BlockPos;

/**
 * {@link net.minecraft.entity.decoration.EndCrystalEntity#writeCustomDataToNbt}
 */
public interface EndCrystalFrame {
    BlockPos getBeamTarget();
    boolean canShowBottom();

    void setBeamTarget(BlockPos pos);
    void setCanShowBottom(boolean canShowBottom);
}
