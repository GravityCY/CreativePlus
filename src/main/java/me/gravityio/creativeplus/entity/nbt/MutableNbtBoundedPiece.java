package me.gravityio.creativeplus.entity.nbt;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class MutableNbtBoundedPiece<T> extends MutableNbtPiece<T> implements NbtBoundedPiece<T> {

    double min;
    double max;

    public MutableNbtBoundedPiece(Supplier<T> getter, Consumer<T> setter, Type type, String name, double min, double max) {
        super(getter, setter, type, name);
        this.min = min;
        this.max = max;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public void setMax(double max) {
        this.max = max;
    }

    @Override
    public double getMin() {
        return this.min;
    }

    @Override
    public double getMax() {
        return this.max;
    }

    @Override
    public boolean isBounded() {
        return true;
    }
}
