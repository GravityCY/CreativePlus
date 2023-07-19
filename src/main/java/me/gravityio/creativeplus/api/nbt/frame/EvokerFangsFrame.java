package me.gravityio.creativeplus.api.nbt.frame;

import java.util.UUID;

public interface EvokerFangsFrame {
    int getWarmup();
    UUID getOwner();

    void setWarmup(int warmup);
    void setOwner(UUID owner);
}
