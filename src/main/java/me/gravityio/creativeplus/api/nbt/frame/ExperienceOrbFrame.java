package me.gravityio.creativeplus.api.nbt.frame;

/**
 * {@link net.minecraft.entity.ExperienceOrbEntity#writeCustomDataToNbt}
 */
public interface ExperienceOrbFrame {
    short getHealth();
    short getAge();
    short getValue();
    int getCount();

    void setHealth(short health);
    void setAge(short age);
    void setValue(short value);
    void setCount(int count);
}
