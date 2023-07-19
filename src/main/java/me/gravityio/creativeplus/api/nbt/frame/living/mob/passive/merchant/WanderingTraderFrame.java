package me.gravityio.creativeplus.api.nbt.frame.living.mob.passive.merchant;

import net.minecraft.util.math.BlockPos;

public interface WanderingTraderFrame {
    int getDespawnDelay();
    BlockPos getWanderTarget();

    void setDespawnDelay(int despawnDelay);
    void setWanderTarget(BlockPos pos);
}
