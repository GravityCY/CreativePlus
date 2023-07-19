package me.gravityio.creativeplus.api.nbt.frame.living.mob.passive;

/**
 * {@link net.minecraft.entity.passive.PassiveEntity#writeCustomDataToNbt}
 */
public interface PassiveFrame {
    int getAge();
    int getForcedAge();

    void setAge(int age);
    void setForcedAge(int forcedAge);
}
