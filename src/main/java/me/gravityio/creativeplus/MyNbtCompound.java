package me.gravityio.creativeplus;

import net.minecraft.nbt.NbtCompound;

public class MyNbtCompound extends NbtCompound {

    @Override
    public String toString() {
        return this.asString();
    }

    @Override
    public String asString() {
        return new MyNbtElementVisitor().apply(this);
    }

    public static MyNbtCompound fromCompound(NbtCompound original) {
        MyNbtCompound nbt = new MyNbtCompound();
        nbt.copyFrom(original);
        return nbt;
    }

}
