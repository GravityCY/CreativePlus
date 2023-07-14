package me.gravityio.creativeplus.entity;

import me.gravityio.creativeplus.entity.nbt.NbtPiece;
import me.gravityio.creativeplus.lib.EntityNbtHelper.ExperienceOrb;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

import static me.gravityio.creativeplus.entity.nbt.NbtPiece.Type.INT;

public class ClientExperienceOrb extends ClientEntity {
    private final ExperienceOrbEntity orb;
    private final ExperienceOrbEntity transform;
    private int health;
    private int age;
    private int value;
    private int count;

    private static String key(String v) {
        return "experience_orb.nbt."+v;
    }

    public final List<NbtPiece<?>> pieces = List.of(
            NbtPiece.of(this::getHealth, this::setHealth, INT, key("health")),
            NbtPiece.of(this::getAge, this::setAge, INT, key("age")),
            NbtPiece.of(this::getValue, this::setValue, INT, key("value")),
            NbtPiece.of(this::getCount, this::setCount, INT, key("count"))
    );

    public ClientExperienceOrb(ExperienceOrbEntity orb, ExperienceOrbEntity transform, NbtCompound realNbt) {
        super(orb, transform, realNbt);
        this.orb = orb;
        this.transform = transform;

        this.init();
    }

    private void init() {
        this.health = this.orb.health;
        this.age = this.orb.orbAge;
        this.value = this.orb.getExperienceAmount();
        this.count = this.orb.pickingCount;
    }

    @Override
    protected void updateRealNbt() {
        super.updateRealNbt();

        this.health = realNbt.getInt(ExperienceOrb.HEALTH);
        this.age = realNbt.getInt(ExperienceOrb.AGE);
        this.value = realNbt.getInt(ExperienceOrb.VALUE);
        this.count = realNbt.getInt(ExperienceOrb.COUNT);
    }

    @Override
    public NbtCompound getOutput() {
        output(ExperienceOrb.HEALTH, this.health, NbtCompound::putInt, NbtCompound::getInt);
        output(ExperienceOrb.AGE, this.age, NbtCompound::putInt, NbtCompound::getInt);
        output(ExperienceOrb.VALUE, this.value, NbtCompound::putInt, NbtCompound::getInt);
        output(ExperienceOrb.COUNT, this.count, NbtCompound::putInt, NbtCompound::getInt);

        return super.getOutput();
    }

    public int getHealth() {
        return this.health;
    }

    public int getAge() {
        return this.age;
    }

    public int getValue() {
        return this.value;
    }

    public int getCount() {
        return this.count;
    }

    public void setHealth(int health) {
        this.transform.health = health;
        this.health = health;
    }

    public void setAge(int age) {
        this.transform.orbAge = age;
        this.age = age;
    }

    public void setValue(int value) {
        this.transform.amount = value;
        this.value = value;
    }

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
