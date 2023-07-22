package me.gravityio.creativeplus.lib.helper;

import net.minecraft.entity.Entity;

public class EntityHelper {

    public static float getSize(Entity entity) {
        return Math.max(entity.getWidth(), entity.getHeight());
    }

}
