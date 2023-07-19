package me.gravityio.creativeplus.api.nbt.frame.living.mob;

import net.minecraft.util.math.BlockPos;

public interface DolphinFrame {
    BlockPos getTreasurePos();
    boolean hasFish();
    int getTreasureX();
    int getTreasureY();
    int getTreasureZ();
    int getMoistness();

    void setTreasurePos(BlockPos pos);
    void setTreasureX(int x);
    void setTreasureY(int y);
    void setTreasureZ(int z);
    void setMoistness(int moistness);
}
