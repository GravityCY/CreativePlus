package me.gravityio.creativeplus.entity.client.living;

import me.gravityio.creativeplus.api.nbt.frame.living.LivingFrame;
import me.gravityio.creativeplus.api.nbt.pieces.MutableNbtBoundedPiece;
import me.gravityio.creativeplus.api.nbt.pieces.NbtPiece;
import me.gravityio.creativeplus.entity.client.ClientEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.List;
import java.util.Objects;

import static me.gravityio.creativeplus.lib.helper.EntityNbtHelper.Living;

public class ClientLivingEntity extends ClientEntity implements LivingFrame {
    final LivingEntity living;
    private final LivingEntity transform;

    float health;
    short hurtTime;
    int hurtByTimestamp;
    short deathTime;
    boolean fallFlying;

    final MutableNbtBoundedPiece<Float> healthPiece = NbtPiece.ofBoundedMutable(this::getHealth, this::setHealth, NbtPiece.Type.FLOAT, "living_entity.nbt.health", 0, 20);
    final List<NbtPiece<?>> pieces = List.of(healthPiece);
    public ClientLivingEntity(LivingEntity living, LivingEntity transform, NbtCompound realNbt) {
        super(living, transform, realNbt);
        this.living = living;
        this.transform = transform;

        this.init();
    }

    private void init() {
        healthPiece.setMax(this.living.getMaxHealth());
        this.health = this.living.getHealth();
    }


    @Override
    public void updateRealNbt() {
        super.updateRealNbt();

        this.health = super.realNbt.getFloat(Living.HEALTH);
    }

    @Override
    public NbtCompound getOutput() {
        output(Living.HEALTH, this.health, NbtCompound::putFloat, NbtCompound::getFloat, (v, rv) -> !Objects.equals(v, rv));

        return super.getOutput();
    }

    @Override
    public void setHealth(float health) {
        this.transform.setHealth(health);
        this.health = health;
    }

    @Override
    public float getHealth() {
        return this.health;
    }

    @Override
    public List<NbtPiece<?>> getNbt() {
        var a = super.getNbt();
        a.addAll(pieces);
        return a;
    }

//    public void setActiveEffects()

//    public void setAttributes()
}
