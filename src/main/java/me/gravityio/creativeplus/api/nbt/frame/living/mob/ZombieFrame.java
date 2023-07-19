package me.gravityio.creativeplus.api.nbt.frame.living.mob;

/**
 * {@link net.minecraft.entity.mob.ZombieEntity#writeCustomDataToNbt}
 */
public interface ZombieFrame {
    boolean isBaby();
    boolean canBreakDoors();
    int getInWaterTicks();
    int getDrownedConversionTime();

    void setBaby(boolean baby);
    void setCanBreakDoors(boolean canBreakDoors);
    void setInWaterTicks(int inWaterTicks);
    void setDrownedConversionTime(int drownedConversionTime);
}
