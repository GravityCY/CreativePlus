package me.gravityio.creativeplus.entity.client.living.mob;

import me.gravityio.creativeplus.api.nbt.frame.living.mob.IronGolemFrame;
import me.gravityio.creativeplus.api.nbt.pieces.NbtPiece;
import me.gravityio.creativeplus.lib.helper.EntityNbtHelper.IronGolem;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

/**
 * @see IronGolemEntity#writeCustomDataToNbt
 */
public class ClientIronGolemEntity extends ClientMobEntity implements IronGolemFrame {

    private final IronGolemEntity ironGolem;
    private final IronGolemEntity transform;
    boolean playerCreated;

    private String key(String v) {
        return "iron_golem.nbt." + v;
    }

    private final List<NbtPiece<?>> pieces = List.of(
            NbtPiece.of(this::isPlayerCreated, this::setPlayerCreated, NbtPiece.Type.BOOLEAN, key("player_created"))
    );

    public ClientIronGolemEntity(IronGolemEntity ironGolem, IronGolemEntity transform, NbtCompound realNbt) {
        super(ironGolem, transform, realNbt);
        this.ironGolem = ironGolem;
        this.transform = transform;

        this.init();
    }

    private void init() {
        this.playerCreated = this.ironGolem.isPlayerCreated();
    }

    @Override
    public void updateRealNbt() {
        super.updateRealNbt();

        this.playerCreated = super.realNbt.getBoolean(IronGolem.PLAYER_CREATED);
    }

    @Override
    public NbtCompound getOutput() {
        output(IronGolem.PLAYER_CREATED, this.playerCreated, NbtCompound::putBoolean, NbtCompound::getBoolean);

        return super.getOutput();
    }

    @Override
    public boolean isPlayerCreated() {
        return this.playerCreated;
    }

    @Override
    public void setPlayerCreated(boolean v) {
        this.transform.setPlayerCreated(v);
        this.playerCreated = v;
    }

    @Override
    public List<NbtPiece<?>> getNbt() {
        var a = super.getNbt();
        a.addAll(pieces);
        return a;
    }
}
