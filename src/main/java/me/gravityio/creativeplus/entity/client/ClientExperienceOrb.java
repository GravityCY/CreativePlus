package me.gravityio.creativeplus.entity.client;

import me.gravityio.creativeplus.api.nbt.frame.ExperienceOrbFrame;
import me.gravityio.creativeplus.api.nbt.pieces.NbtPiece;
import me.gravityio.creativeplus.lib.helper.EntityNbtHelper.ExperienceOrb;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

import static me.gravityio.creativeplus.api.nbt.pieces.NbtPiece.Type.INT;
import static me.gravityio.creativeplus.api.nbt.pieces.NbtPiece.Type.SHORT;

public class ClientExperienceOrb extends ClientEntity implements ExperienceOrbFrame {
    private final ExperienceOrbEntity orb;
    private final ExperienceOrbEntity transform;
    private short health;
    private short age;
    private short value;
    private int count;

    private static String key(String v) {
        return "experience_orb.nbt."+v;
    }

    public final List<NbtPiece<?>> pieces = List.of(
            NbtPiece.of(this::getHealth, this::setHealth, SHORT, key("health")),
            NbtPiece.of(this::getAge, this::setAge, SHORT, key("age")),
            NbtPiece.of(this::getValue, this::setValue, SHORT, key("value")),
            NbtPiece.of(this::getCount, this::setCount, INT, key("count"))
    );

    public ClientExperienceOrb(ExperienceOrbEntity orb, ExperienceOrbEntity transform, NbtCompound realNbt) {
        super(orb, transform, realNbt);
        this.orb = orb;
        this.transform = transform;

        this.init();
    }

    private void init() {
        this.health = (short) this.orb.health;
        this.age = (short) this.orb.orbAge;
        this.value = (short) this.orb.getExperienceAmount();
        this.count = this.orb.pickingCount;
    }

    @Override
    protected void updateRealNbt() {
        super.updateRealNbt();

        this.health = realNbt.getShort(ExperienceOrb.HEALTH);
        this.age = realNbt.getShort(ExperienceOrb.AGE);
        this.value = realNbt.getShort(ExperienceOrb.VALUE);
        this.count = realNbt.getInt(ExperienceOrb.COUNT);
    }

    @Override
    public NbtCompound getOutput() {
        output(ExperienceOrb.HEALTH, this.health, NbtCompound::putShort, NbtCompound::getShort);
        output(ExperienceOrb.AGE, this.age, NbtCompound::putShort, NbtCompound::getShort);
        output(ExperienceOrb.VALUE, this.value, NbtCompound::putShort, NbtCompound::getShort);
        output(ExperienceOrb.COUNT, this.count, NbtCompound::putInt, NbtCompound::getInt);

        return super.getOutput();
    }

    @Override
    public short getHealth() {
        return this.health;
    }

    @Override
    public short getAge() {
        return this.age;
    }

    @Override
    public short getValue() {
        return this.value;
    }

    @Override
    public int getCount() {
        return this.count;
    }

    @Override
    public void setHealth(short health) {
        this.transform.health = health;
        this.health = health;
    }

    @Override
    public void setAge(short age) {
        this.transform.orbAge = age;
        this.age = age;
    }

    @Override
    public void setValue(short value) {
        this.transform.amount = value;
        this.value = value;
    }

    @Override
    public void setCount(int count) {
        this.transform.pickingCount = count;
        this.count = count;
    }

    @Override
    public List<NbtPiece<?>> getNbt() {
        var a = super.getNbt();
        a.addAll(pieces);
        return a;
    }
}
