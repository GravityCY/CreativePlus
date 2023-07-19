package me.gravityio.creativeplus.api.nbt.frame.living.mob;

import net.minecraft.block.BlockState;

public interface EndermanFrame {
    BlockState getCarriedBlock();

    void setCarriedBlock(BlockState state);
}
