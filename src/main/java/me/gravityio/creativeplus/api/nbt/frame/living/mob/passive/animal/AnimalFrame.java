package me.gravityio.creativeplus.api.nbt.frame.living.mob.passive.animal;

import java.util.UUID;

/**
 * {@link net.minecraft.entity.passive.AnimalEntity#writeCustomDataToNbt}
 */
public interface AnimalFrame {
    int getLoveTicks();
    UUID getLoveCause();

    void setLoveTicks(int loveTicks);
    void setLoveCause(UUID loveCause);
}
