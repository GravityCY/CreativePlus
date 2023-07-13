package me.gravityio.creativeplus.entity;

import me.gravityio.creativeplus.entity.nbt.NbtPiece;

import java.util.List;

public interface ClientEntityFrame {
    List<NbtPiece<?>> getNbt();
}
