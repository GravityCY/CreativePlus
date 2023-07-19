package me.gravityio.creativeplus.api.nbt.frame.living.mob;

/**
 * {@link net.minecraft.entity.mob.ZoglinEntity#writeCustomDataToNbt}
 */
public interface ZoglinFrame {
    boolean isBaby();

    void setBaby(boolean baby);
}
