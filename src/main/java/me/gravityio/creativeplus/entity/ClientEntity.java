package me.gravityio.creativeplus.entity;

import me.gravityio.creativeplus.entity.living.mob.ClientIronGolemEntity;
import me.gravityio.creativeplus.entity.living.mob.passive.ClientPassiveEntity;
import me.gravityio.creativeplus.entity.living.mob.passive.animal.ClientAnimalEntity;
import me.gravityio.creativeplus.entity.nbt.NbtPiece;
import me.gravityio.creativeplus.entity.living.ClientArmorStandEntity;
import me.gravityio.creativeplus.entity.living.ClientLivingEntity;
import me.gravityio.creativeplus.entity.living.mob.ClientAllayEntity;
import me.gravityio.creativeplus.entity.living.mob.ClientMobEntity;
import me.gravityio.creativeplus.lib.EntityNbtHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class ClientEntity {

    protected NbtCompound realNbt;
    protected final NbtCompound output = new NbtCompound();
    boolean glowing;
    boolean invulnerable;
    boolean noGravity;
    boolean silent;
    boolean nameVisible;
    Text customName;

    private final List<NbtPiece<?>> pieces = List.of(
            NbtPiece.of(this::isGlowing, this::setGlowing, NbtPiece.Type.BOOLEAN, "entity.nbt.glowing"),
            NbtPiece.of(this::isInvulnerable, this::setInvulnerable, NbtPiece.Type.BOOLEAN, "entity.nbt.invulnerable"),
            NbtPiece.of(this::hasNoGravity, this::setNoGravity, NbtPiece.Type.BOOLEAN, "entity.nbt.no_gravity"),
            NbtPiece.of(this::isSilent, this::setSilent, NbtPiece.Type.BOOLEAN, "entity.nbt.silent"),
            NbtPiece.of(this::isCustomNameVisible, this::setCustomNameVisible, NbtPiece.Type.BOOLEAN, "entity.nbt.custom_name_visible"),
            NbtPiece.ofBounded(this::getCustomName, this::setCustomName, NbtPiece.Type.TEXT, "entity.nbt.custom_name", 0, 128)
    );

    Entity entity;

    /**
     * Tries to create an instance of the correct type depending on whether the given entity is an instance of these subclasses<br><br>
     *
     * @param realNbt This will be the real data of the current entity on the
     *                client, can be set later on when for example the server responds
     *                back to the server with the actual data of the entity
     */
    public static ClientEntity create(Entity e, @Nullable NbtCompound realNbt) {
        if (e instanceof IronGolemEntity golem) {
            return new ClientIronGolemEntity(golem, realNbt);
        } else  if (e instanceof ArmorStandEntity stand) {
            return new ClientArmorStandEntity(stand, realNbt);
        } else if (e instanceof AllayEntity allay) {
            return new ClientAllayEntity(allay, realNbt);
        } else if (e instanceof AnimalEntity animal) {
            return new ClientAnimalEntity(animal, realNbt);
        } else if (e instanceof PassiveEntity passive) {
            return new ClientPassiveEntity(passive, realNbt);
        } else if (e instanceof MobEntity mob) {
            return new ClientMobEntity(mob, realNbt);
        } else if (e instanceof LivingEntity living) {
            return new ClientLivingEntity(living, realNbt);
        }
        return new ClientEntity(e, realNbt);

    }

    public ClientEntity(Entity entity, NbtCompound realNbt) {
        this.realNbt = realNbt;
        this.entity = entity;

        this.init();
    }

    private void init() {
        this.glowing = this.entity.isGlowing();
        this.invulnerable = this.entity.isInvulnerable();
        this.noGravity = this.entity.hasNoGravity();
        this.silent = this.entity.isSilent();
        this.nameVisible = this.entity.isCustomNameVisible();
        this.customName = this.entity.getCustomName();
    }

    protected void update() {
        this.glowing = this.realNbt.getBoolean(EntityNbtHelper.Entity.GLOWING);
        this.invulnerable = this.realNbt.getBoolean(EntityNbtHelper.Entity.INVULNERABLE);
        this.noGravity = this.realNbt.getBoolean(EntityNbtHelper.Entity.NO_GRAVITY);
        this.silent = this.realNbt.getBoolean(EntityNbtHelper.Entity.SILENT);
        this.nameVisible = this.realNbt.getBoolean(EntityNbtHelper.Entity.CUSTOM_NAME_VISIBLE);
    }

    protected <T> void output(String key, T value, TriConsumer<NbtCompound, String, T> putConsumer, BiFunction<NbtCompound, String, T> getFunction) {
        output(key, value, putConsumer, getFunction, (i, ii) -> i != null && !i.equals(ii));
    }

    protected <T> void output(String key, T value, TriConsumer<NbtCompound, String, T> putConsumer, BiFunction<NbtCompound, String, T> getFunction, BiPredicate<T, T> realChecker) {
        T rv = getFunction.apply(this.realNbt, key);
        if (realChecker.test(value, rv)) {
            putConsumer.accept(this.output, key, value);
        }
    }

    public void setRealNbt(NbtCompound realNbt) {
        this.realNbt = realNbt;
        this.update();
    }


    public boolean isGlowing() {
        return this.glowing;
    }

    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    public boolean hasNoGravity() {
        return this.noGravity;
    }

    public boolean isSilent() {
        return this.silent;
    }

    public boolean isCustomNameVisible() {
        return this.nameVisible;
    }

    public Text getCustomName() {
        return this.customName;
    }

    public void setGlowing(boolean v) {
        this.glowing = v;
    }

    public void setInvulnerable(boolean v) {
        this.invulnerable = v;
    }

    public void setNoGravity(boolean v) {
        this.noGravity = v;
    }

    public void setSilent(boolean v) {
        this.silent = v;
    }

    public void setCustomNameVisible(boolean v) {
        this.nameVisible = v;
    }

    public void setCustomName(Text text) {
        this.customName = text;
    }

    public NbtCompound getOutput() {
        BiPredicate<Boolean, Boolean> booleanPredicate = (v, rv) -> (rv == null && v) || !v.equals(rv);

        output(EntityNbtHelper.Entity.GLOWING, this.glowing, NbtCompound::putBoolean, NbtCompound::getBoolean, booleanPredicate);
        output(EntityNbtHelper.Entity.INVULNERABLE, this.invulnerable, NbtCompound::putBoolean, NbtCompound::getBoolean, booleanPredicate);
        output(EntityNbtHelper.Entity.NO_GRAVITY, this.noGravity, NbtCompound::putBoolean, NbtCompound::getBoolean, booleanPredicate);
        output(EntityNbtHelper.Entity.SILENT, this.silent, NbtCompound::putBoolean, NbtCompound::getBoolean, booleanPredicate);
        output(EntityNbtHelper.Entity.CUSTOM_NAME_VISIBLE, this.nameVisible, NbtCompound::putBoolean, NbtCompound::getBoolean, booleanPredicate);

        TriConsumer<NbtCompound, String, String> textPutter = (nbt, s, s2) -> {
            // Putting 'null' in a nbt seems to actually nullify it in data merge. example; data merge {CustomName:null}
            String temp = String.valueOf(s2);
            nbt.putString(s, temp);
        };

        BiFunction<NbtCompound, String, String> textGetter = (nbtCompound, s) -> {
            var temp = nbtCompound.get(s);
            if (temp == null)
                return null;
            return temp.asString();
        };

        BiPredicate<String, String> textPredicate = (s, s2) ->  s == null && s2 != null || s != null && !s.equals(s2);

        output(EntityNbtHelper.Entity.CUSTOM_NAME, customName == null ? null : Text.Serializer.toJson(customName), textPutter, textGetter , textPredicate);

        return this.output;
    }

    public List<NbtPiece<?>> getNbt() {
        return new ArrayList<>(pieces);
    }

}
