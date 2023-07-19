package me.gravityio.creativeplus.api.nbt.frame.living.mob;

public interface CreeperFrame {
    boolean isPowered();
    boolean isIgnited();
    short getFuse();
    byte getExplosionRadius();

    void setPowered(boolean powered);
    void setIgnited(boolean ignited);
    void setFuse(short fuse);
    void setExplosionRadius(byte explosionRadius);
}
