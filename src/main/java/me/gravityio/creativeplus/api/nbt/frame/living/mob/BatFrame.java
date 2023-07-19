package me.gravityio.creativeplus.api.nbt.frame.living.mob;

/**
 * {@link net.minecraft.entity.passive.BatEntity#writeCustomDataToNbt}
 */
public interface BatFrame {
    byte getFlags();

    void setFlags(byte flags);
}
