package me.gravityio.creativeplus.api.nbt.frame.living.mob.abstract_piglin;

public interface PiglinFrame {
    boolean isBaby();
    boolean isCapableOfHunting();

    void setBaby(boolean baby);
    void setCapableOfHunting(boolean capableOfHunting);
}
