package me.gravityio.creativeplus.api.nbt.pieces;

public interface NbtEnumPiece extends NbtPiece<Enum<?>> {
    Class<? extends Enum<?>> getEnumClass();
}
