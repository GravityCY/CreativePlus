package me.gravityio.creativeplus.api.nbt.frame.living.mob.patrol.raider;

/**
 * {@link net.minecraft.entity.raid.RaiderEntity#writeCustomDataToNbt}
 */
public interface RaiderFrame {
    int getWave();
    boolean canJoinRaid();
    int getRaidId();

    void setWave(int wave);
    void setCanJoinRaid(boolean canJoinRaid);
    void setRaidId(int raidId);
}
