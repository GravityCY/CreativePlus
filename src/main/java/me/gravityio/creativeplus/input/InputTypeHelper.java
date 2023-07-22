package me.gravityio.creativeplus.input;

import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicates;

import java.util.function.Predicate;

public class InputTypeHelper {
    public static final Predicate<Entity> TARGETED_ENTITY = EntityPredicates.EXCEPT_SPECTATOR.and(e -> e.getVehicle() == null && !e.isPlayer());

}
