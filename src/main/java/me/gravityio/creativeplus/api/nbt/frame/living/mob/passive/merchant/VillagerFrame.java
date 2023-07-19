package me.gravityio.creativeplus.api.nbt.frame.living.mob.passive.merchant;

import net.minecraft.village.VillagerGossips;

public interface VillagerFrame {
    byte getFoodLevel();
    VillagerGossips getGossips();
    int getXp();
    long getLastRestock();
    long getLastGossipDecay();
    int getRestocksToday();
    boolean isNatural();

    void setFoodLevel(byte foodLevel);
    void setGossips(VillagerGossips gossips);
    void setXp(int xp);
    void setLastRestock(long lastRestock);
    void setLastGossipDecay(long lastGossipDecay);
    void setRestocksToday(int restocksToday);
    void setNatural(boolean natural);
}
