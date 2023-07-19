package me.gravityio.creativeplus.api.nbt.frame.living.mob.patrol.raider;

/**
 * {@link net.minecraft.entity.mob.RavagerEntity#writeCustomDataToNbt}
 */
public interface RavagerFrame {

    int getAttackTick();
    int getStunTick();
    int getRoarTick();

    void setAttackTick(int attackTick);
    void setStunTick(int stunTick);
    void setRoarTick(int roarTick);

}
