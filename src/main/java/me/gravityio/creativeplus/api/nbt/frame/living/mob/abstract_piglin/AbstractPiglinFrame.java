package me.gravityio.creativeplus.api.nbt.frame.living.mob.abstract_piglin;

public interface AbstractPiglinFrame {
    boolean isImmuneToZombification();
    int getTimeInOverworld();

    void setImmuneToZombification(boolean immuneToZombification);
    void setTimeInOverworld(int timeInOverworld);
}
