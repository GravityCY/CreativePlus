package me.gravityio.creativeplus.api.nbt.frame.living.mob;

/**
 * {@link net.minecraft.entity.boss.WitherEntity#writeCustomDataToNbt}
 */
public interface WitherFrame {
    int getInvulnerableTicks();

    void setInvulnerableTicks(int ticks);
}
