package me.gravityio.creativeplus.entity.living.mob;

import me.gravityio.creativeplus.entity.nbt.NbtPiece;
import me.gravityio.creativeplus.lib.EntityNbtHelper;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

/**
 * @see AllayEntity#writeCustomDataToNbt
 */
public class ClientAllayEntity extends ClientMobEntity {
    private final AllayEntity allay;
    private final AllayEntity transform;

    private boolean canDuplicate;

    private String key(String v) {
        return "allay.nbt." + v;
    }

    private final List<NbtPiece<?>> pieces = List.of(
            NbtPiece.of(this::canDuplicate, this::setCanDuplicate, NbtPiece.Type.BOOLEAN, key("can_duplicate"))
    );

    public ClientAllayEntity(AllayEntity allay, AllayEntity transform, NbtCompound realNbt) {
        super(allay, transform, realNbt);
        this.allay = allay;
        this.transform = transform;

        this.init();
    }

    private void init() {
        this.canDuplicate = this.allay.canDuplicate();
    }

    @Override
    public void updateRealNbt() {
        super.updateRealNbt();

        this.canDuplicate = super.realNbt.getBoolean(EntityNbtHelper.Allay.CAN_DUPLICATE);
    }

    @Override
    public NbtCompound getOutput() {
        output(EntityNbtHelper.Allay.CAN_DUPLICATE, this.canDuplicate, NbtCompound::putBoolean, NbtCompound::getBoolean);

        return super.getOutput();
    }

    public boolean canDuplicate() {
        return canDuplicate;
    }

    public void setCanDuplicate(boolean canDuplicate) {
        this.allay.getDataTracker().set(AllayEntity.CAN_DUPLICATE, canDuplicate);
        this.canDuplicate = canDuplicate;
    }

    @Override
    public List<NbtPiece<?>> getNbt() {
        var a = super.getNbt();
        a.addAll(pieces);
        return a;
    }
}
