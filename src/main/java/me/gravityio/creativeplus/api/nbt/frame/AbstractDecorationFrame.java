package me.gravityio.creativeplus.api.nbt.frame;

import net.minecraft.util.math.BlockPos;

public interface AbstractDecorationFrame {

    BlockPos getTilePos();

    int getTileX();
    int getTileY();
    int getTileZ();

    void setTilePos(BlockPos tilePos);
    void setTileX(int tileX);
    void setTileY(int tileY);
    void setTileZ(int tileZ);

}
