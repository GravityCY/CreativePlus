package me.gravityio.creativeplus.api.nbt.pieces;

public interface NbtBoundedPiece<T> extends NbtPiece<T> {

    double getMin();
    double getMax();
}
