package me.gravityio.creativeplus.entity.living.mob.passive.animal;

import me.gravityio.creativeplus.entity.living.mob.passive.ClientPassiveEntity;
import me.gravityio.creativeplus.entity.nbt.NbtPiece;
import me.gravityio.creativeplus.lib.EntityNbtHelper.Animal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.List;
import java.util.UUID;

/**
 * @see AnimalEntity#writeCustomDataToNbt
 */
public class ClientAnimalEntity extends ClientPassiveEntity {
    private final AnimalEntity transform;
    private final AnimalEntity animal;
    private int loveTicks;
    private UUID loveCause;

    private String key(String v) {
        return "animal.nbt." + v;
    }
    final List<NbtPiece<?>> pieces = List.of(
            NbtPiece.of(this::getLoveTicks, this::setLoveTicks, NbtPiece.Type.INT, key("in_love"))/*,
            NbtPiece.of(this::getLoveCause, this::setLoveCause, NbtPiece.Type.UUID, key("love_cause"))*/
    );

    public ClientAnimalEntity(AnimalEntity animal, AnimalEntity transform, NbtCompound realNbt) {
        super(animal, transform, realNbt);
        this.animal = animal;
        this.transform = transform;

        this.init();
    }

    private void init() {
        this.loveTicks = this.animal.getLoveTicks();
        this.loveCause = this.animal.lovingPlayer;
    }

    @Override
    public void updateRealNbt() {
        super.updateRealNbt();

        this.loveTicks = super.realNbt.getInt(Animal.IN_LOVE);
        if (super.realNbt.contains(Animal.LOVE_CAUSE))
            this.loveCause = super.realNbt.getUuid(Animal.LOVE_CAUSE);
    }

    @Override
    public NbtCompound getOutput() {
        output(Animal.IN_LOVE, this.loveTicks, NbtCompound::putInt, NbtCompound::getInt);
//        output(Animal.LOVE_CAUSE, this.loveCause, NbtCompound::putUuid, NbtCompound::getUuid);

        return super.getOutput();
    }

    @Override
    public List<NbtPiece<?>> getNbt() {
        var a = super.getNbt();
        a.addAll(pieces);
        return a;
    }

    public int getLoveTicks() {
        return this.loveTicks;
    }

    public UUID getLoveCause() {
        return this.loveCause;
    }

    public void setLoveTicks(int v) {
        this.transform.setLoveTicks(v);
        this.loveTicks = v;
    }

    public void setLoveCause(UUID v) {
        this.transform.lovingPlayer = v;
        this.loveCause = v;
    }
}
