package me.gravityio.creativeplus.entity.living.mob;

import me.gravityio.creativeplus.entity.nbt.NbtPiece;
import me.gravityio.creativeplus.entity.living.ClientLivingEntity;
import me.gravityio.creativeplus.lib.EntityNbtHelper;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

public class ClientMobEntity extends ClientLivingEntity {

    private final MobEntity mob;
    private final MobEntity transform;
    boolean pickupLoot;
    boolean persistent;
    boolean leftHanded;
    boolean noAI;

    private String key(String key) {
        return "mob_entity.nbt." + key;
    }

    private final List<NbtPiece<?>> pieces = List.of(
            NbtPiece.of(this::isPickupLoot, this::setPickupLoot, NbtPiece.Type.BOOLEAN, key("can_pickup_loot")),
            NbtPiece.of(this::isPersistent, this::setPersistent, NbtPiece.Type.BOOLEAN, key("persistence_required")),
            NbtPiece.of(this::isLeftHanded, this::setLeftHanded, NbtPiece.Type.BOOLEAN, key("left_handed")),
            NbtPiece.of(this::isNoAI, this::setNoAI, NbtPiece.Type.BOOLEAN, key("no_ai"))
    );

    public ClientMobEntity(MobEntity mob, MobEntity transform, NbtCompound realNbt) {
        super(mob, transform, realNbt);
        this.mob = mob;
        this.transform = transform;

        this.init();
    }

    private void init() {
        this.pickupLoot = this.mob.canPickUpLoot();
        this.persistent = this.mob.isPersistent();
        this.leftHanded = this.mob.isLeftHanded();
        this.noAI = this.mob.isAiDisabled();
    }

    @Override
    public void updateRealNbt() {
        super.updateRealNbt();

        this.pickupLoot = super.realNbt.getBoolean(EntityNbtHelper.Mob.CAN_PICKUP_LOOT);
        this.persistent = super.realNbt.getBoolean(EntityNbtHelper.Mob.PERSISTENCE_REQUIRED);
        this.leftHanded = super.realNbt.getBoolean(EntityNbtHelper.Mob.LEFT_HANDED);
        this.noAI = super.realNbt.getBoolean(EntityNbtHelper.Mob.NO_AI);
    }

    @Override
    public NbtCompound getOutput() {
        output(EntityNbtHelper.Mob.CAN_PICKUP_LOOT, this.pickupLoot, NbtCompound::putBoolean, NbtCompound::getBoolean);
        output(EntityNbtHelper.Mob.PERSISTENCE_REQUIRED, this.persistent, NbtCompound::putBoolean, NbtCompound::getBoolean);
        output(EntityNbtHelper.Mob.LEFT_HANDED, this.leftHanded, NbtCompound::putBoolean, NbtCompound::getBoolean);
        output(EntityNbtHelper.Mob.NO_AI, this.noAI, NbtCompound::putBoolean, NbtCompound::getBoolean, (v, rv) -> (rv == null && v) || !v.equals(rv));

        return super.getOutput();
    }

    public boolean isPickupLoot() {
        return pickupLoot;
    }

    public void setPickupLoot(boolean v) {
        this.transform.setCanPickUpLoot(v);
        this.pickupLoot = v;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean v) {
        this.transform.persistent = v;
        this.persistent = v;
    }

    public boolean isLeftHanded() {
        return leftHanded;
    }

    public void setLeftHanded(boolean v) {
        this.transform.setLeftHanded(v);
        this.leftHanded = v;
    }

    public boolean isNoAI() {
        return noAI;
    }

    public void setNoAI(boolean v) {
        this.transform.setAiDisabled(v);
        this.noAI = v;
    }

    @Override
    public List<NbtPiece<?>> getNbt() {
        var a = super.getNbt();
        a.addAll(pieces);
        return a;
    }

    //    public void setArmorItems(ArmorItem) {
//
//    }
}
