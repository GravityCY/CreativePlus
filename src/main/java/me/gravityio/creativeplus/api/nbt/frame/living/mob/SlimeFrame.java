package me.gravityio.creativeplus.api.nbt.frame.living.mob;

/**
 * {@link net.minecraft.entity.mob.SlimeEntity#writeCustomDataToNbt}
 */
public interface SlimeFrame {
    int getSize();
    boolean wasOnGround();

    void setSize(int size);
    void setWasOnGround(boolean wasOnGround);
}
