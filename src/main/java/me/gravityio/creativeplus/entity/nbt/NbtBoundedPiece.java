package me.gravityio.creativeplus.entity.nbt;

public interface NbtBoundedPiece<T> extends NbtPiece<T> {

    double getMin();
    double getMax();
}
