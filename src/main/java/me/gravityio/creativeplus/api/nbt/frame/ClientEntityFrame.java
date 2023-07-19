package me.gravityio.creativeplus.api.nbt.frame;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;

public interface ClientEntityFrame extends EntityFrame {
    NbtCompound getOutput();
    NbtCompound getRealNbt();
    Entity getReadEntity();
    Entity getWriteEntity();
}
