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
    AnimalEntity animal;
    boolean inLove;
    UUID loveCause;

    private String key(String v) {
        return "animal.nbt." + v;
    }
    final List<NbtPiece<?>> pieces = List.of(
            NbtPiece.of(this::isInLove, this::setInLove, NbtPiece.Type.BOOLEAN, key("in_love")),
            NbtPiece.of(this::getLoveCause, this::setLoveCause, NbtPiece.Type.UUID, key("love_cause"))
    );

    public ClientAnimalEntity(AnimalEntity animal, NbtCompound realNbt) {
        super(animal, realNbt);
        this.animal = animal;

        this.init();
    }

    private void init() {
        this.inLove = this.animal.isInLove();
        this.loveCause = this.animal.lovingPlayer;
    }

    @Override
    public void update() {
        super.update();

        this.inLove = super.realNbt.getBoolean(Animal.IN_LOVE);
        this.loveCause = super.realNbt.getUuid(Animal.LOVE_CAUSE);
    }

    @Override
    public NbtCompound getOutput() {
        output(Animal.IN_LOVE, this.inLove, NbtCompound::putBoolean, NbtCompound::getBoolean);
//        output(Animal.LOVE_CAUSE, this.loveCause, NbtCompound::putUuid, NbtCompound::getUuid);

        return super.getOutput();
    }

    @Override
    public List<NbtPiece<?>> getNbt() {
        var a = super.getNbt();
        a.addAll(pieces);
        return a;
    }

    public boolean isInLove() {
        return this.inLove;
    }

    public UUID getLoveCause() {
        return this.loveCause;
    }

    public void setInLove(boolean v) {
        super.output.putBoolean(Animal.IN_LOVE, v);
    }

    public void setLoveCause(UUID v) {
        super.output.putUuid(Animal.LOVE_CAUSE, v);
    }
}
