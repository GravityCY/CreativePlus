package me.gravityio.creativeplus.api.nbt.frame.living;

/**
 * {@link net.minecraft.entity.LivingEntity#writeCustomDataToNbt}
 */
public interface LivingFrame {

    float getHealth();

    void setHealth(float health);
}
