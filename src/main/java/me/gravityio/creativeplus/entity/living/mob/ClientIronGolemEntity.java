package me.gravityio.creativeplus.entity.living.mob;

import me.gravityio.creativeplus.entity.nbt.NbtPiece;
import me.gravityio.creativeplus.lib.EntityNbtHelper.IronGolem;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

/**
 * @see IronGolemEntity#writeCustomDataToNbt
 */
public class ClientIronGolemEntity extends ClientMobEntity {

    private final IronGolemEntity ironGolem;
    boolean playerCreated;

    private String key(String v) {
        return "iron_golem.nbt." + v;
    }

    private final List<NbtPiece<?>> pieces = List.of(
            NbtPiece.of(this::isPlayerCreated, this::setPlayerCreated, NbtPiece.Type.BOOLEAN, key("player_created"))
    );

    public ClientIronGolemEntity(IronGolemEntity ironGolem, NbtCompound realNbt) {
        super(ironGolem, realNbt);
        this.ironGolem = ironGolem;

        this.init();
    }

    private void init() {
        this.playerCreated = this.ironGolem.isPlayerCreated();
    }

    @Override
    public void update() {
        super.update();

        this.playerCreated = super.realNbt.getBoolean(IronGolem.PLAYER_CREATED);
    }

    @Override
    public NbtCompound getOutput() {
        output(IronGolem.PLAYER_CREATED, this.playerCreated, NbtCompound::putBoolean, NbtCompound::getBoolean);

        return super.getOutput();
    }

    public boolean isPlayerCreated() {
        return this.playerCreated;
    }

    public void setPlayerCreated(boolean v) {
        this.playerCreated = v;
    }

    @Override
    public List<NbtPiece<?>> getNbt() {
        var a = super.getNbt();
        a.addAll(pieces);
        return a;
    }
}
