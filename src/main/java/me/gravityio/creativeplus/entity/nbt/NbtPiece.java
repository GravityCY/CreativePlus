package me.gravityio.creativeplus.entity.nbt;

import me.gravityio.creativeplus.entity.ClientEntity;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * An NbtPiece that defines how to set an NbtPiece, and how to get it.<br><br>
 *
 * For example; A use case would be making an NbtPiece for the Invulnerable Tag, essentially acts
 * as a wrapper for a way to set the Invulnerable tag without needing to know what tag it is, what type it is etc.<br><br>
 *
 * Check the {@link ClientEntity ClientEntity} class to see how it's used
 */
public interface NbtPiece<T> {

    default boolean isBounded() {
        return false;
    }

    T get();
    void set(T t);

    Type getType();

    String getName();

    static <T> MutableNbtPiece<T> ofMutable(Supplier<T> getter, Consumer<T> setter, Type type, String name) {
        return new MutableNbtPiece<>(getter, setter, type, name);
    }

    static <T> MutableNbtBoundedPiece<T> ofBoundedMutable(Supplier<T> getter, Consumer<T> setter, Type type, String name, double min, double max) {
        return new MutableNbtBoundedPiece<>(getter, setter, type, name, min, max);
    }

    static <T> NbtBoundedPiece<T> ofBounded(Supplier<T> getter, Consumer<T> setter, Type type, String name, double min, double max) {
        return new NbtBoundedPiece<T>() {
            @Override
            public double getMin() {
                return min;
            }

            @Override
            public double getMax() {
                return max;
            }

            @Override
            public boolean isBounded() {
                return true;
            }

            @Override
            public T get() {
                return getter.get();
            }

            @Override
            public void set(T t) {
                setter.accept(t);
            }

            @Override
            public Type getType() {
                return type;
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }

    static <T> NbtPiece<T> of(Supplier<T> getter, Consumer<T> setter, Type type, String name) {
        return new NbtPiece<>() {
            @Override
            public T get() {
                return getter.get();
            }

            @Override
            public void set(T t) {
                setter.accept(t);
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public Type getType() {
                return type;
            }

        };
    }

    enum Type {
        BOOLEAN, INT, FLOAT, DOUBLE, LONG, STRING, UUID, TEXT
    }
}
