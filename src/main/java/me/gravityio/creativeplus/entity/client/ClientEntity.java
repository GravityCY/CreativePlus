package me.gravityio.creativeplus.entity.client;

import me.gravityio.creativeplus.api.nbt.frame.EntityFrame;
import me.gravityio.creativeplus.api.nbt.pieces.NbtPiece;
import me.gravityio.creativeplus.entity.client.living.ClientArmorStandEntity;
import me.gravityio.creativeplus.entity.client.living.ClientLivingEntity;
import me.gravityio.creativeplus.entity.client.living.mob.ClientAllayEntity;
import me.gravityio.creativeplus.entity.client.living.mob.ClientIronGolemEntity;
import me.gravityio.creativeplus.entity.client.living.mob.ClientMobEntity;
import me.gravityio.creativeplus.entity.client.living.mob.passive.ClientPassiveEntity;
import me.gravityio.creativeplus.entity.client.living.mob.passive.animal.ClientAnimalEntity;
import me.gravityio.creativeplus.lib.helper.EntityNbtHelper;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class ClientEntity implements EntityFrame {

    protected NbtCompound realNbt;
    protected final NbtCompound output = new NbtCompound();
    private final Entity entity;
    private final Entity transform;
    private final List<NbtPiece<?>> pieces = List.of(
            NbtPiece.of(this::isGlowing, this::setGlowing, NbtPiece.Type.BOOLEAN, "entity.nbt.glowing"),
            NbtPiece.of(this::isInvulnerable, this::setInvulnerable, NbtPiece.Type.BOOLEAN, "entity.nbt.invulnerable"),
            NbtPiece.of(this::hasNoGravity, this::setNoGravity, NbtPiece.Type.BOOLEAN, "entity.nbt.no_gravity"),
            NbtPiece.of(this::isSilent, this::setSilent, NbtPiece.Type.BOOLEAN, "entity.nbt.silent"),
            NbtPiece.of(this::isCustomNameVisible, this::setCustomNameVisible, NbtPiece.Type.BOOLEAN, "entity.nbt.custom_name_visible"),
            NbtPiece.ofBounded(this::getCustomName, this::setCustomName, NbtPiece.Type.TEXT, "entity.nbt.custom_name", 0, 128)
    );
    private boolean glowing;
    private boolean invulnerable;
    private boolean noGravity;
    private boolean silent;
    private boolean nameVisible;
    private Text customName;


    /**
     * Tries to create an instance of the correct type depending on whether the given entity is an instance of these subclasses<br><br>
     *
     * @param readEntity The entity to use to get the initial data from
     * @param writeEntity The entity to transform when transforming the NBT data
     * @param realNbt This will be the real data of the current entity on the
     *                client, can be set later on when for example the server responds
     *                back to the server with the actual data of the entity
     */
    public static ClientEntity create(Entity readEntity, Entity writeEntity, @NotNull NbtCompound realNbt) {

        if (readEntity instanceof DisplayEntity.TextDisplayEntity display) {
            return new ClientTextDisplay(display, (DisplayEntity.TextDisplayEntity) writeEntity, realNbt);
        } else if (readEntity instanceof DisplayEntity display) {
            return new ClientDisplay(display, (DisplayEntity) writeEntity, realNbt);

        } else if (readEntity instanceof AreaEffectCloudEntity cloud) {
            return new ClientAreaEffectCloud(cloud, (AreaEffectCloudEntity) writeEntity, realNbt);

        } else if (readEntity instanceof AbstractMinecartEntity minecart) {
          return new ClientAbstractMinecart(minecart, (AbstractMinecartEntity) writeEntity, realNbt);

        } else if (readEntity instanceof ExperienceOrbEntity orb) {
            return new ClientExperienceOrb(orb, (ExperienceOrbEntity) writeEntity, realNbt);

        } else if (readEntity instanceof IronGolemEntity golem) {
            return new ClientIronGolemEntity(golem, (IronGolemEntity) writeEntity,  realNbt);

        } else  if (readEntity instanceof ArmorStandEntity stand) {
            return new ClientArmorStandEntity(stand, (ArmorStandEntity) writeEntity, realNbt);

        } else if (readEntity instanceof AllayEntity allay) {
            return new ClientAllayEntity(allay, (AllayEntity) writeEntity, realNbt);

        } else if (readEntity instanceof AnimalEntity animal) {
            return new ClientAnimalEntity(animal, (AnimalEntity) writeEntity, realNbt);

        } else if (readEntity instanceof PassiveEntity passive) {
            return new ClientPassiveEntity(passive, (PassiveEntity) writeEntity, realNbt);

        } else if (readEntity instanceof MobEntity mob) {
            return new ClientMobEntity(mob, (MobEntity) writeEntity, realNbt);

        } else if (readEntity instanceof LivingEntity living) {
            return new ClientLivingEntity(living, (LivingEntity) writeEntity, realNbt);

        }
        return new ClientEntity(readEntity, writeEntity, realNbt);

    }

    public ClientEntity(Entity entity, Entity transform, NbtCompound realNbt) {
        this.realNbt = realNbt;
        this.entity = entity;
        this.transform = transform;

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

    protected void updateRealNbt() {
        this.setGlowing(this.realNbt.getBoolean(EntityNbtHelper.Entity.GLOWING));
        this.setInvulnerable(this.invulnerable = this.realNbt.getBoolean(EntityNbtHelper.Entity.INVULNERABLE));
        this.setNoGravity(this.noGravity = this.realNbt.getBoolean(EntityNbtHelper.Entity.NO_GRAVITY));
        this.setSilent(this.realNbt.getBoolean(EntityNbtHelper.Entity.SILENT));
        this.setCustomNameVisible(this.realNbt.getBoolean(EntityNbtHelper.Entity.CUSTOM_NAME_VISIBLE));
    }

    protected <T> void output(String key, T value, TriConsumer<NbtCompound, String, T> putConsumer, BiFunction<NbtCompound, String, T> getFunction) {
        output(key, value, putConsumer, getFunction, (i, ii) -> i != null && !i.equals(ii));
    }

    protected <T> void outputNull(String key, T value, TriConsumer<NbtCompound, String, T> putConsumer, BiFunction<NbtCompound, String, T> getFunction) {
        output(key, value, putConsumer, getFunction, (i, ii) -> i == null || !i.equals(ii));
    }

    protected <T> void output(String key, T value, TriConsumer<NbtCompound, String, T> putConsumer, BiFunction<NbtCompound, String, T> getFunction, BiPredicate<T, T> realChecker) {
        T rv = getFunction.apply(this.realNbt, key);
        if (realChecker.test(value, rv)) {
            putConsumer.accept(this.output, key, value);
        }
    }

    public void setRealNbt(NbtCompound realNbt) {
        this.realNbt = realNbt;
        this.updateRealNbt();
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

    public Entity getReadEntity() {
        return this.entity;
    }

    public Entity getWriteEntity() {
        return this.transform;
    }

    @Override
    public boolean isGlowing() {
        return this.glowing;
    }

    @Override
    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    @Override
    public boolean hasNoGravity() {
        return this.noGravity;
    }

    @Override
    public boolean isSilent() {
        return this.silent;
    }

    @Override
    public boolean isCustomNameVisible() {
        return this.nameVisible;
    }

    @Override
    public Text getCustomName() {
        return this.customName;
    }

    @Override
    public void setGlowing(boolean v) {
        this.transform.setGlowing(v);
        this.glowing = v;
    }

    @Override
    public void setInvulnerable(boolean v) {
        this.transform.setInvulnerable(v);
        this.invulnerable = v;
    }

    @Override
    public void setNoGravity(boolean v) {
        this.transform.setNoGravity(v);
        this.noGravity = v;
    }

    @Override
    public void setSilent(boolean v) {
        this.transform.setSilent(v);
        this.silent = v;
    }

    @Override
    public void setCustomNameVisible(boolean v) {
        this.transform.setCustomNameVisible(v);
        this.nameVisible = v;
    }

    @Override
    public void setCustomName(Text text) {
        this.transform.setCustomName(text);
        this.customName = text;
    }
}
