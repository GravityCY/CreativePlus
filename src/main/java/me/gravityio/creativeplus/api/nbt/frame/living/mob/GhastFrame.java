package me.gravityio.creativeplus.api.nbt.frame.living.mob;

/**
 * {@link net.minecraft.entity.mob.GhastEntity#writeCustomDataToNbt}
 */
public interface GhastFrame {
    byte getExplosionPower();

    void setExplosionPower(byte explosionPower);
}
