package me.gravityio.creativeplus.api.nbt.frame.living.mob.patrol;

import net.minecraft.util.math.BlockPos;

/**
 * {@link net.minecraft.entity.mob.PatrolEntity#writeCustomDataToNbt}
 */
public interface PatrolFrame {
    BlockPos getPatrolPos();
    boolean isLeader();
    boolean isPatrolling();

    void setPatrolPos(BlockPos pos);
    void setLeader(boolean leader);
    void setPatrolling(boolean patrolling);
}
