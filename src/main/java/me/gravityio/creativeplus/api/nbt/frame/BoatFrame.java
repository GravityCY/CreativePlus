package me.gravityio.creativeplus.api.nbt.frame;

import net.minecraft.entity.vehicle.BoatEntity;

/**
 * {@link net.minecraft.entity.vehicle.BoatEntity#writeCustomDataToNbt}
 */
public interface BoatFrame {
    BoatEntity.Type getType();

    void setType(BoatEntity.Type type);
}
