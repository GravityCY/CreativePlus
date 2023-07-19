package me.gravityio.creativeplus.api.nbt.frame;

/**
 * {@link net.minecraft.entity.TntEntity#writeCustomDataToNbt}
 */
public interface TntFrame {
    short getFuse();

    void setFuse(short fuse);
}
