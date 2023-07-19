package me.gravityio.creativeplus.api.nbt.frame;

import java.util.UUID;

/**
 * {@link net.minecraft.entity.projectile.ProjectileEntity#writeCustomDataToNbt}
 */
public interface ProjectileFrame {
    UUID getOwner();
    boolean hasLeftOwner();
    boolean hasBeenShot();

    void setOwner(UUID owner);
    void setHasLeftOwner(boolean hasLeftOwner);
    void setHasBeenShot(boolean hasBeenShot);
}
