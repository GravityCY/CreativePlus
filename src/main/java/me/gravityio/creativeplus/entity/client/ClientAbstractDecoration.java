package me.gravityio.creativeplus.entity.client;

import me.gravityio.creativeplus.api.nbt.frame.AbstractDecorationFrame;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class ClientAbstractDecoration extends ClientEntity implements AbstractDecorationFrame {

    private final AbstractDecorationEntity transform;
    private final AbstractDecorationEntity dec;

    public ClientAbstractDecoration(AbstractDecorationEntity entity, AbstractDecorationEntity transform, NbtCompound realNbt) {
        super(entity, transform, realNbt);
        
        this.dec = entity;
        this.transform = transform;
    }

    @Override
    public BlockPos getTilePos() {
        return null;
    }

    @Override
    public int getTileX() {
        return 0;
    }

    @Override
    public int getTileY() {
        return 0;
    }

    @Override
    public int getTileZ() {
        return 0;
    }

    @Override
    public void setTilePos(BlockPos tilePos) {

    }

    @Override
    public void setTileX(int tileX) {

    }

    @Override
    public void setTileY(int tileY) {

    }

    @Override
    public void setTileZ(int tileZ) {

    }
}
