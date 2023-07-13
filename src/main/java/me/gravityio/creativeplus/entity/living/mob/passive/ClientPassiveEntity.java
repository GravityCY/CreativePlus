package me.gravityio.creativeplus.entity.living.mob.passive;

import me.gravityio.creativeplus.entity.living.mob.ClientMobEntity;
import me.gravityio.creativeplus.entity.nbt.NbtPiece;
import me.gravityio.creativeplus.lib.EntityNbtHelper.Passive;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

/**
 * @see PassiveEntity#writeCustomDataToNbt
 */
public class ClientPassiveEntity extends ClientMobEntity {
    PassiveEntity passive;
    int age;
    int forcedAge;

    private String key(String v) {
        return "passive.nbt." + v;
    }

    final List<NbtPiece<?>> pieces = List.of(
        NbtPiece.ofBounded(this::getAge, this::setAge, NbtPiece.Type.INT, key("age"), -24000, 24000),
        NbtPiece.ofBounded(this::getForcedAge, this::setForcedAge, NbtPiece.Type.INT, key("forced_age"), -24000, 24000)
    );

    public ClientPassiveEntity(PassiveEntity passive, NbtCompound realNbt) {
        super(passive, realNbt);
        this.passive = passive;

        this.init();
    }

    private void init() {
        this.age = this.passive.age;
        this.forcedAge = this.passive.forcedAge;
    }

    @Override
    public void update() {
        super.update();

        this.age = super.realNbt.getInt(Passive.AGE);
        this.forcedAge = super.realNbt.getInt(Passive.FORCED_AGE);
    }

    @Override
    public NbtCompound getOutput() {
        output(Passive.AGE, this.age, NbtCompound::putInt, NbtCompound::getInt);
        output(Passive.FORCED_AGE, this.forcedAge, NbtCompound::putInt, NbtCompound::getInt);

        return super.getOutput();
    }

    public int getAge() {
        return this.age;
    }

    public int getForcedAge() {
        return this.forcedAge;
    }

    public void setAge(int age) {
        this.age = age;
    }
    public void setForcedAge(int forcedAge) {
        this.forcedAge = forcedAge;
    }

    @Override
    public List<NbtPiece<?>> getNbt() {
        var a = super.getNbt();
        a.addAll(pieces);
        return a;
    }

}
